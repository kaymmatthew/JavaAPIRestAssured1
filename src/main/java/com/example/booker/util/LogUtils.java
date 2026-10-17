package com.example.booker.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LogUtils {
    private LogUtils() { }

    public static Logger logger(Class<?> owner) {
        return LoggerFactory.getLogger(owner);
    }
}
