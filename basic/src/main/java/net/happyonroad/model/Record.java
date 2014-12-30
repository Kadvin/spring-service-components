/**
 * Developer: Kadvin Date: 14-7-18 下午2:42
 */
package net.happyonroad.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;

import java.sql.Timestamp;

/**
 * <h1>一般的业务对象</h1>
 * <p/>
 * 在数据库中以主表的形式出现（而不是关联表）
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class )
public class Record {
    protected static ObjectMapper mapper = new ObjectMapper();

    private Long      id;
    private Timestamp createdAt, updatedAt;

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
        if (o == null || getClass() != o.getClass()) return false;

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
}
