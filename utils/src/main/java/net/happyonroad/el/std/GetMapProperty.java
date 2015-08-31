package net.happyonroad.el.std;

import net.happyonroad.el.CalculateException;
import net.happyonroad.el.Calculator;
import net.happyonroad.util.ExpressionUtils;

import java.util.Map;

/**
 * <h1>根据名称读取相应Map对象属性的计算器</h1>
 *
 * @author Jay Xiong
 */
public class GetMapProperty<Out> implements Calculator<Map, Out> {
    String property;

    public GetMapProperty(String property) {
        this.property = ExpressionUtils.quote(property);
    }

    @Override
    public Out calc(Map input) throws CalculateException {
        if (input == null) return null;
        //noinspection unchecked
        return (Out) input.get(ExpressionUtils.unQuote(property));
    }

    @Override
    public String toString() {
        return "[" + property + "]";
    }
}
