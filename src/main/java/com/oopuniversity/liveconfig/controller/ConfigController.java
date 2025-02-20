package com.oopuniversity.liveconfig.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oopuniversity.liveconfig.config.Config;
import com.oopuniversity.liveconfig.config.ConfigItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
public class ConfigController {

    final
    Config config;

    public final KafkaTemplate<String, String> kafkaTemplate;
    private final Logger logger = Logger.getLogger("config");

    public ConfigController(Config config, KafkaTemplate kafkaTemplate) {
        this.config = config;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Get the value of one configuration entry.
     *
     * @param key The name of the configuration property
     * @return A String value containing the value of the configuration property if found.
     * If there is no such property return an empty string
     */
    @GetMapping(value = "/config/get/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getConfigValueFor(@PathVariable("key") String key) {
        return config.getConfigurationValue(key);
    }

    /**
     * RESTful interface to set a configuration value
     * <p>
     * Please note:  This does NOT directly update the internal data store.  It simply pushes the desired
     * value onto the config topic and leaves the rest up to the ecosystem.
     *
     * @param key   Name of the configuration value to be set
     * @param value Value to be set
     */
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
    @GetMapping(value = "/config/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getConfiguration() {
        return config.toString();
    }
}
