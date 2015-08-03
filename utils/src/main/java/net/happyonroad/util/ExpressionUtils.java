package net.happyonroad.util;

import net.happyonroad.el.Calculator;
import net.happyonroad.el.CalculatorsChain;
import net.happyonroad.el.std.CompareByProperty;
import net.happyonroad.el.std.GetByProperty;
import net.happyonroad.el.std.LocateByIndex;
import org.apache.commons.lang.math.NumberUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * <h1>表达式工具</h1>
 * <pre>
 * 现在支持对:
 *  xxx[aaa][bbb][ccc]
 * 这种语法进行编译；
 * </pre>
 * 其中xxx为任意字符串，代表被计算的对象
 * <p>
 * 而 aaa/bbb/ccc 为某段表达式的具体计算逻辑，支持如下语法:
 * </p>
 * <p/>
 * <ul>
 * <li> 数字: 如，result[0] 代表取数组第一项
 * <li> 字符串: 如, result['name'] 用单引号将属性引用起来，代表 去map特定的key
 * <li> 比较: 如 result['name' == 'linux'] 代表取第一个name = 'linux'的记录,
 * 其中的比较符号支持 大于，小于，大于等于，小于等于，等于；被比较的对象为 Comparable，
 * 如果是字符串形式的对象，将会试图按照数字进行比较
 * </ul>
 *
 * @author Jay Xiong
 */
@SuppressWarnings("UnusedDeclaration")
public class ExpressionUtils {
    static Pattern pattern         = Pattern.compile("\\[([^\\]]+)\\]");
    static Pattern comparePattern  = Pattern.compile("\\s*([^><=]+)\\s*([><=]+)\\s*([^><=]+)\\s*");
    static Pattern propertyPattern = Pattern.compile("\\s*'([\\w|.|-]+)'\\s*");

    /**
     * <h2>将已有的表达式解析成为特定计算器的工具</h2>
     * <p/>
     * <pre>
     *
     * 如，原有的spring el表达式为：
     *
     *  #{result.?[#this['IfIndex'] == 123][0]['IfNUcastPkts']}
     *
     * 其含义为，从result(array)中寻找 属性 IfIndex = 123 的第一条记录，获取其 IfNUcastPkts 属性
     *
     * 而实际对象记录为：
     *  [{IfIndex: 123, IfNUcastPkts: 20}, {IfIndex: 124, IfNUCastPkts: 22}]
     *
     * 其编译结果可以被简化为如下两个算子:
     *
     *  Calculator&lt;Array, Map&gt; -&gt; Calculator&lt;Map, Object&gt;
     *      |                                   |
     *  CompareByProperty                    GetByProperty
     *
     * 相应的语法也应该从源头进行简化为：
     *   result['IfIndex' == 123]['IfNUcastPkts']
     *
     * 对于 老的DSS的实际采集场景，由于其数据为:
     *  [{IfIndex: 123, IfNUcastPkts: 20, ...}]
     * 其语法可以被简化为:
     *   result[0]['IfNUcastPkts']
     *
     *  Calculator&lt;Array, Map&gt; -&gt; Calculator&lt;Map, Object&gt;
     *      |                                   |
     *  LocateByIndex                    GetByProperty
     *
     *
     * </pre>
     *
     * @param expression 原有的表达式
     * @return 计算器
     */
    public static Calculator compile(String expression) {
        Matcher matcher = pattern.matcher(expression);
        CalculatorsChain chain = new CalculatorsChain();
        while (matcher.find()) {
            String exp = matcher.group(1);
            Calculator calculator = compileSegment(exp);
            chain.addCalculator(calculator);
        }
        if (chain.getSize() == 0) {
            throw new PatternSyntaxException("Your expression `" + expression + "` is illegal",
                                             pattern.pattern(), 0);
        } else if (chain.getSize() == 1) {
            return chain.get(0);
        } else {
            return chain;
        }
    }

    private static Calculator compileSegment(String exp) {
        if (NumberUtils.isNumber(exp)) {
            return new LocateByIndex(Integer.valueOf(exp));
        } else {
            Matcher matcher = comparePattern.matcher(exp);
            if (matcher.matches()) {
                String left = matcher.group(1).trim();
                String operator = matcher.group(2);
                String right = matcher.group(3).trim();
                String property, value;
                matcher = propertyPattern.matcher(left);
                if (matcher.matches()) {
                    property = left;
                    value = right;
                } else {
                    matcher = propertyPattern.matcher(right);
                    if (matcher.matches()) {
                        property = right;
                        value = left;
                    } else {
                        throw new PatternSyntaxException("You must provide a quoted property",
                                                         propertyPattern.pattern(), 0);
                    }
                }
                return new CompareByProperty(property, value, operator);
            }
            matcher = propertyPattern.matcher(exp);
            if (matcher.matches()) {
                return new GetByProperty(matcher.group(1));
            } else {
                throw new UnsupportedOperationException("The expression " + exp + " is not supported");
            }
        }
    }


}
