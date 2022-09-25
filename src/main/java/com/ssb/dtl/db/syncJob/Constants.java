package com.ssb.dtl.db.syncJob;

import java.util.UUID;

public class Constants {
    static UUID UID = UUID.randomUUID();
    public static String getCurrentIP() {
        return UID.toString();
    }
    public static String SYNC_JOB_LOCK = "SYNC_JOB_LOCK";
}
