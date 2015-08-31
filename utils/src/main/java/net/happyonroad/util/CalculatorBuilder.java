package net.happyonroad.util;

import net.happyonroad.el.Calculator;
import net.happyonroad.el.std.Expression;

import java.util.Arrays;

/**
 * <h1>Calculator Builder</h1>
 *
 * @author Jay Xiong
 */
class CalculatorBuilder {
    private Object[] segments;
    private int      cursor;

    public CalculatorBuilder() {
        segments = new Object[3];
        cursor = 0;
    }

    public CalculatorBuilder(CalculatorBuilder previous) {
        this();
        segments[cursor] = previous;
        step();
    }

    public void step() {
        if( cursor == 2 )
            throw new IllegalStateException("You shouldn't step when the cursor at the end(2)");
        cursor++;
    }

    public void append(char c) {
        Object o = current();
        if (o == null) {
            o = segments[cursor] = new StringBuilder();
        }
        if (o instanceof StringBuilder)
            ((StringBuilder) o).append(c);
        else if (o instanceof CalculatorBuilder) {
            ((CalculatorBuilder) o).append(c);
        } else {
            throw new IllegalStateException("UnSupported segment");
        }
    }

    private Object current() {
        return segments[cursor];
    }

    public void append(CalculatorBuilder another) {
        segments[cursor] = another;
    }

    public Calculator build() {
        Calculator<Object,Comparable> left = convert(segments[0]);
        String operator = segments[1].toString();
        Calculator<Object,Comparable> right = convert(segments[2]);
        return new Expression(left, operator, right);
    }

    @SuppressWarnings("unchecked")
    protected Calculator<Object,Comparable> convert(Object segment) {
        Calculator<Object,Comparable> left;
        if(segment instanceof StringBuilder){
            left = ExpressionUtils.calculator(segment.toString());
        }else{//CalculatorBuilder
            left = ((CalculatorBuilder)segment).build();
        }
        return left;
    }

    @Override
    public String toString() {
        return Arrays.toString(segments) ;
    }

    public boolean finished() {
        return this.cursor == 2;
    }
}
