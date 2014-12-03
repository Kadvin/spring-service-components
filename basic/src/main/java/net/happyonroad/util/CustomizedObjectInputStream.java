/**
 * @author XiongJie, Date: 13-11-16
 */
package net.happyonroad.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * 先使用默认逻辑（默认采用latestUserDefinedLoader）
 * 如果默认失败,则采用当前线程上下文的ClassLoader进行加载
 */
public class CustomizedObjectInputStream extends ObjectInputStream {
    public CustomizedObjectInputStream(InputStream in) throws IOException {
        super(in);
    }

    protected Class<?> resolveClass(ObjectStreamClass desc)
            throws IOException, ClassNotFoundException {
        String name = desc.getName();
        return Class.forName(name, false, Thread.currentThread().getContextClassLoader());
    }

}
