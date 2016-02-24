/**
 * Developer: Kadvin Date: 14/12/23 下午3:36
 */
package net.happyonroad.model;

import java.util.Properties;

/**
 * 支持扩展属性的记录
 */
public class PropertiesSupportRecord<T> extends Record<T>{

    private static final long serialVersionUID = -8053789424127958797L;
    // 扩展属性
    private Properties properties;

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        if( properties != null ){
            if( this.properties == null )
                this.properties = new Properties();
            this.properties.putAll(properties);
        }else{
            this.properties = null;
        }
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
        PropertiesSupportRecord cloned = (PropertiesSupportRecord) super.clone();
        if( this.properties != null ) {
            cloned.properties = new Properties();
            cloned.properties.putAll(this.properties);
        }
        return cloned;
    }
}
