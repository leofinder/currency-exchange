package com.craftelix.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesUtil {

    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    private PropertiesUtil() {

    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    private static void loadProperties() {
        try (InputStream input = PropertiesUtil.class.getClassLoader().getResourceAsStream("db/liquibase.properties")) {
            PROPERTIES.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
