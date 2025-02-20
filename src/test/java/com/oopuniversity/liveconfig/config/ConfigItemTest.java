package com.oopuniversity.liveconfig.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigItemTest {

    @Test
    void Constructor_leaves_nulls_in_an_empty_ConfigItem() {
        ConfigItem configItem = new ConfigItem();
        assertNull(configItem.getKey());
        assertNull(configItem.getValue());
    }

    @Test
    void Constructor_creates_expected_values_in_a_ConfigItem() {
        ConfigItem configItem = new ConfigItem("key1", "value1");
        assertEquals("key1", configItem.getKey());
        assertEquals("value1", configItem.getValue());
    }
}