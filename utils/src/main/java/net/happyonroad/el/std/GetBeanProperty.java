package net.happyonroad.el.std;

import net.happyonroad.el.CalculateException;
import net.happyonroad.el.Calculator;
import net.happyonroad.util.MiscUtils;

/**
 * <h1>根据名称读取相应对象属性的计算器</h1>
 *
 * @author Jay Xiong
 */
public class GetBeanProperty<In, Out> implements Calculator<In, Out> {
    String property;

    public GetBeanProperty(String property) {
        this.property = property;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Out calc(In input) throws CalculateException {
        try {
            return (Out) MiscUtils.getProperty(input, property);
        } catch (NoSuchMethodException e) {
            if( input instanceof Object[] && "length".equals(property))
                return (Out)new Integer(((Object[])input).length);
            throw new CalculateException("Can't read " + property + " from " + input ,e);
        } catch (Exception e) {
            throw new CalculateException("Can't read " + property + " from " + input ,e);
        }
    }

    @Override
    public String toString() {
        return "." + property;
    }
}
