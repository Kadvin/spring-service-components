package net.happyonroad.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.util.StringUtils;

/**
 * <h1>过滤器</h1>
 * <p/>
 * 封装了对特定数据的查询条件，作为前端(页面)和后端(应用服务器)的查询媒介
 *
 * @author Jay Xiong
 */
public class Criteria {
    //这个查询条件的名称
    private String label;
    private StringBuilder expression = new StringBuilder();

    public static Criteria parse(String criteria) {
        Criteria result = new Criteria();
        result.append(criteria);
        return result;
    }

    public Criteria and(String segment) {
        append(" AND (").append(segment).append(")");
        return this;
    }

    public Criteria or(String segment) {
        append(" OR (").append(segment).append(")");
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
            this.expression.delete(0, this.expression.length() - 1);
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
}
