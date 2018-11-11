package com.oopuniversity.kafkapoc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oopuniversity.kafkapoc.config.Config;
import com.oopuniversity.kafkapoc.config.ConfigItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
public class ConfigController {

    @Autowired
    Config config;

    @Autowired
    public KafkaTemplate kafkaTemplate;
    private Logger logger = Logger.getLogger("config");

    /**
     * Get the value of one configuration entry.
     *
     * @param key The name of the configuration property
     * @return A String value containing the value of the configuration property if found.
     * If there is no such property return an empty string
     */
    @GetMapping("/config/get/{key}")
    public String getConfigValueFor(@PathVariable("key") String key) {
        return config.getConfigurationValue(key);
    }

    @PostMapping("/config/set/{key}/{value}")
    public void putConfigValue(@PathVariable("key") String key, @PathVariable("value") String value) {
        //Do NOT set configuration directly, stick it onto a Kafka stream.
        logger.entering(this.getClass().getName(), "putConfigValue", new String[]{key, value});
        try {
            kafkaTemplate.send("config", new ObjectMapper().writeValueAsString(new ConfigItem(key, value)));
        } catch (JsonProcessingException e) {
            logger.throwing(this.getClass().getName(), "putConfigValue", e);
        }
    }
    /**
     * Get a JSON representation of the entire current application configuration
     * @return A representation of the entire Config object
     */
    @GetMapping("/config/get")
    public Config getConfiguration() {
        return config;
    }
}
