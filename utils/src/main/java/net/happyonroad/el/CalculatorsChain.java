package net.happyonroad.el;

import net.happyonroad.util.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * <h1>多个表达式的链条</h1>
 *
 * @author Jay Xiong
 */
public class CalculatorsChain implements Calculator {
    private String name;
    private List<Calculator> calculators;

    public CalculatorsChain() {
        calculators = new LinkedList<Calculator>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addCalculator(Calculator calculator) {
        this.calculators.add(calculator);
    }

    @Override
    public Object calc(Object input) throws CalculateException {
        Object target = preprocess(input);
        for (Calculator calculator : calculators) {
            try {
                //noinspection unchecked
                target = calculator.calc(target);
                target = preprocess(target);
            } catch (Exception e) {
                throw new CalculateException("Failed to calculate " + target + " by " + calculator, e);
            }
        }
        return target;
    }

    // 遇到list，用数组代替
    private Object preprocess(Object target) {
        if( target instanceof List)
            return ((List) target).toArray();
        return target;
    }

    public int getSize() {
        return calculators.size();
    }

    public Calculator get(int index) {
        return calculators.get(index);
    }

    @Override
    public String toString() {
        return name + StringUtils.join(calculators, "");
    }
}
