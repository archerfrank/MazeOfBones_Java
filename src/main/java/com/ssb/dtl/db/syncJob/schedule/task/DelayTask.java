package com.ssb.dtl.db.syncJob.schedule.task;

import com.google.common.primitives.Ints;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**

 Not Thread-safe by default.
 */
@ToString
@Slf4j
public abstract class DelayTask implements Delayed {
    public static ConcurrentHashMap<String, DelayTask> map = new ConcurrentHashMap<>();
    protected String name;
    protected long startTime;
    protected long delayInMilliseconds;
    private volatile boolean completed = false;
    protected boolean isSync = false;

    public DelayTask(String name, long delayInMilliseconds, boolean isSync) {
        this.name = name;
        this.startTime = System.currentTimeMillis() + delayInMilliseconds;
        this.delayInMilliseconds = delayInMilliseconds;
        this.isSync = isSync;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = startTime - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return Ints.saturatedCast(
                this.startTime - ((DelayTask) o).startTime);
    }

    public void execute() {
        if (!this.completed) {
            log.info("execute the task {}", this.name);
            execute0();
        } else {
            log.info("skip the task {}", this.getName());
        }
        completed = true;
    }

    public synchronized void synchronizedExecute() {
        execute();
    }

    abstract protected void execute0();

    public String getName() {
        return name;
    }

    public boolean isSync() {
        return isSync;
    }

    public void taskCompleted() {
        this.completed = true;
    }
}
