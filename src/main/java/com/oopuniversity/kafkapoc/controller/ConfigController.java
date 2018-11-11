package com.oopuniversity.kafkapoc.controller;

import com.oopuniversity.kafkapoc.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigController {

    @Autowired
    Config config;

    /**
     * Get the value of one configuration entry.
     *
     * @param key
     * @return
     */
    @GetMapping ("/get/config/{key}")
    public String getConfigValueFor(@PathVariable("key") String key) {
        return config.getConfigurationValue(key);
    }

    /**
     * Get a JSON representation of the entire current application configuration
     * @return
     */
    @GetMapping("/get/config")
    public Config getConfiguration() {
        return config;
    }
}
