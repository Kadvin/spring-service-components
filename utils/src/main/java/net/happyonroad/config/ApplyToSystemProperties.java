/**
 * @author XiongJie, Date: 13-12-5
 */
package net.happyonroad.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.Lifecycle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Apply configured properties to system property
 */
public class ApplyToSystemProperties implements Lifecycle, FilenameFilter{
    private static final Pattern INTERPOLATE_PTN = Pattern.compile("[#|$]\\{([^}]+)\\}");
    Logger logger = LoggerFactory.getLogger(ApplyToSystemProperties.class);
    private boolean running;

    @Override
    public void start() {
        running = true;
        Properties properties = listProperties(System.getProperty("app.home"));
        // support value with ${variable} or #{variable}
        Enumeration<?> en = properties.propertyNames();
        while (en.hasMoreElements()) {
          String key = (String) en.nextElement();
          String value = properties.getProperty(key);
          String newValue = interpolate(value, properties);
            properties.setProperty(key, newValue);
        }
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

    public String interpolate(String origin, Properties props) {
        Matcher m = INTERPOLATE_PTN.matcher(origin);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String variable = m.group(1);
            String replacement = props.getProperty(variable);
            if (replacement == null) replacement = System.getProperty(variable);
            if (replacement == null) replacement = System.getenv(variable);
            if (replacement == null) {
                System.err.println("Undefined variable ${" + variable + "} " +
                                   " in current properties file or system properties/evn" );
                continue;
            }
            //解析出来的变量可能还需要再解析
            if (INTERPOLATE_PTN.matcher(replacement).find()) {
                replacement = interpolate(replacement, props);
            }
            try {
                m.appendReplacement(sb, replacement);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();//just for catch it to debug
                throw e;
            }
        }
        m.appendTail(sb);
        return sb.toString().trim();
    }
}
