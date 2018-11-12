package com.oopuniversity.liveconfig.management;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ManagementConfig {
    @Bean
    public HealthIndicator helloHealthIndicator() {
        return () -> Health.up().withDetail("config", "service").build();
    }
}
