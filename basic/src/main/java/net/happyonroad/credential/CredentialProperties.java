/**
 * Developer: Kadvin Date: 14-6-16 下午5:53
 */
package net.happyonroad.credential;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.happyonroad.model.Credential;

import java.util.Properties;

/**
 * Properties which implements Credential interface
 */
public class CredentialProperties extends Properties implements Credential {
    private static final long serialVersionUID = -5943166126247555544L;

    @JsonIgnore
    @Override
    public int getOrder() {
        return 40;
    }

    @Override
    public String getName() {
        return getProperty("name");
    }

    public void setName(String name){
        setProperty("name", name);
    }

    @Override
    public String getType() {
        return getProperty("type");
    }

    public void setType(String type){
        setProperty("type", type);
    }

    @Override
    public boolean isEnabled() {
        return Boolean.valueOf(getProperty("enabled", "true"));
    }

    public void setEnabled(boolean enabled){
        setProperty("enabled", String.valueOf(enabled));
    }
}
