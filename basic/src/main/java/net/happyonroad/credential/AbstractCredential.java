package net.happyonroad.credential;

import net.happyonroad.model.Credential;
import org.apache.commons.lang.StringUtils;

/**
 * <h1>抽象的认证信息</h1>
 *
 * @author Jay Xiong
 */
public abstract class AbstractCredential implements Credential {
    private static final long serialVersionUID = -5470080193523796203L;

    private String name;
    private String type;
    private boolean enabled = true;
    private int order;

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

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public boolean support(String type) {
        return StringUtils.equalsIgnoreCase(type, getType());
    }
}
