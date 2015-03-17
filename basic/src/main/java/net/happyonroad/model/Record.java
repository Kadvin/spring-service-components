/**
 * Developer: Kadvin Date: 14-7-18 下午2:42
 */
package net.happyonroad.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * <h1>一般的业务对象</h1>
 * <p/>
 * 在数据库中以主表的形式出现（而不是关联表）
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class )
public class Record implements Cloneable, Serializable{

    private static final long serialVersionUID = 3209170950751539327L;
    public static String[] HELP_ATTRS =
            {"class", "callbacks", "createdAt", "updatedAt", "new", "cascadeUpdating", "cascadeDeleting",
             "cascadeCreating", "hierarchyDeleting"};

    protected static ObjectMapper mapper = new ObjectMapper();

    private Long      id;
    private Timestamp createdAt, updatedAt;
    @JsonIgnore
    private boolean cascadeDeleting, cascadeCreating, hierarchyDeleting, cascadeUpdating;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Record() {
        creating();
    }

    public Timestamp getCreatedAt() {
        return createdAt == null ? new Timestamp(System.currentTimeMillis()) : createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt == null ? new Timestamp(System.currentTimeMillis()) : updatedAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 标记本对象正在本创建
     */
    public void creating(){
        setCreatedAt(new Timestamp(System.currentTimeMillis()));
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
     */
    public void apply( Record another) {
        try {
            BeanUtils.copyProperties(this, another);
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
    public boolean isNew() {
        return id == null || id <= 0 ;
    }


    public void cascadeCreating(){
        cascadeCreating = true;
    }

    public void cascadeDeleting(){
        cascadeDeleting = true;
    }

    @JsonIgnore
    public boolean isCascadeCreating(){
        return cascadeCreating;
    }

    @JsonIgnore
    public boolean isCascadeDeleting(){
        return cascadeDeleting;
    }

    public void hierarchyDeleting() {
        hierarchyDeleting = true;
    }

    @JsonIgnore
    public boolean isHierarchyDeleting() {
        return hierarchyDeleting;
    }

    public void cascadeUpdating() {
        this.cascadeUpdating = true;
    }

    @JsonIgnore
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
}
