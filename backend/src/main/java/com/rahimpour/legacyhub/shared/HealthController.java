package com.rahimpour.legacyhub.shared;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/api/ping")
    public String ping() {
        return "LegacyHub running";
    }
}
