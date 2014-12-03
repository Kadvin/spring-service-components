/**
 * @author XiongJie, Date: 13-12-2
 */
package net.happyonroad.support;

import net.happyonroad.model.Attributed;

import java.util.HashMap;
import java.util.Map;

/** The attributed object*/
public class AttributedSupport extends KeyedSupport implements Attributed {

    private static final long serialVersionUID = 4194674101273999992L;
    /** 对象的属性 */
    private Map<String, Object> attributes;

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public <T> T getAttribute(String key) {
        if(attributes == null ) return  null;
        //noinspection unchecked
        return (T) attributes.get(key);
    }

    public void setAttribute(String name, Object value) {
        if(this.attributes == null)
            this.attributes = new HashMap<String, Object>();
        this.attributes.put(name, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AttributedSupport that = (AttributedSupport) o;

        //noinspection RedundantIfStatement
        if (attributes != null ? !attributes.equals(that.attributes) : that.attributes != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        return result;
    }

    @Override
    protected AttributedSupport clone() throws CloneNotSupportedException {
        AttributedSupport another = (AttributedSupport) super.clone();
        if(attributes != null)
            another.setAttributes(new HashMap<String, Object>(getAttributes()));
        return another;
    }
}
