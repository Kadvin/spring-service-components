package net.happyonroad.credential;

import net.happyonroad.model.Credential;

/**
 * <h1>抽象的认证信息</h1>
 *
 * @author Jay Xiong
 */
public abstract class AbstractCredential implements Credential {
    private String  name;
    private String  type;
    private boolean enabled = true;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
