package net.happyonroad.credential;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.happyonroad.model.Credential;

/**
 * <h1>本机监控方式，只能监控与监控引擎部署在一起的资源</h1>
 *
 * @author Jay Xiong
 */
public class LocalCredential implements Credential {

    private static final long serialVersionUID = -7566065997165248098L;

    @JsonIgnore
    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public String name() {
        return Local;
    }

    public String toString(){
        return "LocalCredential";
    }
}
