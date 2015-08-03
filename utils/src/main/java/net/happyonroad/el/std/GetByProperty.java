package net.happyonroad.el.std;

import net.happyonroad.el.CalculateException;
import net.happyonroad.el.Calculator;

import java.util.Map;

/**
 * <h1>根据属性名称读取相应对象数值的计算器</h1>
 *
 * @author Jay Xiong
 */
public class GetByProperty<Out> implements Calculator<Map, Out> {
    private String property;

    public GetByProperty(String property) {
        this.property = property;
    }

    @Override
    public Out calc(Map input) throws CalculateException {
        if (input == null) return null;
        //noinspection unchecked
        return (Out) input.get(property);
    }
}
