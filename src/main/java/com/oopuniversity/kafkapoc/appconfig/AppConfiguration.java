package com.oopuniversity.kafkapoc.appconfig;

import com.oopuniversity.kafkapoc.config.Config;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AppConfiguration {
    @Bean
    Config getConfig() {
        return new Config();
    }

    @Bean
    Map<String, String> getAppConfiguration() {
        return new HashMap<>();
    }

    @Bean
    public HealthIndicator helloHealthIndicator() {
        return () -> Health.up().withDetail("config", "service").build();

    }

}
