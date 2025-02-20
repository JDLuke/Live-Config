package com.oopuniversity.liveconfig.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oopuniversity.liveconfig.config.Config;
import com.oopuniversity.liveconfig.config.ConfigItem;
import com.oopuniversity.liveconfig.config.ConfigListener;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import static com.oopuniversity.liveconfig.logging.LogUtil.logError;
import static com.oopuniversity.liveconfig.logging.LogUtil.logMessage;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
public class ConfigController {

    final
    Config config;
    final ConfigListener configListener;

    public final KafkaTemplate<String, String> kafkaTemplate;

    public ConfigController(Config config, KafkaTemplate<String, String> kafkaTemplate, ConfigListener configListener) {
        this.config = config;
        this.kafkaTemplate = kafkaTemplate;
        this.configListener = configListener;
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
        logMessage(this.getClass().getName(), "putConfigValue", new String[]{key, value});
        try {
            kafkaTemplate.send("config", new ObjectMapper().writeValueAsString(new ConfigItem(key, value)));
        } catch (JsonProcessingException e) {
            logError(this.getClass().getName(), "putConfigValue", e);
        }
    }
    /**
     * Get a JSON representation of the entire current application configuration
     * @return A representation of the entire Config object
     */
    @GetMapping(value = "/config/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getConfiguration() {
        if (null == config) {
            return new ResponseEntity<>(NOT_FOUND);
        }
        return new ResponseEntity<>(config.toString(), OK);
    }
}
