/**
 * @author XiongJie, Date: 13-11-20
 */
package net.happyonroad.credential;

import net.happyonroad.support.JsonSupport;

import java.util.Map;

/** The snmp passport by get or set  */
@SuppressWarnings("UnusedDeclaration")
public class SnmpPassport extends JsonSupport {
    private static final long serialVersionUID = 8363183702392915587L;

    private String version;
    private String community;
    private String securityName;
    private String authPassword;
    private String privatePassword;

    public SnmpPassport() {
        this.version = "v2c";
        this.community = "public";
    }

    public SnmpPassport(Map<String, String> passport) {
        this.version = passport.get("version");
        this.community = passport.get("community");
        this.securityName = passport.get("securityName");
        this.authPassword = passport.get("authPassword");
        this.privatePassword = passport.get("privatePassword");
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getSecurityName() {
        return securityName;
    }

    public void setSecurityName(String securityName) {
        this.securityName = securityName;
    }

    public String getAuthPassword() {
        return authPassword;
    }

    public void setAuthPassword(String authPassword) {
        this.authPassword = authPassword;
    }

    public String getPrivatePassword() {
        return privatePassword;
    }

    public void setPrivatePassword(String privatePassword) {
        this.privatePassword = privatePassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SnmpPassport passport = (SnmpPassport) o;

        if (authPassword != null ? !authPassword.equals(passport.authPassword) : passport.authPassword != null)
            return false;
        if (community != null ? !community.equals(passport.community) : passport.community != null)
            return false;
        if (privatePassword != null ? !privatePassword.equals(passport.privatePassword) : passport.privatePassword != null)
            return false;
        if (securityName != null ? !securityName.equals(passport.securityName) : passport.securityName != null)
            return false;
        //noinspection RedundantIfStatement
        if (!version.equals(passport.version)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (version != null ? version.hashCode() : 0);
        result = 31 * result + (community != null ? community.hashCode() : 0);
        result = 31 * result + (securityName != null ? securityName.hashCode() : 0);
        result = 31 * result + (authPassword != null ? authPassword.hashCode() : 0);
        result = 31 * result + (privatePassword != null ? privatePassword.hashCode() : 0);
        return result;
    }
}
