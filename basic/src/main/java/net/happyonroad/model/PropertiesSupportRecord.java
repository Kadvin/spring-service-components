/**
 * Developer: Kadvin Date: 14/12/23 下午3:36
 */
package net.happyonroad.model;

import java.util.Properties;

/**
 * 支持扩展属性的记录
 */
public class PropertiesSupportRecord extends Record{

    // 扩展属性
    private Properties properties;

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getProperty(String name, String defaultValue){
        if( this.properties == null ) return defaultValue;
        return this.properties.getProperty(name, defaultValue);
    }

    public void setProperty(String name, String value) {
        if( this.properties == null ) this.properties = new Properties();
        this.properties.setProperty(name, value);
    }

    public String getProperty(String name){
        return getProperty(name, null);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
