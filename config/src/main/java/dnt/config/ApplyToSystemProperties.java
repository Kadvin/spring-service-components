/**
 * @author XiongJie, Date: 13-12-5
 */
package dnt.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.Lifecycle;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

/**
 * Apply configured properties to system property
 */
@Component
public class ApplyToSystemProperties implements Lifecycle, FilenameFilter{
    Logger logger = LoggerFactory.getLogger(ApplyToSystemProperties.class);
    private boolean running;

    @Override
    public void start() {
        running = true;
        Properties properties = listProperties(System.getProperty("app.home"));
        applyProperties(properties);
    }

    @Override
    public boolean accept(File dir, String name) {
        return name.endsWith(".properties");
    }

    private Properties listProperties(String home) {
        Properties properties = new Properties();
        if (home == null) {
            logger.warn("There is no app.home in system properties");
            return properties;
        }
        File config = new File(home, "config");
        File[] propertyFiles = config.listFiles(this);
        for (File propertyFile : propertyFiles) {
            try {
                FileInputStream fis = new FileInputStream(propertyFile);
                try {
                    properties.load(fis);
                } finally {
                    fis.close();
                }
            } catch (IOException e) {
                logger.error("Can't load properties from: "+ propertyFile.getPath(), e);
            }
        }
        return properties;
    }

    private void applyProperties(Properties properties) {
        Set<String> names = properties.stringPropertyNames();
        for (String name : names) {
            String property = properties.getProperty(name);
            String exist = System.getProperty(name);
            if(exist != null && !exist.equals(property)){
                logger.warn("Override System Property {}: {} => {}", name, exist, property);
            }else{
                logger.trace("Set System Property {} = {}", name, property);
            }
            System.setProperty(name, property);
        }
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

}
