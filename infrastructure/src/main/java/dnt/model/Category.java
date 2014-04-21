/**
 * Developer: Kadvin Date: 14-1-22 上午9:45
 */
package dnt.model;

import dnt.support.JsonSupport;

import java.util.LinkedList;
import java.util.List;

/**
 * 对象类型
 */
public class Category extends JsonSupport {
    private String         name;
    private String         alias;
    private String         label;
    private String         description;
    private transient Category       parent;
    private transient List<Category> children;

    public String getType() {
        if (getParent() != null)
            return getParent().getType() + "." + name;
        return getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

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
}
