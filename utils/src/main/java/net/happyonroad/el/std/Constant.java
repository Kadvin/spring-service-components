package net.happyonroad.el.std;

import net.happyonroad.el.CalculateException;
import net.happyonroad.el.Calculator;
import net.happyonroad.util.ExpressionUtils;
import org.apache.commons.beanutils.MethodUtils;

/**
 * <h1>常量表达式</h1>
 *
 * @author Jay Xiong
 */
public class Constant implements Calculator {

    private String value;

    public Constant(String value) {
        this.value = value;
    }

    @Override
    public Comparable calc(Object input) throws CalculateException {
        if ("null".equals(this.value))
            return null;
        if (input instanceof Class) {
            if (input == String.class) {
                return ExpressionUtils.unQuote(this.value);
            }
            try {
                return (Comparable) MethodUtils.invokeExactStaticMethod((Class) input, "valueOf", value);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Can't convert \"" + value + "\" to given class " + input, ex);
            }
        }
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
