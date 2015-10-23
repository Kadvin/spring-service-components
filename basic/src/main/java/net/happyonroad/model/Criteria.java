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
    private String name;
    private StringBuilder builder = new StringBuilder();

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
            builder.append(criteria);
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return toString();
    }

    public void setValue(String value) {
        if (this.builder.length() > 0)
            this.builder.delete(0, this.builder.length() - 1);
        this.builder.append(value);
    }

    @Override
    public String toString() {
        return builder.toString();
    }

    @JsonIgnore
    public boolean isEmpty() {
        return StringUtils.isEmpty(toString());
    }
}
