package com.ssb.dtl.db.syncJob.schedule.task;

import com.ssb.dtl.db.syncJob.dao.LockerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskFactory {
    @Autowired
    LockerRepository lockerRepository;
    @Autowired
    TimerQueue timerQueue;
    public DelayTask createLockTask(String name, long delayInMilliseconds, boolean isSync) {
        LockTask task = new LockTask(name, delayInMilliseconds, isSync);
        task.setLockerRepository(lockerRepository);
        task.setTaskFactory(this);
        task.setTimerQueue(timerQueue);
        return task;
    }
}
