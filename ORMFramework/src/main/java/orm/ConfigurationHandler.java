package orm;


import lombok.extern.java.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;

/**
 * This class gets information from the configuration file.
 */
@Log
public class ConfigurationHandler {
    /**
     * This method gets for a given key a value from the "orm.properties" file.
     * @param propertyName the key value
     * @return the value for the given key
     * @throws IOException
     */
    public static String getConfigPropertyValue(String propertyName) throws IOException {
        Properties properties = new Properties();
        String fileName = "orm.properties";

        InputStream inputStream = ConfigurationHandler.class.getClassLoader().getResourceAsStream(fileName);

        if(inputStream != null){
            properties.load(inputStream);
            return properties.getProperty(propertyName);
        }

        log.log(Level.WARNING,"Config file '"+fileName+"' not found");
        throw new FileNotFoundException("property file '" + fileName + "' not found in the classpath");
    }
}
