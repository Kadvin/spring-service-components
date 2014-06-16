/**
 * @author XiongJie, Date: 13-11-20
 */
package dnt.access;

import dnt.model.Access;
import dnt.support.JsonSupport;

import java.util.Collections;
import java.util.Map;

/** SNMP Access Parameters */
public class SnmpAccess extends JsonSupport implements Access{

    private static final long serialVersionUID = -2413039242630188845L;

    private int timeout; // milliseconds
    private int retries;
    private int port;

    private SnmpPassport read;
    private SnmpPassport write;

    public SnmpAccess() {
        this(Collections.emptyMap());
    }

    @SuppressWarnings("unchecked")
    public SnmpAccess(Map map) {
        setTimeout(parseInt(map.get("timeout"), 60000));
        setRetries(parseInt(map.get("retries"), 3));
        setPort(parseInt(map.get("port"), 161));

        Map<String,String> passport = (Map<String,String>) map.get("read");
        if(passport != null )
            setRead(new SnmpPassport(passport));
        else
            setRead(new SnmpPassport());
        passport = (Map<String,String>) map.get("write");
        if(passport != null )
            setWrite(new SnmpPassport(passport));
        else
            setWrite(new SnmpPassport());
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

    public SnmpPassport getRead() {
        return read;
    }

    public void setRead(SnmpPassport read) {
        this.read = read;
    }

    public SnmpPassport getWrite() {
        return write == null ? read : write;
    }

    public void setWrite(SnmpPassport write) {
        this.write = write;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SnmpAccess that = (SnmpAccess) o;

        if (port != that.port) return false;
        if (retries != that.retries) return false;
        if (timeout != that.timeout) return false;
        if (read != null ? !read.equals(that.read) : that.read != null) return false;
        //noinspection RedundantIfStatement
        if (write != null ? !write.equals(that.write) : that.write != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = timeout;
        result = 31 * result + retries;
        result = 31 * result + port;
        result = 31 * result + (read != null ? read.hashCode() : 0);
        result = 31 * result + (write != null ? write.hashCode() : 0);
        return result;
    }
}
