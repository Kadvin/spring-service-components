package net.happyonroad.el.std;

import net.happyonroad.el.CalculateException;
import net.happyonroad.el.Calculator;
import net.happyonroad.util.StringUtils;
import org.springframework.util.NumberUtils;

import java.util.Map;

/**
 * <h1>根据属性定位对象的计算器</h1>
 * <p/>
 * 返回符合条件的第一个记录，不返回所有记录
 *
 * @author Jay Xiong
 */
public class CompareByProperty<Out> implements Calculator<Out[], Out> {
    private final GetByProperty<Comparable> getter;
    private final String                    operator;
    private final String                    value;

    public CompareByProperty(String property, String operator, String value) {
        this.getter = new GetByProperty<Comparable>(property);
        this.operator = operator;
        this.value = value;
    }

    @Override
    public Out calc(Out[] input) throws CalculateException {
        if (input == null) return null;
        for (Out out : input) {
            //TODO IF out is not a map, but an object
            // We should use another getter(by reflection)
            Comparable value = getter.calc((Map) out);
            if (compare(value)) {
                return out;
            }
        }
        return null;
    }

    protected boolean compare(Comparable value) {
        if (this.value == null || value == null) {
            //noinspection SimplifiableIfStatement
            if (this.value == null)
                return value == null;
            else {
                return false;
            }
        }
        Comparable base = this.value;
        if (this.value.startsWith("\"") && this.value.endsWith("\"")) {
            //字符串形式，需要剔除双引号
            base = StringUtils.substringBetween(this.value, "\"");
        } else if (value instanceof Number || org.apache.commons.lang.math.NumberUtils.isNumber(value.toString())) {
            base = NumberUtils.parseNumber(base.toString(), Double.class);
            value = NumberUtils.parseNumber(value.toString(), Double.class);
        }
        //noinspection unchecked
        int result = base.compareTo(value);
        if (operator.equals("=") || operator.equals("==")) {
            return result == 0;
        } else if (operator.equals(">")) {
            return result > 0;
        } else if (operator.equals(">=")) {
            return result >= 0;
        } else if (operator.equals("<")) {
            return result < 0;
        } else if (operator.equals("<=")) {
            return result <= 0;
        } else {
            throw new IllegalStateException("The operator " + this.operator + " is not recognized");
        }
    }

    @Override
    public String toString() {
        return "[" + getter.property + " " + operator + " " + value + "]";
    }
}
