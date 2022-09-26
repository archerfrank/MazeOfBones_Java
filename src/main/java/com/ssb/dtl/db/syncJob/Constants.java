package com.ssb.dtl.db.syncJob;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class Constants {
    @Value("${server.port}")
    public static String port;
    static UUID UID = UUID.randomUUID();

    public static String getCurrentIP() {
        return port;
    }

    public static String SYNC_JOB_LOCK = "SYNC_JOB_LOCK";
}
