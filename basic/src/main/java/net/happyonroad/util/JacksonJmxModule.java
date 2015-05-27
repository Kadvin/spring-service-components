package net.happyonroad.util;

import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.happyonroad.util.deserializer.JmxAttributeDeserializer;
import net.happyonroad.util.deserializer.JmxObjectNameDeserializer;
import net.happyonroad.util.serializer.JmxObjectNameSerializer;

import javax.management.Attribute;
import javax.management.ObjectName;

/**
 * <h1>The Jmx Jackson Module</h1>
 *
 * @author Jay Xiong
 */
public class JacksonJmxModule extends SimpleModule {
    private static final long serialVersionUID = 1;

    public JacksonJmxModule() {
        super(PackageVersion.VERSION);

        addDeserializer(Attribute.class, new JmxAttributeDeserializer());
        addDeserializer(ObjectName.class, new JmxObjectNameDeserializer());

        addSerializer(ObjectName.class, new JmxObjectNameSerializer());
    }
}
