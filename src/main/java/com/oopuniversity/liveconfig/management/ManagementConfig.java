package com.oopuniversity.liveconfig.management;

import com.oopuniversity.liveconfig.config.ConfigListener;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ManagementConfig {
    private final ConfigListener configListener;
    public ManagementConfig(ConfigListener configListener) {
        this.configListener = configListener;
    }

    @Bean
    public HealthIndicator helloHealthIndicator() {
        return () -> {
            if (configListener.isReady())
                return Health.up().withDetail("config", "ready").build();
            else {
                return Health.down().withDetail("config", "unavailable").build();
            }
        };
    }
}
