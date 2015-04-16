/**
 * @author XiongJie, Date: 13-11-20
 */
package net.happyonroad.credential;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.happyonroad.model.Credential;

import java.util.Collections;
import java.util.Map;

import static net.happyonroad.util.ParseUtils.parseInt;
import static net.happyonroad.util.ParseUtils.parseString;

/**
 * <h1>SNMP Credential Parameters</h1>
 * <ul>
 *  version: v1, v2c, v3
 *  community: read/write share this community
 *  port: 161 as default
 *  timeout: 1 minute as defaults
 *  retries: 3 times as default
 * </ul>
 *
 */
public class SnmpCredential implements Credential {
    private static final long serialVersionUID = 5338739467961515785L;
    private String version; //v1, v2c, v3
    private String community;//shared between read/write
    private int    port; //161
    private int    timeout; // 60000 milliseconds = 1 minutes
    private int    retries; // 3 times

    //仅仅在version=v3的时候需要有该属性
    private SnmpPassport passport;

    public SnmpCredential() {
        this(Collections.emptyMap());
    }

    @JsonIgnore
    @Override
    public int getOrder() {
        return 10;
    }

    @SuppressWarnings("unchecked")
    public SnmpCredential(Map map) {
        setVersion(parseString(map.get("version"), "v2c"));
        setCommunity(parseString(map.get("community"), "public"));
        setPort(parseInt(map.get("port"), 161));
        setTimeout(parseInt(map.get("timeout"), 15000));/*15s timeout for snmp, same as windows*/
        setRetries(parseInt(map.get("retries"), 3));

        Map<String, Object> passport = (Map<String, Object>) map.get("passport");
        if (passport != null)
            setPassport(new SnmpPassport(passport));
    }

    @Override
    public String name() {
        return Snmp;
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

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public SnmpPassport getPassport() {
        return passport;
    }

    public void setPassport(SnmpPassport passport) {
        this.passport = passport;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SnmpCredential)) return false;

        SnmpCredential that = (SnmpCredential) o;

        if (port != that.port) return false;
        if (retries != that.retries) return false;
        if (timeout != that.timeout) return false;
        if (!community.equals(that.community)) return false;
        if (passport != null ? !passport.equals(that.passport) : that.passport != null) return false;
        //noinspection RedundantIfStatement
        if (!version.equals(that.version)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = version.hashCode();
        result = 31 * result + community.hashCode();
        result = 31 * result + port;
        result = 31 * result + timeout;
        result = 31 * result + retries;
        result = 31 * result + (passport != null ? passport.hashCode() : 0);
        return result;
    }

    public String toString(){
        return "SnmpCredential(" + community + "@" + version + ")";
    }
}
