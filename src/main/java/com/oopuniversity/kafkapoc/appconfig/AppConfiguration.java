package com.oopuniversity.kafkapoc.appconfig;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

    @Bean
    public HealthIndicator helloHealthIndicator() {
        return () -> Health.up().withDetail("config", "service").build();

    }

}
