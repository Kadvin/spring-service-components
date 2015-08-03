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
    private List<Calculator> calculators;

    public CalculatorsChain() {
        calculators = new LinkedList<Calculator>();
    }

    public void addCalculator(Calculator calculator) {
        this.calculators.add(calculator);
    }

    @Override
    public Object calc(Object input) throws CalculateException {
        Object target = input;
        for (Calculator calculator : calculators) {
            try {
                //noinspection unchecked
                target = calculator.calc(target);
            } catch (Exception e) {
                throw new CalculateException("Failed to calculate " + target + " by " + calculator, e);
            }
        }
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
        return "result" + StringUtils.join(calculators, "");
    }
}
