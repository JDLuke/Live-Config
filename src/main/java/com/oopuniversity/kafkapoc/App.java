package com.oopuniversity.kafkapoc;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oopuniversity.kafkapoc.config.Config;
import com.oopuniversity.kafkapoc.config.ConfigItem;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class App {
    @Bean
    Config getConfig() {
        return new Config();
    }

    @Bean
    public Map<String, String> getAppConfiguration() {
        return new HashMap<>();
    }
    public static void main(String[] args) {

        try {
            System.out.println("Test JSON: " + new ObjectMapper().writeValueAsString(new ConfigItem("key","value")));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        new SpringApplication().run(App.class, args);
    }
}
