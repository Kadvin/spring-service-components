/**
 * Developer: Kadvin Date: 14-1-22 上午9:45
 */
package net.happyonroad.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import net.happyonroad.support.JsonSupport;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import java.util.LinkedList;
import java.util.List;

/**
 * 对象类型
 */
@ManagedResource(description = "系统模型分类")
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class )
public class Category extends JsonSupport {
    private           String         name;
    private           String         alias;
    private           String         label;
    private           String         description;
    private transient Category       parent;
    private transient List<Category> children;
    private Class resourceClass;

    @ManagedAttribute
    public String getType() {
        if (getParent() != null)
        {
            String parentType = getParent().getType();
            if( parentType.endsWith("/") )
                return parentType + getName();
            else
                return parentType + "/" + getName();
        }
        return getName();
    }

    @ManagedAttribute
    public String getName() {
        return name;
    }

    @ManagedAttribute
    public void setName(String name) {
        this.name = name;
    }

    @ManagedAttribute
    public String getAlias() {
        return alias;
    }

    @ManagedAttribute
    public void setAlias(String alias) {
        this.alias = alias;
    }

    @ManagedAttribute
    public String getLabel() {
        return label;
    }

    @ManagedAttribute
    public void setLabel(String label) {
        this.label = label;
    }

    @ManagedAttribute
    public String getDescription() {
        return description;
    }

    @ManagedAttribute
    public void setDescription(String description) {
        this.description = description;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public List<Category> getChildren() {
        return children;
    }

    @ManagedAttribute
    public int getChildrenSize(){
        return children == null ? 0 : children.size();
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }

    public void addChild(Category category) {
        if( children == null ){
            children = new LinkedList<Category>();
        }
        children.add(category);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + getName() + ")";
    }

    /**
     * 判断某个资源类别是不是在该分类之下
     * <pre>
     * category = host
     *  resource model = host.linux
     *  resource model = host.windows
     * category = network.router
     *  resource model = network
     *  resource model = network.router
     * </pre>
     * @param name 资源类别
     * @return 是否包含
     */
    public boolean includes(String name) {
        return name.startsWith(getType());
    }

    public void setResourceClass(Class resourceClass) {
        this.resourceClass = resourceClass;
    }

    public Class getResourceClass() {
        return resourceClass;
    }
}
