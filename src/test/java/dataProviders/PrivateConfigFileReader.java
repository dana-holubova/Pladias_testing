package dataProviders;

import java.io.*;
import java.util.*;

public class PrivateConfigFileReader {

    private Properties properties;
    private final String propertyFilePath = "configs//PrivateConfiguration.properties";

    public PrivateConfigFileReader() {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(propertyFilePath));
            properties = new Properties();
            try {
                properties.load(reader);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Configuration.properties not found at " + propertyFilePath);
        }
    }

    public String getPladiasLogin() {
        String pladiasLogin = properties.getProperty("pladiasLogin");
        if (pladiasLogin != null) return pladiasLogin;
        else throw new RuntimeException("pladiasLogin not specified in the Configuration.properties file.");
    }

    public String getPladiasPassword() {
        String pladiasPassword = properties.getProperty("pladiasPassword");
        if (pladiasPassword != null) return pladiasPassword;
        else throw new RuntimeException("pladiasPassword not specified in the Configuration.properties file.");
    }


}

