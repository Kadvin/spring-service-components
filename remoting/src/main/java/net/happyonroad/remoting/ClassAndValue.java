/**
 * Developer: Kadvin Date: 15/3/13 上午9:14
 */
package net.happyonroad.remoting;

import java.io.Serializable;

/**
* <h1>The class and value</h1>
*/
public class ClassAndValue implements Serializable {
    Class  klass;
    Object value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassAndValue)) return false;

        ClassAndValue that = (ClassAndValue) o;

        if (!klass.equals(that.klass)) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = klass.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
