/**
 * @author XiongJie, Date: 13-11-20
 */
package net.happyonroad.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.happyonroad.model.Jsonable;
import net.happyonroad.util.ParseUtils;

/**
 * <h1>Common Json Support </h1>
 */
public class JsonSupport extends BinarySupport implements Jsonable {
    private static final long serialVersionUID = -5873792929187680622L;

    public String toJson() {
        try {
            return ParseUtils.getMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while convert " + getClass().getSimpleName() + " as json", e);
        }
    }

    @Override
    public String toString() {
        return toJson();
    }


}
