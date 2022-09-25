package com.ssb.dtl.db.syncJob.service;

import com.ssb.dtl.db.syncJob.Constants;
import com.ssb.dtl.db.syncJob.dao.LockerRepository;
import com.ssb.dtl.db.syncJob.domain.JobLock;
import com.ssb.dtl.db.syncJob.schedule.task.DelayTask;
import com.ssb.dtl.db.syncJob.schedule.task.TaskFactory;
import com.ssb.dtl.db.syncJob.schedule.task.TimerQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service("dbLockerService")
@Slf4j
public class LockerServiceImp {
    @Autowired
    private LockerRepository lockerRepository;
    @Autowired
    private TimerQueue timerQueue;
    @Autowired
    private TaskFactory taskFactory;
    @Value("${config.lock.renew.frequency}")
    private Integer renewFrequency;

    @Value("${config.lock.busy.expire}")
    private Integer busyExpire;

    @Transactional
    public boolean tryLock(String name) {
        boolean res = lockerRepository.tryLock(name);
        if(res) {
            log.info("get lock of {} ", name);
            DelayTask task = taskFactory.createLockTask(Constants.SYNC_JOB_LOCK, renewFrequency * 1000, false);
            timerQueue.addTask(task);
        };
        return res;
    }


    @Transactional
    public boolean release(String name) {
        log.info("release lock of {} ", name);
        DelayTask task = DelayTask.map.get(name);
        if (task !=  null){
            task.taskCompleted();
        }
        return lockerRepository.release(name);
    }

    @Transactional List<JobLock> queryOldBusyLock() {
        return lockerRepository.getBusyLocker().stream().peek(x -> log.info("Busy lock {}", x)).filter(
                x -> x.getUpdatedAt().isBefore(OffsetDateTime.now().minusSeconds(busyExpire)))
                .collect(Collectors.toList());
    }
}
