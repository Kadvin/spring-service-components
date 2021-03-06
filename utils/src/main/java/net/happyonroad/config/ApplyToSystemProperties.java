/**
 * @author XiongJie, Date: 13-12-5
 */
package net.happyonroad.config;

import net.happyonroad.util.StringUtils;
import net.happyonroad.util.VariableResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.Lifecycle;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Apply configured properties to system property
 */
@ManagedResource(objectName = "net.happyonroad:type=service,name=configProperties")
public class ApplyToSystemProperties implements Lifecycle, FilenameFilter {
    private static final Pattern INTERPOLATE_PTN = Pattern.compile("[#|$]\\{([^}]+)\\}");
    Logger logger = LoggerFactory.getLogger(ApplyToSystemProperties.class);
    private boolean    running;
    private Properties properties;

    @Override
    public void start() {
        running = true;
        Map<String, Object> map = new HashMap<String, Object>();
        properties = listProperties(System.getProperty("app.home"));
        //add properties to map
        Set<String> propertyNames = properties.stringPropertyNames();
        for (String propertyName : propertyNames) {
            map.put(propertyName, properties.getProperty(propertyName));
        }
        //add system properties to map
        propertyNames = System.getProperties().stringPropertyNames();
        for (String propertyName : propertyNames) {
            map.put(propertyName, System.getProperty(propertyName));
        }
        //Evaluate properties by context map
        VariableResolver resolver = new VariableResolver.MapResolver(map);
        resolver = new VariableResolver.DefaultAwareResolver(resolver);
        // support value with ${variable} or #{variable}
        Enumeration<?> en = properties.propertyNames();
        while (en.hasMoreElements()) {
            String key = (String) en.nextElement();
            String value = properties.getProperty(key);
            String newValue = interpolate(value, resolver);
            properties.setProperty(key, newValue);
        }
        // Set properties to system, or update itself by system
        applyProperties(properties);
    }

    @Override
    public boolean accept(File dir, String name) {
        String configFile = System.getProperty("app.config");
        if (StringUtils.isBlank(configFile))
            return name.endsWith(".properties");
        else {
            return name.equalsIgnoreCase(configFile);
        }
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
                logger.error("Can't load properties from: " + propertyFile.getPath(), e);
            }
        }
        return properties;
    }

    @ManagedAttribute
    public Properties getProperties() {
        return properties;
    }

    private void applyProperties(Properties properties) {
        Set<String> names = properties.stringPropertyNames();
        for (String name : names) {
            String property = properties.getProperty(name);
            String exist = System.getProperty(name);
            if (StringUtils.isBlank(exist)) {
                //配置文件不能覆盖用户通过命令行 -D 定义过的系统属性
                System.setProperty(name, property);
            } else {
                logger.debug("{}={} is shield by system, value = {}", name, property, exist);
                properties.setProperty(name, exist);
            }
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

    public String interpolate(String origin, VariableResolver resolver) {
        return StringUtils.interpolate(origin, INTERPOLATE_PTN, resolver);
    }
}
