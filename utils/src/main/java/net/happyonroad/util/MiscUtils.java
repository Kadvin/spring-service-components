package net.happyonroad.util;

import net.happyonroad.component.container.AppLauncher;
import net.happyonroad.extension.GlobalClassLoader;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import static java.util.Locale.ENGLISH;

/**
 * <h1>乱七八糟的工具方法</h1>
 *
 * @author Jay Xiong
 */
public final class MiscUtils {
    public static final String KEY     = "component.feature.resolvers";
    public static final String SPRING  = "net.happyonroad.platform.resolver.SpringMvcFeatureResolver";
    public static final String MYBATIS = "net.happyonroad.platform.resolver.MybatisFeatureResolver";

    static Map<String, PropertyDescriptor> descriptorMap = new ConcurrentHashMap<String, PropertyDescriptor>();
    static PropertyDescriptor UNKNOWN_PROPERTY;

    static {
        try {
            UNKNOWN_PROPERTY = new PropertyDescriptor("unknown", Unknown.class);
        } catch (IntrospectionException e) {
            throw new RuntimeException("Can't init the unknown property descriptor");
        }
    }

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
            InputStream stream = null;
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
                StringWriter writer = new StringWriter();
                IOUtils.copy(stream, writer);
                value = writer.toString();
            } catch (IOException e) {
                throw new IllegalStateException("Can't convert " + rawValue, e);
            } finally {
                IOUtils.closeQuietly(stream);
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


    public static String which(String exe) {
        List<String> lines;
        String errors;
        try {
            Process exec = Runtime.getRuntime().exec(new String[]{"which", exe});
            exec.waitFor();
            lines = IOUtils.readLines(exec.getInputStream());
            errors = org.apache.commons.lang.StringUtils.join(IOUtils.readLines(exec.getErrorStream()), "\n");
        } catch (Exception ex) {
            throw new UnsupportedOperationException("Can't execute `which " + exe + "`", ex);
        }
        if (lines.isEmpty()) {
            throw new UnsupportedOperationException("Can't execute `which " + exe + "`: " + errors);
        }
        return lines.get(0);
    }

    public static boolean isSpringMvcEnabled() {
        return System.getProperty(KEY, "").contains(SPRING);
    }

    public static boolean isMybatisEnabled() {
        return System.getProperty(KEY, "").contains(MYBATIS);
    }

    // 性能优化结果：
    //   快速的读取对象属性，相比 org.apache.commons.beanutils.PropertyUtils
    public static <T> T getProperty(Object bean, String property)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null)
            throw new NullPointerException();
        //special treat for array length

        if ("length".equals(property)) {
            if (bean instanceof Object[]) {
                int length = ((Object[]) bean).length;
                //noinspection unchecked
                return (T) new Integer(length);
            } else if (ClassUtils.isPrimitiveArray(bean.getClass())) {
                //noinspection unchecked
                return (T) new Integer(Array.getLength(bean));
            }

        }
        String key = bean.getClass().getName() + "#" + property;
        PropertyDescriptor descriptor = descriptorMap.get(key);
        if (descriptor == null) {
            try {
                descriptor = new PropertyDescriptor(property, bean.getClass());
            } catch (IntrospectionException e) {
                String name = property.substring(0, 1).toUpperCase(ENGLISH) + property.substring(1);
                String readMethodName = "get" + name;
                try {
                    descriptor = new PropertyDescriptor(property, bean.getClass(), readMethodName, null);
                } catch (IntrospectionException e1) {
                    readMethodName = "is" + name;
                    try {
                        descriptor = new PropertyDescriptor(property, bean.getClass(), readMethodName, null);
                    } catch (IntrospectionException e2) {
                        descriptor = UNKNOWN_PROPERTY;
                    }
                }
            }
            descriptorMap.put(key, descriptor);
        }
        if (descriptor == UNKNOWN_PROPERTY) {
            throw new IllegalArgumentException("Unknown property " + property + " for " + bean);
        }
        if (descriptor.getReadMethod() == null) {
            throw new IllegalArgumentException(key + " without read method");
        }
        //noinspection unchecked
        return (T) descriptor.getReadMethod().invoke(bean);
    }

    public static void runWithClassLoader(ClassLoader cl, Runnable job){
        Thread thread = Thread.currentThread();
        ClassLoader legacy = thread.getContextClassLoader();
        try {
            thread.setContextClassLoader(cl);
            job.run();
        } finally {
            thread.setContextClassLoader(legacy);
        }
    }

    public static <T> T callWithClassLoader(ClassLoader cl, Callable<T> job) throws Exception {
        Thread thread = Thread.currentThread();
        ClassLoader legacy = thread.getContextClassLoader();
        try {
            thread.setContextClassLoader(cl);
            return job.call();
        } finally {
            thread.setContextClassLoader(legacy);
        }
    }

    public static <T> T callWithClassLoaderSilently(ClassLoader cl, Callable<T> job)  {
        try {
            return callWithClassLoader(cl, job);
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }

    static class Unknown {
        private boolean unknown;

        public boolean isUnknown() {
            return unknown;
        }

        public void setUnknown(boolean unknown) {
            this.unknown = unknown;
        }
    }
}
