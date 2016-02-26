/**
 * Developer: Kadvin Date: 14-7-18 下午2:42
 */
package net.happyonroad.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <h1>一般的业务对象</h1>
 * <p/>
 * 在数据库中以主表的形式出现（而不是关联表）
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class )
public class Record<T extends Serializable> implements Cloneable, Serializable{
    public static final String TOTAL  = "total";
    public static final String PAGES  = "pages";
    public static final String NUMBER = "number";
    public static final String REAL   = "real";
    public static final String SORT   = "sort";
    public static final String COUNT  = "count";

    private static final long serialVersionUID = 3209170950751539327L;

    protected static ObjectMapper mapper = new ObjectMapper();

    private T id;
    private Date createdAt, updatedAt;
    @JsonIgnore
    private boolean cascadeDeleting, cascadeCreating, hierarchyDeleting, cascadeUpdating;

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

    public Record() {
        creating();
    }

    public Date getCreatedAt() {
        return createdAt == null ? new Date(System.currentTimeMillis()) : createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt == null ? new Date(System.currentTimeMillis()) : updatedAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 标记本对象正在本创建
     */
    public void creating(){
        setCreatedAt(new Date(System.currentTimeMillis()));
        setUpdatedAt(getCreatedAt());
    }

    /**
     * 标记本对象正在被更新
     */
    public void updating(){
        setUpdatedAt(new Timestamp(System.currentTimeMillis()));
    }

    /**
     * 将另外一个对象的属性设置到本对象上来
     *
     * @param another 另外一个对象
     * @param excludeProperties 不需要设置的属性
     */
    public void apply( Record another, String... excludeProperties) {
        try {
            Map<String, Object> originValues = new HashMap<String, Object>(excludeProperties.length);
            for (String property : excludeProperties) {
                Object originValue = PropertyUtils.getProperty(this, property);
                originValues.put(property, originValue);
            }
            PropertyUtils.copyProperties(this, another);
            for (String property : excludeProperties) {
                Object originValue = originValues.get(property);
                PropertyUtils.setProperty(this, property, originValue);
            }
        } catch (Exception e) {
            throw new RuntimeException("Can't apply record properties", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Record)) return false;

        Record record = (Record) o;

        //noinspection RedundantIfStatement
        if (id != null ? !id.equals(record.id) : record.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @JsonIgnore
    @Transient
    public boolean isNew() {
        return id == null || id.hashCode() <= 0 ;
    }


    public void cascadeCreating(){
        cascadeCreating = true;
    }

    public void cascadeDeleting(){
        cascadeDeleting = true;
    }

    @JsonIgnore
    @Transient
    public boolean isCascadeCreating(){
        return cascadeCreating;
    }

    @JsonIgnore
    @Transient
    public boolean isCascadeDeleting(){
        return cascadeDeleting;
    }

    public void hierarchyDeleting() {
        hierarchyDeleting = true;
    }

    @JsonIgnore
    @Transient
    public boolean isHierarchyDeleting() {
        return hierarchyDeleting;
    }

    public void cascadeUpdating() {
        this.cascadeUpdating = true;
    }

    @JsonIgnore
    @Transient
    public boolean isCascadeUpdating() {
        return cascadeUpdating;
    }

    /**
     * 将class name中的cglib生成的动态名称去除
     *
     * @param className class的name
     * @return 去除之后的class name
     */
    public static String reduceCglibName(String className) {
        int pos = className.indexOf("$$EnhancerByCGLIB");
        return pos > 0 ? className.substring(0, pos) : className;
    }

    @JsonIgnore
    @Transient
    public String getClassName() {
        return reduceCglibName(getClass().getName());
    }

    @JsonIgnore
    @Transient
    public String getSimpleName() {
        return reduceCglibName(getClass().getSimpleName());
    }
}
