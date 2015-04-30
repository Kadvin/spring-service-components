package net.happyonroad.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * <h1>乱七八糟的工具方法</h1>
 *
 * @author Jay Xiong
 */
public final class MiscUtils {

    /**
     * <h2>以最简洁的方式描述一个异常</h2>
     * 包括异常信息和最后一次的执行代码
     *
     * @param ex 异常
     * @return 异常信息
     */
    public static String describeException(Throwable ex) {
        String message = ExceptionUtils.getRootCauseMessage(ex);
        String[] traces = ExceptionUtils.getRootCauseStackTrace(ex);
        if (traces.length > 2)
            return message + traces[1];
        else return message;
    }

    /**
     * <h2>读取可能是class资源相对内容</h2>
     *
     * @param klass    类
     * @param rawValue 字符串，可能为 classpath: 开头，可能使用了相对路径
     * @return 字符串内容
     */
    public static String actualContent(Class klass, String rawValue) {
        String value;
        if (rawValue.startsWith(ResourceUtils.CLASSPATH_URL_PREFIX)) {
            String path = rawValue.substring(ResourceUtils.CLASSPATH_URL_PREFIX.length());
            InputStream stream;
            try {
                if (path.startsWith("./")) {//relative path
                    path = klass.getPackage().getName().replaceAll("\\.", "/") + path.substring(1);
                }
                stream = new ClassPathResource(path, klass.getClassLoader()).getInputStream();
                List<String> strings = IOUtils.readLines(stream);
                value = StringUtils.join(strings, SystemUtils.LINE_SEPARATOR);
            } catch (IOException e) {
                throw new IllegalStateException("Can't convert " + rawValue, e);
            }
        } else {
            value = rawValue;
        }
        return value;
    }
}
