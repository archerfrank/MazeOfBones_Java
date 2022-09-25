package com.ssb.dtl.db.syncJob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SyncJobApplication {

	public static void main(String[] args) {
		SpringApplication.run(SyncJobApplication.class, args);
	}

}
