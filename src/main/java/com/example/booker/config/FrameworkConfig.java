package com.example.booker.config;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

public final class FrameworkConfig {
    private static final String CONFIG_DIRECTORY = "config/";
    private static final FrameworkConfig INSTANCE = load();
    private final Properties properties;

    private FrameworkConfig(Properties properties) {
        this.properties = properties;
    }

    public static FrameworkConfig get() {
        return INSTANCE;
    }

    public String baseUrl() {
        return System.getProperty("baseUrl", properties.getProperty("base.url")).replaceAll("/+$", "");
    }

    public Duration connectTimeout() {
        return Duration.ofMillis(Long.parseLong(properties.getProperty("timeout.connect.ms", "5000")));
    }

    public Duration readTimeout() {
        return Duration.ofMillis(Long.parseLong(properties.getProperty("timeout.read.ms", "10000")));
    }

    public long maxResponseTimeMs() {
        return Long.parseLong(System.getProperty("maxResponseTimeMs",
                properties.getProperty("response.time.max.ms", "5000")));
    }

    private static FrameworkConfig load() {
        String environment = System.getProperty("env", "dev").trim().toLowerCase();
        if (!environment.matches("[a-z0-9_-]+")) {
            throw new IllegalArgumentException("Invalid environment name: " + environment);
        }
        Properties properties = new Properties();
        try (InputStream stream = FrameworkConfig.class.getClassLoader()
                .getResourceAsStream(CONFIG_DIRECTORY + environment + ".properties")) {
            if (stream == null) {
                throw new IllegalStateException("No configuration found for environment: " + environment);
            }
            properties.load(stream);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load configuration for " + environment, exception);
        }
        if (properties.getProperty("base.url") == null || properties.getProperty("base.url").isBlank()) {
            throw new IllegalStateException("base.url must be configured for environment: " + environment);
        }
        return new FrameworkConfig(properties);
    }
}
