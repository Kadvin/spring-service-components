package net.happyonroad.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * <h1>读取Class相对的内容</h1>
 *
 * @author Jay Xiong
 */
public final class ClassStreamUtils {
    public static String actualContent(Class klass, String rawValue) {
        String value ;
        if(rawValue.startsWith(ResourceUtils.CLASSPATH_URL_PREFIX)){
            String path = rawValue.substring(ResourceUtils.CLASSPATH_URL_PREFIX.length());
            InputStream stream ;
            try {
                if(path.startsWith("./")){//relative path
                    path = klass.getPackage().getName().replaceAll("\\.","/") + path.substring(1);
                }
                stream = new ClassPathResource(path, klass.getClassLoader()).getInputStream();
                List<String> strings = IOUtils.readLines(stream);
                value = StringUtils.join(strings, SystemUtils.LINE_SEPARATOR);
            } catch (IOException e) {
                throw new IllegalStateException("Can't convert " + rawValue, e);
            }
        }else{
            value = rawValue;
        }
        return value;
    }
}
