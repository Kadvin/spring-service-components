package net.happyonroad.util;

import net.happyonroad.el.Calculator;
import net.happyonroad.el.CalculatorsChain;
import net.happyonroad.el.std.*;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;

import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * <h1>表达式工具</h1>
 * <pre>
 * 现在支持对:
 *  xxx[aaa][bbb].ccc, xxx.aaa[bbb][ccc]
 * 这种取值语法进行编译；
 *
 * 也支持，对 xxx[aaa][bbb][ccc] > another[x][yy][zzz] 这种比较语法
 * 同样支持，更复杂的 (x > a) || ((y < b) && (c < d )) 这种组合的逻辑表达式
 * </pre>
 * 其中xxx为任意字符串，代表被计算的对象
 * x, y, a, b, c, d可以为常量或者取值语句
 * <p>
 * 而 aaa/bbb/ccc 为某段表达式的具体计算逻辑，支持如下语法:
 * </p>
 * <p/>
 * <ul>
 * <li> 数字: 如，result[0] 代表取数组第一项
 * <li> 字符串: 如, result['name'] 用单引号将属性引用起来，代表 去map特定的key
 * <li> 比较: 如 result['name' == 'linux'] 代表取第一个name = 'linux'的记录,
 * 其中的比较符号支持 大于(>)，小于(<)，大于等于(>=)，小于等于(<=)，等于(==)，匹配(=~),不匹配(!~)；
 * 被比较的对象为 Comparable， 如果是字符串形式的对象，将会试图按照数字进行比较
 * </ul>
 *
 * @author Jay Xiong
 */
@SuppressWarnings("UnusedDeclaration")
public class ExpressionUtils {
    static Pattern pattern            = Pattern.compile("\\[([^\\]]+)\\]|\\.([^\\.]+)");
    static Pattern comparePattern     = Pattern.compile("\\s*([^><=~]+)\\s*([><=~]+)\\s*([^><=~]+)\\s*");
    static Pattern propertyPattern    = Pattern.compile("\\s*'([\\w|.|-]+)'\\s*");
    static Pattern methodPattern      = Pattern.compile("([\\w]+)\\(\\s*\\)");
    static Pattern singleQuotePattern = Pattern.compile("'(.*)'");
    static Pattern doubleQuotePattern = Pattern.compile("\"(.*)\"");

    final static Map<String, Calculator> expressionCache = new ConcurrentHashMap<String, Calculator>();

    /**
     * <h2>编译表达式</h2>
     *
     * @param expression 原始表达式
     * @return 编译结果
     */
    public static Calculator<Object, Boolean> expression(String expression) {
        Validate.notNull(expression, "The expression for logic expression can't be null");
        String key = expression.trim();
        //noinspection unchecked
        Calculator<Object, Boolean> cached = expressionCache.get(key);
        if (cached != null) return cached;
        Calculator<Object, Boolean> result = compileExpression(key);
        synchronized (expressionCache) {
            expressionCache.put(key, result);
        }
        return result;
    }

    /**
     * 编译表达式
     * <pre>
     *  meet (       -> start segment
     *       [       -> open calc
     *       ]       -> close calc
     *       '       -> open/close string
     *       >,<,... -> operator
     *       space   -> element boundary
     *       )       -> finish segment
     * </pre>
     *
     * @param expression origin expression like:
     *                   (node[1] > 20) || ((node['age'] < 100) && (node[age] < node[birth] )) <br/>
     *                   现在只支持单个对象作为root object，取名随意 如上文的node <br/>
     *                   就是指代 LogicExpression.eval(Object)中的参数Object实例
     * @return compiled expression
     */
    private static Calculator<Object, Boolean> compileExpression(String expression) {
        if (!expression.startsWith("(") || !expression.endsWith(")")) {
            expression = "(" + expression + ")";
        }
        CalculatorBuilder result = null;
        Stack<CalculatorBuilder> stack = new Stack<CalculatorBuilder>();
        boolean quoted = false, spaced = false, escaped = false, calc = false;
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (escaped) {
                stack.peek().append(c);
                escaped = false;
                continue;
            }
            if (c == '\'') {
                quoted = !quoted;
                stack.peek().append(c);
                spaced = false;
                continue;
            }
            if (c == '[') {
                calc = true;
            }
            if (quoted) {
                stack.peek().append(c);
            } else if (calc) {
                stack.peek().append(c);
                if (c == ']')
                    calc = false;
            } else {
                switch (c) {
                    case '\\':
                        escaped = true;
                        break;
                    case '(':
                        stack.push(new CalculatorBuilder());
                        spaced = false;
                        break;
                    case ')':
                        CalculatorBuilder segment = stack.pop();
                        if (stack.isEmpty()) {
                            stack.push(segment);
                        } else {
                            stack.peek().append(segment);
                        }
                        spaced = false;
                        break;
                    case ' ':
                        if (spaced) continue;// trim white space
                        if (stack.isEmpty()) {
                            stack.push(new CalculatorBuilder());
                        } else if (stack.peek().finished()) {
                            stack.push(new CalculatorBuilder(stack.pop()));
                        } else {
                            stack.peek().step();
                        }
                        spaced = true;
                        break;
                    default: //common chars
                        stack.peek().append(c);
                        spaced = false;
                }
            }

        }
        if (stack.isEmpty()) {
            throw new IllegalArgumentException("Can't compile \"" + expression + "\" as expression");
        }
        //noinspection unchecked
        return stack.pop().build();
    }


    /**
     * <h2>将已有的表达式解析成为特定计算器的工具</h2>
     * 本方法具有缓存能力，同样的字符串，将会得到同一个对象
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
     * <p/>
     * TODO 这里也应该基于语法栈进行解析，而不是基于正则表达式
     */
    public static Calculator calculator(String expression) {
        Validate.notNull(expression, "The expression for calculator can't be null");
        String key = expression.trim();
        Calculator cached = expressionCache.get(key);
        if (cached != null) return cached;
        Calculator calculator = compileCalculator(expression);
        if (!(calculator instanceof Constant)) {
            synchronized (expressionCache) {
                expressionCache.put(key, calculator);
            }
        }
        return calculator;
    }

    static Calculator compileCalculator(String expression) {
        if (isQuoted(expression)) {
            return new Constant(expression);
        }
        int index1 = expression.indexOf('[');
        int index2 = expression.indexOf('.');
        String name;
        if (index1 >= 0) {
            if (index2 >= 0) {
                name = expression.substring(0, Math.min(index1, index2));
            } else {
                name = expression.substring(0, index1);
            }
        } else {
            if (index2 >= 0) {
                name = expression.substring(0, index2);
            } else {
                return new Constant(expression);
            }
        }
        Matcher matcher = pattern.matcher(expression);
        CalculatorsChain chain = new CalculatorsChain();
        chain.setName(name);
        while (matcher.find()) {
            String exp = matcher.group(1);
            if (exp != null) {
                Calculator calculator = compileSegment(exp);
                chain.addCalculator(calculator);
            } else {
                //暂时仅有/支持两种方式 a.property, a.method()
                exp = matcher.group(2);
                Matcher mm = methodPattern.matcher(exp);
                if (mm.matches()) {
                    chain.addCalculator(new MethodCalculator(mm.group(1)));
                } else {
                    chain.addCalculator(new GetBeanProperty(exp));
                }
            }
        }
        if (chain.getSize() == 0) {
            return new Constant(expression);
        } else {
            return chain;
        }
    }

    static Calculator compileSegment(String exp) {
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
                return new CompareByProperty(property, operator, value);
            }
            matcher = propertyPattern.matcher(exp);
            if (matcher.matches()) {
                return new GetMapProperty(matcher.group(1));
            } else {
                throw new UnsupportedOperationException("The expression " + exp + " is not supported");
            }
        }
    }

    /**
     * 基于操作符，对两个可比较对象进行比较
     * <pre>
     * TODO: 现在的方法没有进行编译优化，例如：
     * a && b, a已经是false时，b就不需要计算了
     * a || b, a已经是true时，b也不需要计算
     * </pre>
     *
     * @param one      左式对象
     * @param another  右式对象
     * @param operator 比较操作符，支持逻辑操作符，以及字符串 =~， !~ 等
     * @return 比较结果
     */
    public static boolean compare(Comparable one, Comparable another, String operator) {
        //noinspection unchecked
        int result = one.compareTo(another);
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
        } else if (operator.equals("=~")) {//正则表达式匹配
            return Pattern.compile(another.toString()).matcher(one.toString()).find();
        } else if (operator.equals("!~")) {//正则表达式不匹配
            return !Pattern.compile(another.toString()).matcher(one.toString()).find();
        } else if (operator.equals("||") || operator.equalsIgnoreCase("or")) {//logic or
            return (Boolean) one || (Boolean) another;
        } else if (operator.equals("&&") || operator.equalsIgnoreCase("and")) {//logic and
            return (Boolean) one && (Boolean) another;
        } else {
            throw new IllegalStateException("The operator " + operator + " is not recognized");
        }
    }

    public static String unQuote(String value) {
        if (isSingleQuoted(value)) {
            return StringUtils.substringBetween(value, "'");
        } else if (isDoubleQuoted(value)) {
            return StringUtils.substringBetween(value, "\"");
        } else {
            return value;
        }
    }

    public static String quote(String property) {
        if (isQuoted(property)) {
            return property;
        } else {
            return "'" + property + "'";
        }
    }

    private static boolean isQuoted(String expression) {
        return isSingleQuoted(expression) || isDoubleQuoted(expression);
    }

    private static boolean isSingleQuoted(String expression) {
        return singleQuotePattern.matcher(expression).matches();
    }

    private static boolean isDoubleQuoted(String expression) {
        return doubleQuotePattern.matcher(expression).matches();
    }
}
