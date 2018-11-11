package com.oopuniversity.kafkapoc.config;

import org.springframework.stereotype.Component;

@Component
public class ConfigItem {
    public String key;
    public String value;

    public ConfigItem() {};

    public ConfigItem(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
