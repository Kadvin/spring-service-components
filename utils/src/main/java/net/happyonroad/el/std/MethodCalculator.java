package net.happyonroad.el.std;

import net.happyonroad.el.CalculateException;
import net.happyonroad.el.Calculator;
import org.apache.commons.beanutils.MethodUtils;

/**
 * <h1>调用方法的计算器</h1>
 *
 * @author Jay Xiong
 */
public class MethodCalculator<In, Out> implements Calculator<In, Out> {
    private String methodName;

    public MethodCalculator(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public Out calc(In input) throws CalculateException {
        try {
            //noinspection unchecked
            return (Out)MethodUtils.invokeMethod(input, methodName, new Object[0]);
        }catch (Exception ex){
            throw new CalculateException("Can't invoke " + methodName + " against " + input, ex);
        }
    }

    @Override
    public String toString() {
        return "." + methodName + "()";
    }
}
