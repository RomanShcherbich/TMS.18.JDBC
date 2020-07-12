package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUtils {

    private Properties properties = new Properties();

    public PropertiesUtils() {
        try {
            this.properties.load(new FileInputStream(new File("src/main/resources/application.properties")));
        } catch (IOException e) {
            throw new RuntimeException(
                    String.join("", "Properties has not been loaded ", e.getMessage()));
        }
    }

    public String get(String appPropertyName) {
        return properties.getProperty(appPropertyName);
    }

}
