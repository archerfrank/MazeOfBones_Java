package com.ssb.dtl.db.syncJob.schedule.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.DelayQueue;

@Component(value = "timerQueue")
@Slf4j
public class TimerQueue {
    private DelayQueue<DelayTask> queue = new DelayQueue<>();

    public TimerQueue addTask(DelayTask task) {
        log.info("add delay task {}", task);
        DelayTask.map.put(task.getName(), task);
        queue.put(task);
        return this;
    }

    public Optional<DelayTask> getTask(){
        try {
            return Optional.of(queue.take());
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return Optional.ofNullable(null);
        }
    }
}
