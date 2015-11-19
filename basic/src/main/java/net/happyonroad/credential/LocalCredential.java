package net.happyonroad.credential;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * <h1>本机监控方式，只能监控与监控引擎部署在一起的资源</h1>
 *
 * @author Jay Xiong
 */
public class LocalCredential extends AbstractCredential implements CliCredential, WmiCredential {

    private static final long serialVersionUID = -7566065997165248098L;

    public LocalCredential() {
        setType(Local);
        setName(Local);
    }

    @JsonIgnore
    @Override
    public int getOrder() {
        return 0;
    }

    public String toString(){
        return "LocalCredential";
    }
}
