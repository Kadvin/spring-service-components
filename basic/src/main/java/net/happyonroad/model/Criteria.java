package net.happyonroad.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.happyonroad.util.PatternUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * <h1>过滤器</h1>
 * <p/>
 * 封装了对特定数据的查询条件，作为前端(页面)和后端(应用服务器)的查询媒介
 *
 * @author Jay Xiong
 */
public class Criteria {
    public static final  String  EQ                   = "=";
    public static final  String  NEQ                  = "!=";
    public static final  String  GT                   = ">"; // Greater Than
    public static final  String  GET                  = ">="; // Greater or Equals Than
    public static final  String  LT                   = "<";  //Less Than
    public static final  String  LET                  = "<="; //Less or Equals Than
    public static final  String  RM                   = "=~"; //Regular Match
    public static final  String  NRM                  = "!~"; //Not Regular Match
    public static final  String  IN                   = "in"; //IN
    public static final  String  NIN                  = "not-in"; //Not IN
    public static final  String  Under                = "under"; //UNDER
    public static final  String  NotUnder             = "not-under"; //Not UNDER


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
}

class CriteriaItem {
    private static final Pattern UNDERSCORE_PATTERN_1 = Pattern.compile("([A-Z]+)([A-Z][a-z])");

    private static final Pattern UNDERSCORE_PATTERN_2 = Pattern.compile("([a-z\\d])([A-Z])");


    private final String field;
    private final String operation;
    private final String value;

    private String sqlField;
    private String sqlOperation;
    private String sqlValue;


    public CriteriaItem(String field, String operation, String value) {
        this.field = field;
        this.operation = operation;
        this.value = value;
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
}