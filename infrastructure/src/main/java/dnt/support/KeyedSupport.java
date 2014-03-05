/**
 * @author XiongJie, Date: 13-10-25
 */
package dnt.support;

import java.util.UUID;

/** Json支持性接口 */
public abstract class KeyedSupport extends JsonSupport {

    private static final long serialVersionUID = 1597815290802309384L;
    /*private*/ String key;

    protected KeyedSupport() {
    }

    public KeyedSupport(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + getKey() + ")";
    }

    /*package: for json-smart*/
    public void setKey(String key) {
        this.key = key;
    }

    public String toLog() {
        return getClass().getSimpleName() + "[" + key + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KeyedSupport that = (KeyedSupport) o;

        //noinspection RedundantIfStatement
        if (!key.equals(that.key)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    public static String randomKey() {
        return UUID.randomUUID().toString();
    }

}
