package net.happyonroad.el.std;

import net.happyonroad.el.CalculateException;
import net.happyonroad.el.Calculator;

/**
 * <h1>根据数组索引定位对象的计算器</h1>
 *
 * @author Jay Xiong
 */
public class LocateByIndex<Out> implements Calculator<Out[],Out> {
    private int index;

    public LocateByIndex(int index) {
        this.index = index;
    }

    @Override
    public Out calc(Out[] input) throws CalculateException {
        if( input == null ) return null;
        return input[index];
    }
}
