package com.oopuniversity.kafkapoc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ConfigConfig {
    @Bean
    Config getConfig() {
        return new Config();
    }

    @Bean
    Map<String, String> getAppConfiguration() {
        return new HashMap<>();
    }

}
