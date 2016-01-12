package net.happyonroad.el.std;

import net.happyonroad.el.CalculateException;
import net.happyonroad.el.Calculator;
import net.happyonroad.util.ExpressionUtils;
import org.apache.commons.lang.Validate;

/**
 * <h1>计算表达式</h1>
 *
 * @author Jay Xiong
 */
public class Expression implements Calculator<Object, Boolean> {

    private Calculator<Object, Comparable> left;
    private String                         operator;
    private Calculator<Object, Comparable> right;

    public Expression(Calculator<Object, Comparable> left, String operator, Calculator<Object, Comparable> right) {
        Validate.notNull(left, "The left calculator can't be null");
        Validate.notNull(right, "The right calculator can't be null");
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    // 如果表达式的计算，需要支持从多个对象中取值，那么现在这个方式就有问题了
    //  重构的方式是， 将input构建为一个context(map)， 根据将chain中的对象名称，到context里面查找对象
    // 这样越做，越与Spring EL靠齐
    @Override
    public Boolean calc(Object input) throws CalculateException {
        Comparable leftValue;
        Comparable rightValue;
        if( left instanceof Constant && right instanceof Constant){
            leftValue = this.left.calc(input);
            rightValue = this.right.calc(input);
        }else {
            if( right instanceof Constant){
                leftValue = this.left.calc(input);
                Class leftClass = leftValue == null ? String.class : leftValue.getClass();
                rightValue = this.right.calc(leftClass);
            }else if(left instanceof Constant){
                rightValue = this.right.calc(input);
                Class rightClass = rightValue == null ? String.class : rightValue.getClass();
                leftValue = this.left.calc(rightClass);
            }else{
                leftValue = this.left.calc(input);
                rightValue = this.right.calc(input);
            }
        }
        return ExpressionUtils.compare(leftValue, operator, rightValue);
    }

    @Override
    public String toString() {
        return "(" + left + ' ' + operator + ' '  + right + ')';
    }
}
