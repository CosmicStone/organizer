package com.organizer.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Loggers {
    private Loggers() {
    }

    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    public static Logger getLogger(String name) {
        return LoggerFactory.getLogger(name);
    }
}
