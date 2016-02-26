/**
 * Developer: Kadvin Date: 14-6-16 下午5:53
 */
package net.happyonroad.credential;

import net.happyonroad.model.Credential;

import java.util.Properties;

/**
 * Properties which implements Credential interface
 */
public class CredentialProperties extends Properties implements Credential {
    private static final long serialVersionUID = -5943166126247555544L;

    public CredentialProperties() {
        //for mongo db
        setProperty("_class", getClass().getName());
    }

    @Override

    public int getOrder() {
        return 40;
    }

    public void setOrder(int order) {
        setProperty("order", String.valueOf(order));
    }

    @Override
    public String getName() {
        return getProperty("name");
    }

    public void setName(String name) {
        setProperty("name", name);
    }

    @Override
    public String getType() {
        return getProperty("type");
    }

    public void setType(String type) {
        setProperty("type", type);
    }

    @Override
    public boolean isEnabled() {
        return Boolean.valueOf(getProperty("enabled", "true"));
    }

    public void setEnabled(boolean enabled) {
        setProperty("enabled", String.valueOf(enabled));
    }
}
