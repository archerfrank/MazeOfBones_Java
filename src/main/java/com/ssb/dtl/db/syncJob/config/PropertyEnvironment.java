package com.ssb.dtl.db.syncJob.config;

import com.ssb.dtl.db.syncJob.Constants;
import com.ssb.dtl.db.syncJob.schedule.task.DelayTask;
import com.ssb.dtl.db.syncJob.schedule.task.TimerQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class PropertyEnvironment implements ApplicationListener<ContextRefreshedEvent> {
    private final ConfigurableEnvironment configurableEnvironment;
    private final TimerQueue timerQueue;
    private static boolean started = false;
    @Value("${server.port}")
    private String port;

    @Autowired
    public PropertyEnvironment(ConfigurableEnvironment configurableEnvironment,
                               TimerQueue timerQueue) {
        this.configurableEnvironment = configurableEnvironment;
        this.timerQueue = timerQueue;
    }

    /**
     * Process the configuration from table inst_properties
     * @param event
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Constants.port = this.port;
        if(!started) {
            // start the queue consumer
            startDelayQueueDaemon();
            started = true;
        }
    }

    private void startDelayQueueDaemon() {
        log.info("Start the delay task Daemon");
        Thread daemon = new Thread(() -> {
            for(;;){
                Optional<DelayTask> dt = timerQueue.getTask();
                if (dt.isPresent()) {
                    DelayTask task = dt.get();
                    try {
                        if(task.isSync()) task.synchronizedExecute();
                        else task.execute();
                    } catch (Throwable t) {
                        log.error(t.getMessage(), t);
                    }
                }
            }
        });
        daemon.start();
    }
}
