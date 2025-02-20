package com.oopuniversity.liveconfig.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ConfigConfig {
    @Bean
    ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
    Config getConfig() {
        return new Config(getAppConfiguration(), getObjectMapper());
    }

    @Bean
    Map<String, String> getAppConfiguration() {
        return new HashMap<>();
    }
}
