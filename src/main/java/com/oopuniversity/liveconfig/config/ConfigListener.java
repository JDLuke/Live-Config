package com.oopuniversity.liveconfig.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.oopuniversity.liveconfig.logging.LogUtil.logError;
import static com.oopuniversity.liveconfig.logging.LogUtil.logMessage;
import static java.lang.Integer.parseInt;


/**
 * ConfigListener will listen to a Kafka topic in order to build and maintain the configuration
 * information for the module
 */
@Component
public class ConfigListener implements ConsumerSeekAware {
    int currentKafkaIndex = 0;
    private ConsumerSeekCallback seekCallback;

    //TODO Associate current index with a specific topic

    final
    Config config;

    public ConfigListener(Config config) {
        this.config = config;
    }

    public void setCurrentKafkaIndex(String topic, int partition, int currentKafkaIndex) {
        this.currentKafkaIndex = currentKafkaIndex;
        seekCallback.seek(topic, partition, currentKafkaIndex);
    }

    @KafkaListener(topics = "${config.startup.topic}")
    public void processMessage(String content) {
        logMessage(this.getClass().getName(), "processMessage", content);
        currentKafkaIndex++;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            config.setConfigurationValue(objectMapper.readValue(content, ConfigItem.class));
        } catch (Exception e) {
            logError(this.getClass().getName(), "processMessage", e);
        }
        logMessage(this.getClass().getName(), "processMessage", "exiting");
    }


    /**
     * Register the callback to use when seeking at some arbitrary time. When used with a
     * {@code ConcurrentMessageListenerContainer} or the same listener instance in multiple
     * containers listeners should store the callback in a {@code ThreadLocal}.
     *
     * @param callback the callback.
     */
    @Override
    public void registerSeekCallback(@NonNull ConsumerSeekCallback callback) {
        logMessage(this.getClass().getName(), "registerSeekCallback", callback);

        logMessage("Storing ConsumerSeekCallback for future use.");
        this.seekCallback = callback;
    }

    /**
     * When using group management, called when partition assignments change.
     *
     * @param assignments the new assignments and their current offsets.
     * @param callback    the callback to perform an initial seek after assignment.
     */
    @Override
    public void onPartitionsAssigned(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
        logMessage("onPartitionsAssigned: " + assignments.toString() + ", " + callback.getClass().getSimpleName());
        logMessage("Value of configStart is " + config.getConfigStart());
        logMessage("Current positions are:");
        for (TopicPartition key : assignments.keySet()) {
            logMessage(key.topic() + "=<" + assignments.get(key) + ">");
            if (config.getTopicName().equals(key.topic())) {
                currentKafkaIndex = assignments.get(key).intValue();
            }
        }

        if ("End".equalsIgnoreCase(config.getConfigStart())) {
            logMessage("Not bothering with any kind of seek.");
        } else if ("Start".equalsIgnoreCase(config.getConfigStart())) {
            logMessage("Seeking to beginning of topic");
            setCurrentKafkaIndex(config.getTopicName(), 0, 0);
            currentKafkaIndex = 0;
        } else {
            logMessage("Seeking to position <" + config.getConfigStart() + ">");
            int startPosition = calculateStartPositionFromConfiguration(config.getConfigStart());
            setCurrentKafkaIndex(config.getTopicName(), 0, startPosition);
        }

    }

    private int calculateStartPositionFromConfiguration(String configStart) {
        try {
            return parseInt(configStart);
        } catch (NumberFormatException nfe) {
            logError(ConfigListener.class.getName(), "calculateStartPositionFromConfiguration", nfe);
        }
        return 0;
    }

//    /**
//     * If the container is configured to emit idle container events, this method is called
//     * when the container idle event is emitted - allowing a seek operation.
//     *
//     * @param assignments the new assignments and their current offsets.
//     * @param callback    the callback to perform a seek.
//     */
//    @Override
//    public void onIdleContainer(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
//        logger.entering(this.getClass().getName(), "onIdleContainer", new Object[]{assignments, callback});
//
//    }
}
