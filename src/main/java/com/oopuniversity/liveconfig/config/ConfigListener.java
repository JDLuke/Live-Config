package com.oopuniversity.liveconfig.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.oopuniversity.liveconfig.logging.LogUtil.logError;
import static com.oopuniversity.liveconfig.logging.LogUtil.logMessage;
import static java.lang.Long.parseLong;


/**
 * ConfigListener will listen to a Kafka topic in order to build and maintain the configuration
 * information for the module
 */
@Component
public class ConfigListener implements ConsumerSeekAware {
    Long currentKafkaIndex = 0L;
    private ConsumerSeekCallback seekCallback;

    boolean isReady = false;
    public boolean isReady() {
        return isReady;
    }
    //TODO Associate current index with a specific topic

    final Config config;

    public ConfigListener(Config config) {
        this.config = config;
    }

    public void setCurrentKafkaIndex(String topic, int partition, long currentKafkaIndex) {
        this.currentKafkaIndex = currentKafkaIndex;
        seekCallback.seek(topic, partition, currentKafkaIndex);
    }
    ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "${config.startup.topic}")
    public void processMessage(ConsumerRecord<String, String> record) {
        String content = record.value();
        long currentOffset = record.offset();
        int partition = record.partition();
        String topic = record.topic();
        TopicPartition topicPartition = new TopicPartition(topic, partition); // Adjust as needed

        logMessage(this.getClass().getName(), "processMessage", content);
        currentKafkaIndex++;
        try {
            config.setConfigurationValue(objectMapper.readValue(content, ConfigItem.class));
        } catch (Exception e) {
            logError(this.getClass().getName(), "processMessage", e);
        }
        logMessage("Current Offset: " + currentOffset + ", endOffsets: " + endOffsets);
        logMessage("topicPartition: " + topicPartition);
        logMessage("endOffsets.get(topicPartition): " + endOffsets.get(topicPartition));
        if (currentOffset + 1 >= endOffsets.getOrDefault(topicPartition, Long.MAX_VALUE)) {
            System.out.println("Reached the end of the topic.");
            isReady = true;
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
    private final Map<TopicPartition, Long> endOffsets = new ConcurrentHashMap<>();

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
                endOffsets.put(key, assignments.get(key));
                currentKafkaIndex = assignments.get(key);
            }
        }

        if ("End".equalsIgnoreCase(config.getConfigStart())) {
            isReady = true;
            logMessage("Not bothering with any kind of seek.");
        } else if ("Start".equalsIgnoreCase(config.getConfigStart())) {
            logMessage("Seeking to beginning of topic");
            currentKafkaIndex = 0L;
            setCurrentKafkaIndex(config.getTopicName(), 0, currentKafkaIndex);
            isReady = true;
        } else {
            logMessage("Seeking to position <" + config.getConfigStart() + ">");
            long startPosition = calculateStartPositionFromConfiguration(config.getConfigStart());
            setCurrentKafkaIndex(config.getTopicName(), 0, startPosition);
        }

    }

    private long calculateStartPositionFromConfiguration(String configStart) {
        try {
            return parseLong(configStart);
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
    @Override
    public void onIdleContainer(@NonNull  Map<TopicPartition, Long> assignments, @NonNull ConsumerSeekCallback callback) {
        logMessage(this.getClass().getName(), "onIdleContainer", new Object[]{assignments, callback});
    }
}
