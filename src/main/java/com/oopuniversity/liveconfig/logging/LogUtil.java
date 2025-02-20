package com.oopuniversity.liveconfig.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// This is not a full-fledged adapter but it's good enough for my purposes.
@Component
public class LogUtil {
    final static private Logger logger = LoggerFactory.getLogger("Live Config");
    final static private ObjectMapper objectMapper = new ObjectMapper();

    public static void logError(String className, String methodName, Exception e) {
        logger.error("Class " + className + ", Method: " + methodName, e);
    }

    public static void logMessage(String message) {
        logger.info(message);
    }

    public static void logMessage(String className, String methodName, Object message) {
        try {
            logger.info(className + ", Method: " + methodName + ", Message: " + objectMapper.writeValueAsString((message)));
        } catch (JsonProcessingException e) {
            logger.error(className, methodName, e);
        }
    }
}
