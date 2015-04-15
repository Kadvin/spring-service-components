package net.happyonroad.credential;

import net.happyonroad.model.Credential;

/**
 * <h1>本机监控方式，只能监控与监控引擎部署在一起的资源</h1>
 *
 * @author Jay Xiong
 */
public class LocalCredential implements Credential {

    private static final long serialVersionUID = -7566065997165248098L;

    @Override
    public String name() {
        return Local;
    }

    public String toString(){
        return "LocalCredential";
    }
}
