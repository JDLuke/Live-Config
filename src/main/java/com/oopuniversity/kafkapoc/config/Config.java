package com.oopuniversity.kafkapoc.config;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;
import java.util.logging.Logger;

public class Config {
    @Autowired
    Map<String,String> appConfiguration;

    @Value("${config.startup.seek}")
    private String configStart;

    private Logger logger = Logger.getLogger(Config.class.getName());

    public String getConfigStart() {
        return configStart;
    }
    /**
     * If I remove this it's no longer a Bean that Spring can play with...
     * <p>
     * However, I'm not really a fan of exposing it this way.
     *
     * @return
     * @TODO Get rid of this tech debt
     */
    public Map<String,String> getAppConfiguration() {
        return appConfiguration;
    }

    public String getConfigurationValue(String key) {
        String value;
        value = appConfiguration.get(key);
        logger.config("Returning configuration value for key '" + key + "' as '" + value + "'");
        return value;
    }

    protected void setConfigurationValue(ConfigItem value) {
        logger.config("Setting configuration key <" + value.getKey() + "> to <" + value.getValue() + ">");
        if (value.getValue().length() == 0) {
            logger.config("Deleting...");
            appConfiguration.remove(value.getKey());
        } else {
            logger.config("Updating...");
            appConfiguration.put(value.getKey(), value.getValue());
        }
        logger.exiting(this.getClass().getName(), "setConfigurationValue");
    }

    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(appConfiguration);
        } catch (JsonProcessingException e) {
            logger.throwing(this.getClass().getName(), "toString", e);
        }
        return "Error";
    }
}
