package com.oopuniversity.kafkapoc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.logging.Logger;


/**
 * ConfigListener will listen to a Kafka topic in order to build and maintain the configuration
 * information for the module
 */
@Component
public class ConfigListener implements ConsumerSeekAware {
    @Autowired
    Config config;

    //private ConsumerSeekCallback seekCallback;
    private Logger logger = Logger.getLogger(Config.class.getName());
    @Autowired
    KafkaTemplate kafkaTemplate;
    @KafkaListener(topics = "config")
    public void processMessage(String content) {

        logger.entering(this.getClass().getName(), "processMessage", content);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            config.setConfigurationValue(objectMapper.readValue(content,ConfigItem.class));
        } catch (Exception e) {
            logger.finer("Invalid configuration parameter found on queue: <" + content + ">");
        }
        logger.exiting(this.getClass().getName(), "processMessage");
    }


    /**
     * Register the callback to use when seeking at some arbitrary time. When used with a
     * {@code ConcurrentMessageListenerContainer} or the same listener instance in multiple
     * containers listeners should store the callback in a {@code ThreadLocal}.
     *
     * @param callback the callback.
     */
    @Override
    public void registerSeekCallback(ConsumerSeekCallback callback) {
        System.out.println("registerSeekCallback: " + callback.getClass().getSimpleName());

        //this.seekCallback = callback;
    }

    /**
     * When using group management, called when partition assignments change.
     *
     * @param assignments the new assignments and their current offsets.
     * @param callback    the callback to perform an initial seek after assignment.
     */
    @Override
    public void onPartitionsAssigned(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
        logger.info("onPartitionsAssigned: " + assignments.toString() + ", " + callback.getClass().getSimpleName());
        logger.info("Value of configStart is " + config.getConfigStart());
        logger.info("Current positions are:");
        for (TopicPartition key : assignments.keySet()) {
            logger.info(key.topic() + "=<" + assignments.get(key) + ">");
        }
        if ("End".equalsIgnoreCase(config.getConfigStart())) {
            logger.info("Not bothering with any kind of seek.");
        } else if ("Start".equalsIgnoreCase(config.getConfigStart())) {
            logger.info("Seeking to beginning of topic");
            callback.seekToBeginning("config", 0);
        } else {
            logger.info("Seeking to position <" + config.getConfigStart() + ">");
            callback.seek("config", 0, calculateStartPositionFromConfiguration(config.getConfigStart()));
        }

    }

    private int calculateStartPositionFromConfiguration(String configStart) {
        try {
            Integer integer = new Integer(configStart);
            return integer.intValue();
        } catch (NumberFormatException nfe) {
            logger.throwing(ConfigListener.class.getName(), "calculateStartPositionFromConfiguration", nfe);
        }
        return 0;
    }

    /**
     * If the container is configured to emit idle container events, this method is called
     * when the container idle event is emitted - allowing a seek operation.
     *
     * @param assignments the new assignments and their current offsets.
     * @param callback    the callback to perform a seek.
     */
    @Override
    public void onIdleContainer(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
        System.out.println("onIdleContainer: " + assignments.toString() + ", " + callback.getClass().getSimpleName());

    }
}
