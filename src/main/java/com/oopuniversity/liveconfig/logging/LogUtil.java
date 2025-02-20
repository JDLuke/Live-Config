package com.oopuniversity.liveconfig.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// This is not a full-fledged adapter but it's good enough for my purposes.
public class LogUtil {
    static Logger logger = LoggerFactory.getLogger("Live Config");

    public static void logError(String className, String methodName, Exception e) {
        logger.error("Class " + className + ", Method: " + methodName, e);
    }

    public static void logMessage(String message) {
        logger.info(message);
    }
}
