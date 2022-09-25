package com.ssb.dtl.db.syncJob.controller;

import com.ssb.dtl.db.syncJob.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Version {
    @GetMapping("version")
    public String getVersion() {
        return Constants.getCurrentIP();
    }
}
