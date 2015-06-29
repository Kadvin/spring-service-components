package net.happyonroad.util;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;

/**
 * <h1>Extended Mapper which config can be set</h1>
 *
 * @author Jay Xiong
 */
public class ExtendedMapper extends ObjectMapper {
    private static final long serialVersionUID = 4862638288361635610L;

    public void setSerializationConfig(SerializationConfig serializationConfig) {
        super._serializationConfig = serializationConfig;
    }

    public void setDeserializationConfig(DeserializationConfig config) {
        super._deserializationConfig = config;
    }
}
