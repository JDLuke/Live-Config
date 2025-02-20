package com.oopuniversity.liveconfig.config;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

import static com.oopuniversity.liveconfig.logging.LogUtil.logError;
import static com.oopuniversity.liveconfig.logging.LogUtil.logMessage;

public class Config {

    private final Map<String,String> appConfiguration;
    final private ObjectMapper objectMapper;

    @Value("${config.startup.topic}")
    private String topicName;

    public Config(Map<String, String> appConfiguration, ObjectMapper objectMapper) {
        this.appConfiguration = appConfiguration;
        this.objectMapper = objectMapper;
    }

    public String getTopicName() {
        return topicName;
    }
    @Value("${config.startup.seek}")
    private String configStart;

    public String getConfigStart() {
        return configStart;
    }
    /**
     *
     * @return A Map containing the entire current set of configuration entries
     *
     */

    public String getConfigurationValue(String key) {
        String value;
        value = appConfiguration.get(key);
        logMessage("Returning configuration value for key '" + key + "' as '" + value + "'");
        return value;
    }


    void setConfigurationValue(ConfigItem value) {
        logMessage("Setting configuration key <" + value.getKey() + "> to <" + value.getValue() + ">");
        if (value.getValue().isEmpty()) {
            logMessage("Deleting...");
            appConfiguration.remove(value.getKey());
        } else {
            logMessage("Updating...");
            appConfiguration.put(value.getKey(), value.getValue());
        }
        logMessage(this.getClass().getName() + " - setConfigurationValue");
    }

    public String toString() {
        try {
            return objectMapper.writeValueAsString(appConfiguration);
        } catch (JsonProcessingException e) {
            logError(this.getClass().getSimpleName(), "toString", e);
        }
        return "Error";
    }

}
