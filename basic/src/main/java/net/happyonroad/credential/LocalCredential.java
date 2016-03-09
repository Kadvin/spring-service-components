package net.happyonroad.credential;

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

    @Override
    public boolean support(String type) {
        return super.support(type) || CLI.equals(type);
    }

    public String toString(){
        return "LocalCredential";
    }
}
