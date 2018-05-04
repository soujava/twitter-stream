package br.org.soujava.twitter.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropsUtils {

    static Properties prop = new Properties();

    public PropsUtils() {
        try {
            prop.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Get a property value as int
     * @param propertyName
     * @param defaultValue
     * @return
     */
    public int getPropertyValueInt(String propertyName, int defaultValue) {
        String propertyValue = prop.getProperty(propertyName, String.valueOf(defaultValue));
        if (isNumeric(propertyValue)) {
            return Integer.parseInt(propertyValue);
        } else {
            return defaultValue;
        }
    }

    /**
     * Get a property value as string
     * @param propertyName
     * @param defaultValue
     * @return
     */
    public String getPropertyValueString(String propertyName, String defaultValue) {

        String propertyValue = prop.getProperty(propertyName, defaultValue);
        return propertyValue;
    }

    /**
     * check if field is number
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }
}
