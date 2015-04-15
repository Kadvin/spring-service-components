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

    @Override
    public String name() {
        return getProperty("name");
    }
}
