package org.expr.juliacaller;

import java.io.*;
import java.util.Properties;

public class Constants {

    public final static String JULIA_PATH = "JULIA_PATH";
    public final static String JULIA_PORT = "JULIA_PORT";
    public final static String VERBOSE = "VERBOSE";
    public final static String TRUE = "true";
    public final static String FALSE = "false";
    public final static String JULIA_ERROR_CONSOLE = "JULIA_ERROR_CONSOLE";

    public static Properties properties;
    private final static String propertiesFileName = "juliacaller.properties";

    static {
        properties = new Properties();
        try {
            properties.load(new FileReader(new File(propertiesFileName)));
            System.out.println("Loaded Properties: " + properties);
        } catch (Exception fnte) {
            createEmptyProperties();
        }
    }

    public static void setProperties(String key, String value) {
        properties.setProperty(key, value);
        save();
    }

    public static void save() {
        try {
            properties.store(new FileWriter(propertiesFileName), "Created by JuliaCaller");
        } catch (Exception e) {
            System.err.println("* Error writing properties file" + propertiesFileName);
        }
    }

    public static void createEmptyProperties() {
        properties.setProperty(Constants.JULIA_PATH, "julia");
        properties.setProperty(Constants.JULIA_PORT, "8000");
        properties.setProperty(Constants.VERBOSE, TRUE);
        properties.setProperty(Constants.JULIA_ERROR_CONSOLE, TRUE);
        save();
    }
}
