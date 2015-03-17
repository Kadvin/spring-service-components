/**
 * @author XiongJie, Date: 13-11-20
 */
package net.happyonroad.credential;

import net.happyonroad.util.ParseUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

/**
 * <h1>The snmp passport for V3</h1>
 */
@SuppressWarnings("UnusedDeclaration")
public class SnmpPassport implements Serializable {

    private static final long serialVersionUID = -3830687286331116754L;
    private String authenticateMethod;//身份验证类型 MD5/SHA
    private String encryptMethod;//加密类型 DES/AES
    private String privateKey;//数据加密密钥
    private String userName;
    private String password;
    private String context;// 上下文名称

    public SnmpPassport() {
        //noinspection unchecked
        this(Collections.EMPTY_MAP);
    }

    public SnmpPassport(Map<String, Object> passport) {
        this.authenticateMethod = ParseUtils.parseString(passport.get("authenticateMethod"), "MD5");
        this.encryptMethod = ParseUtils.parseString(passport.get("encryptMethod"), "DES");
        this.privateKey = ParseUtils.parseString(passport.get("privateKey"), null);
        this.userName = ParseUtils.parseString(passport.get("userName"), null);
        this.password = ParseUtils.parseString(passport.get("password"), null);
        this.context = ParseUtils.parseString(passport.get("context"), null);
    }

    public String getAuthenticateMethod() {
        return authenticateMethod;
    }

    public void setAuthenticateMethod(String authenticateMethod) {
        this.authenticateMethod = authenticateMethod;
    }

    public String getEncryptMethod() {
        return encryptMethod;
    }

    public void setEncryptMethod(String encryptMethod) {
        this.encryptMethod = encryptMethod;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SnmpPassport)) return false;

        SnmpPassport that = (SnmpPassport) o;

        if (authenticateMethod != null ? !authenticateMethod.equals(that.authenticateMethod) :
            that.authenticateMethod != null) return false;
        if (context != null ? !context.equals(that.context) : that.context != null) return false;
        if (encryptMethod != null ? !encryptMethod.equals(that.encryptMethod) : that.encryptMethod != null)
            return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (privateKey != null ? !privateKey.equals(that.privateKey) : that.privateKey != null) return false;
        //noinspection RedundantIfStatement
        if (userName != null ? !userName.equals(that.userName) : that.userName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = authenticateMethod != null ? authenticateMethod.hashCode() : 0;
        result = 31 * result + (encryptMethod != null ? encryptMethod.hashCode() : 0);
        result = 31 * result + (privateKey != null ? privateKey.hashCode() : 0);
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (context != null ? context.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return userName + "(******)";
    }
}
