package com.matletik.juliacaller;

import java.io.*;
import java.util.Properties;

public class Constants {

    public final static String JULIA_PATH = "JULIA_PATH";
    public final static String JULIA_PORT= "JULIA_PORT";

    public static Properties properties;
    private final static String propertiesFileName = "juliacaller.properties";

    static {
        properties = new Properties();
        try {
            properties.load(new FileReader(new File(propertiesFileName)));
        }catch (Exception fnte){
            createEmptyProperties();
        }
    }

    public static void setProperties(String key, String value){
        properties.setProperty(key, value);
        save();
    }

    public static void save(){
        try{
            properties.store(new FileWriter(propertiesFileName), "Created by JuliaCaller");
        }catch (Exception e){
            System.err.println("* Error writing properties file" + propertiesFileName);
        }
    }


    public static void createEmptyProperties(){
        properties.setProperty(Constants.JULIA_PATH, "julia");
        properties.setProperty(Constants.JULIA_PORT, "8000");
        save();
    }
}
