package net.happyonroad.util;

import net.happyonroad.component.container.AppLauncher;
import net.happyonroad.extension.GlobalClassLoader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
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
        return AppLauncher.describeException(ex);
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
                try {
                    // 可能class为系统已经加载的类，而resource是扩展的类
                    stream = new ClassPathResource(path, klass.getClassLoader()).getInputStream();
                } catch (FileNotFoundException e) {
                    // 在这种情况下，需要尝试用Thread的当前class loader加载资源
                    try {
                        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                        stream = new ClassPathResource(path, classLoader).getInputStream();
                    } catch (FileNotFoundException e1) {
                        //再次使用全局的class loader
                        ClassLoader classLoader = GlobalClassLoader.getDefaultClassLoader();
                        stream = new ClassPathResource(path, classLoader).getInputStream();
                    }
                }
                List<String> strings = IOUtils.readLines(stream);
                value = StringUtils.join(strings, SystemUtils.LINE_SEPARATOR);
            } catch (IOException e) {
                throw new IllegalStateException("Can't convert " + rawValue, e);
            }
            //TODO support other form content, such as file:// in monitor system storage
        } else {
            value = rawValue;
        }
        return value;
    }

    /**
     * <h2>判断某个数值的特定bit位是否为1</h2>
     *
     * @param value 被判断的数值
     * @param pos   比特位
     * @return 是否为1
     */
    public static boolean isBitOn(int value, int pos) {
        int pv = 0x01 << (pos - 1);
        return (value & pv) == pv;
    }

    /**
     * <h2>将某个数值的特定bit位设置为1</h2>
     *
     * @param origin 原始数值
     * @param pos    比特位
     * @return 返回的值
     */
    public static int setBitOn(int origin, int pos) {
        return origin | (0x01 << (pos - 1));
    }

    /**
     * <h2>将某些数值的特定bit位设置为1</h2>
     *
     * @param origin    原始数值
     * @param positions 比特位
     * @return 返回的值
     */
    public static int setBitOn(int origin, int... positions) {
        int value = origin;
        for (int pos : positions) {
            value = setBitOn(value, pos);
        }
        return value;
    }

    /**
     * <h2>将某个数值的特定bit位设置为0</h2>
     *
     * @param origin 原始数值
     * @param pos    比特位
     * @return 返回的值
     */
    public static int setBitOff(int origin, int pos) {
        return origin & ~(0x01 << (pos - 1));
    }
}
