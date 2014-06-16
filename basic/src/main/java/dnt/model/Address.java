/**
 * Developer: Kadvin Date: 14-6-13 下午2:29
 */
package dnt.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

/**
 * 一般性的地址抽象接口
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
public interface Address extends Serializable, Cloneable{
}
