package com.ssb.dtl.db.syncJob.schedule.task;


import com.ssb.dtl.db.syncJob.dao.LockerRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LockTask extends DelayTask {
    private LockerRepository lockerRepository;
    private TaskFactory taskFactory;
    private TimerQueue timerQueue;

    public void setTimerQueue(TimerQueue timerQueue) {
        this.timerQueue = timerQueue;
    }

    public void setTaskFactory(TaskFactory taskFactory) {
        this.taskFactory = taskFactory;
    }

    public void setLockerRepository(LockerRepository lockerRepository) {
        this.lockerRepository = lockerRepository;
    }

    public LockTask(String name, long delayInMilliseconds, boolean isSync) {
        super(name, delayInMilliseconds, isSync);
    }

    @Override
    protected void execute0() {
        log.info("Now it is going to renew the lock {}", this.name);
        try {
            lockerRepository.renew(this.name);
        } finally {
            DelayTask task = taskFactory.createLockTask(this.name, this.delayInMilliseconds, isSync);
            timerQueue.addTask(task);
        }
    }
}
