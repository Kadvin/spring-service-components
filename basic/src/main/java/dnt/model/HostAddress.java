/**
 * @author XiongJie, Date: 14-1-14
 */

package dnt.model;

import java.io.Serializable;

/**
 * The interface for define a host address
 */
public interface HostAddress extends Serializable, Cloneable {
    String getHost();
}
