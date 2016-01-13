package net.happyonroad.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.happyonroad.type.Severity;
import net.happyonroad.util.PatternUtils;
import net.happyonroad.util.Predicate;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Pattern;

/**
 * <h1>过滤器</h1>
 * <p/>
 * 封装了对特定数据的查询条件，作为前端(页面)和后端(应用服务器)的查询媒介
 *
 * @author Jay Xiong
 */
public class Criteria {
    public static final String EQ       = "=";
    public static final String NEQ      = "!=";
    public static final String GT       = ">"; // Greater Than
    public static final String GET      = ">="; // Greater or Equals Than
    public static final String LT       = "<";  //Less Than
    public static final String LET      = "<="; //Less or Equals Than
    public static final String RM       = "=~"; //Regular Match
    public static final String NRM      = "!~"; //Not Regular Match
    public static final String IN       = "in"; //IN
    public static final String NIN      = "not-in"; //Not IN
    public static final String Under    = "under"; //UNDER
    public static final String NotUnder = "not-under"; //Not UNDER


    //这个查询条件的名称
    private String label;
    private StringBuilder expression = new StringBuilder();

    public static Criteria parse(String criteria) {
        Criteria result = new Criteria();
        result.append(criteria);
        return result;
    }

    @JsonCreator
    public Criteria(@JsonProperty("label") String label, @JsonProperty("expression") String expression) {
        this.label = label;
        setExpression(expression);
    }

    public Criteria() {
    }

    public Criteria and(String segment) {
        if (expression.length() > 0) {
            append(" AND ");
        }
        append("(").append(segment).append(")");
        return this;
    }

    public Criteria or(String segment) {
        if (expression.length() > 0) {
            append(" OR ");
        }
        append("(").append(segment).append(")");
        return this;
    }

    private Criteria append(String criteria) {
        if (!StringUtils.isEmpty(criteria))
            // TODO 防止SQL注入
            expression.append(criteria);
        return this;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getExpression() {
        return expression.toString();
    }

    public void setExpression(String value) {
        if (this.expression.length() > 0)
            this.expression.delete(0, this.expression.length() - 1); // clean
        this.expression.append(value);
    }

    @Override
    public String toString() {
        return getExpression();
    }

    @JsonIgnore
    public boolean isEmpty() {
        return StringUtils.isEmpty(toString());
    }

    // 由前台的限制可知， criteria 仅有一层 &&
    public String toSQL() {
        List<CriteriaItem> items = toItems();
        // 将界面定义的各种操作翻译为 SQL操作，包括值也要转换， 如:
        //  =~ -> like
        //  !~ -> not like
        //  not-in -> not in
        //  under -> locate(xx, y) == 1
        //  not-under ->  locate(xx, y) == 0
        StringBuilder builder = new StringBuilder();
        Iterator<CriteriaItem> it = items.iterator();
        while (it.hasNext()) {
            CriteriaItem item = it.next();
            builder.append(item.toSQL());
            if (it.hasNext()) builder.append(" AND ");
        }
        return builder.toString();
    }

    List<CriteriaItem> toItems() {
        String expr = toString();
        String[] segments = expr.split("&&");
        List<CriteriaItem> items = new LinkedList<CriteriaItem>();
        for (String segment : segments) {
            segment = segment.trim();
            if (segment.charAt(0) == '(')
                segment = segment.substring(1);
            if (segment.charAt(segment.length() - 1) == ')')
                segment = segment.substring(0, segment.length() - 1);
            String[] triple = segment.split(" ");
            String field = triple[0];
            String operation = triple[1];
            String[] values = new String[triple.length - 2];
            System.arraycopy(triple, 2, values, 0, triple.length - 2);
            String value = StringUtils.join(values, " ");
            CriteriaItem item = new CriteriaItem(field, operation, value);
            items.add(item);
            item.doConvert();
        }
        return items;
    }

    public <T> Predicate<T> toPredicate(Map<String, String> mappings) {
        List<CriteriaItem> items = toItems();
        for (CriteriaItem item : items) {
            item.mapping(mappings);
        }
        return new CompoundCriteria<T>(items);
    }
}

class CompoundCriteria<T> implements Predicate<T> {

    private final List<CriteriaItem> items;

    public CompoundCriteria(List<CriteriaItem> items) {
        this.items = items;
    }

    @Override
    public boolean evaluate(T challenge) {
        for (CriteriaItem item : items) {
            if (!item.evaluate(challenge)) return false;
        }
        return true;
    }
}

class CriteriaItem implements Predicate {
    private static final Pattern UNDERSCORE_PATTERN_1 = Pattern.compile("([A-Z]+)([A-Z][a-z])");

    private static final Pattern UNDERSCORE_PATTERN_2 = Pattern.compile("([a-z\\d])([A-Z])");


    private final String field;
    private final String operation;
    private final String value;

    private String              sqlField;
    private String              sqlOperation;
    private String              sqlValue;
    private Map<String, String> mappings;


    public CriteriaItem(String field, String operation, String value) {
        this.field = field;
        this.operation = operation;
        this.value = value;
    }

    @Override
    public boolean evaluate(Object challenge) {
        Object left = readField(challenge, this.field);
        Object value = convertValue(this.value, this.operation, left);
        if (Criteria.EQ.equals(this.operation)) {
            return ObjectUtils.equals(left, value);
        } else if (Criteria.NEQ.equals(this.operation)) {
            return !ObjectUtils.equals(left, value);
        } else if (Criteria.GT.equals(this.operation) || Criteria.GET.equals(this.operation) ||
                   Criteria.LT.equals(this.operation) || Criteria.LET.equals(this.operation)) {
            if (!(left instanceof Comparable)) {
                throw new IllegalStateException(challenge + "'s " + field + ": " + left + " is not a Comparable");
            }
            if (!(value instanceof Comparable)) {
                throw new IllegalStateException(value + " is not a Comparable");
            }
            Comparable leftComparable = (Comparable) left;
            Comparable valueComparable = (Comparable) value;
            //noinspection unchecked
            int compared = leftComparable.compareTo(valueComparable);
            if (Criteria.GT.equals(this.operation)) {
                return compared > 0;
            } else if (Criteria.GET.equals(this.operation)) {
                return compared >= 0;
            } else if (Criteria.LT.equals(this.operation)) {
                return compared < 0;
            } else { //else if (Criteria.LET.equals(this.operation)) {
                return compared <= 0;
            }
        } else if (Criteria.RM.equals(this.operation) || Criteria.NRM.equals(this.operation)) {
            //value has been un-quoted
            Pattern pattern = Pattern.compile(value.toString());
            boolean matches = pattern.matcher(left.toString()).matches();
            if (Criteria.RM.equals(operation)) {
                return matches;
            } else {//NRM
                return !matches;
            }
        } else if (Criteria.IN.equals(this.operation) || Criteria.NIN.equals(this.operation)) {
            Object[] array = (Object[]) value;
            Arrays.sort(array);
            int index = Arrays.binarySearch(array, left);
            if (Criteria.IN.equals(this.operation)) {
                return index >= 0;
            } else {//NIN
                return index < 0;
            }
        } else if (Criteria.Under.equals(this.operation) || Criteria.NotUnder.equals(this.operation)) {
            boolean startsWith = left.toString().startsWith(value.toString());
            if (Criteria.Under.equals(this.operation)) {
                return startsWith;
            } else {//NotUnder
                return !startsWith;
            }
        } else {
            throw new IllegalStateException("Unsupported operation " + this.operation);
        }
    }

    private Object convertValue(String value, String operation, Object challenge) {
        Class<?> referType = challenge.getClass();
        if (Criteria.Under.equals(operation) || Criteria.NotUnder.equals(operation)) {
            return unquote(value);
        } else if (Criteria.RM.equals(operation) || Criteria.NRM.equals(operation)) {
            return unquote(value);
        } else if (Criteria.IN.equals(operation) || Criteria.NIN.equals(operation)) {
            value = value.trim();
            if (value.startsWith("[")) value = value.substring(1);
            if (value.endsWith("]")) value = value.substring(0, value.length() - 1);
            String[] strings = value.split("\\s*,\\s*");
            Object[] values = new Object[strings.length];
            for (int i = 0; i < strings.length; i++) {
                String val = strings[i];
                values[i] = convertValue(val, referType);
            }
            return values;
        }
        return convertValue(value, referType);
    }

    Object convertValue(String val, Class<? extends Object> referType) {
        val = unquote(val);
        if (referType == String.class) {
            return val;
        } else {
            try {
                //static
                Method valueOf = referType.getMethod("valueOf", String.class);
                if(Modifier.isStatic(valueOf.getModifiers()))
                    return valueOf.invoke(null, val);
                return val;
            } catch (NoSuchMethodException e) {
                return val;
            } catch (Exception ex) {
                return val;
            }
        }
    }

    String unquote(String string) {
        if (string.startsWith("'") || string.startsWith("\"")) string = string.substring(1);
        if (string.endsWith("'") || string.endsWith("\"")) string = string.substring(0, string.length() - 1);
        return string;
    }

    Object readField(Object challenge, String field) {
        String[] fields = field.split("\\.");
        Object value = challenge;
        for (String name : fields) {
            if (mappings.containsKey(name)) name = mappings.get(name);
            if ("this".equals(name)) continue;
            try {
                value = PropertyUtils.getProperty(value, name);
            } catch (Exception e) {
                throw new IllegalStateException("Can't read " + name + " from " + value, e);
            }
        }
        return value;
    }

    public void doConvert() {
        //Wrap field into `` for mysql, and convert camel case into underscore as db column
        String field;
        if (this.field.contains(".")) {
            int index = this.field.lastIndexOf('.');
            field = StringUtils.substringAfter(this.field, ".");
            field = underscore(field);
            this.sqlField = this.field.substring(0, index + 1) + "`" + field + "`";
        } else {
            field = underscore(this.field);
            this.sqlField = "`" + field + "`";
        }

        // 将界面定义的各种操作翻译为 SQL操作，包括值也要转换， 如:
        //  =~ -> like
        //  !~ -> not like
        //  not-in -> not in
        //  under -> locate(xx, y) == 1
        //  not-under ->  locate(xx, y) == 0
        if (Criteria.RM.equals(this.operation)) {
            this.sqlOperation = "like";
            this.sqlValue = compileLike(this.value);
        } else if (Criteria.NRM.equals(this.operation)) {
            this.sqlOperation = "not like";
            this.sqlValue = compileLike(this.value);
        } else if (Criteria.NIN.equals(this.operation)) {
            // t.value is null or t.value not in ('a','b')
            this.sqlField = sqlField + " IS NULL or " + sqlField;
            this.sqlOperation = "not in";
            this.sqlValue = compileIn(this.value);
        } else if (Criteria.IN.equals(this.operation)) {
            this.sqlOperation = this.operation;
            this.sqlValue = compileIn(this.value);
        } else if (Criteria.Under.equals(this.operation)) {
            // LOCATE($value, field) = 1
            this.sqlField = "LOCATE('" + this.value + "', " + this.sqlField + ")";
            this.sqlOperation = "=";
            this.sqlValue = "1";
        } else if (Criteria.NotUnder.equals(this.operation)) {
            // LOCATE($value, field) = 0
            this.sqlField = "LOCATE('" + this.value + "', " + this.sqlField + ")";
            this.sqlOperation = "=";
            this.sqlValue = "0";
        } else {
            this.sqlOperation = this.operation;
            this.sqlValue = this.value;
        }
    }

    private String compileLike(String value) {
        return PatternUtils.compileSql(value);
    }

    private String compileIn(String value) {
        String compiled = value.trim().replaceAll("^\\[", "(");
        compiled = compiled.replaceAll("\\]$", ")");
        return compiled;
    }

    /**
     * Underscore a word, such as:
     * <p/>
     * <p><b>ChinaCompany-> china_company</b></p>
     *
     * @param camelCasedWord the camel case word
     * @return the underscored word
     */
    public static String underscore(final String camelCasedWord) {
        String underscoredWord = UNDERSCORE_PATTERN_1.matcher(camelCasedWord)
                                                     .replaceAll("$1_$2");
        underscoredWord = UNDERSCORE_PATTERN_2.matcher(underscoredWord)
                                              .replaceAll("$1_$2");
        underscoredWord = underscoredWord.replace('-', '_')
                                         .toLowerCase();
        return underscoredWord;
    }

    public String toString() {
        return "(" + this.field + " " + this.operation + " " + this.value + ")";
    }

    public String toSQL() {
        return "(" + this.sqlField + " " + this.sqlOperation + " " + this.sqlValue + ")";
    }

    public void mapping(Map<String, String> mappings) {
        this.mappings = mappings;
    }
}