/**
 * Developer: Kadvin Date: 14-6-16 下午1:30
 */
package dnt.model;


import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * 一般性的访问方式抽象
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
public interface Access {
}
