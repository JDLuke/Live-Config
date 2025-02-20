package com.oopuniversity.liveconfig.config;

public class ConfigItem {
    private String key;
    private String value;

    public ConfigItem() {
    }

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
