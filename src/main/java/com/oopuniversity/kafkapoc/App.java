package com.oopuniversity.kafkapoc;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oopuniversity.kafkapoc.config.ConfigItem;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.oopuniversity.kafkapoc")
public class App {
    public static void main(String[] args) {

        try {
            System.out.println("Test JSON: " + new ObjectMapper().writeValueAsString(new ConfigItem("key","value")));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        SpringApplication.run(App.class, args);
    }
}
