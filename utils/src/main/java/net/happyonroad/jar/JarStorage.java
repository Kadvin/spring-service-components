/**
 * @author XiongJie, Date: 13-8-28
 */
package net.happyonroad.jar;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 代表某个单纯的Jar的对象
 * 代替原有外部的JarResources（因为该类会把所有jar资源一次性加载进来)
 */
public abstract class JarStorage {
    /**
     * 为了让单元测试好测试，这里增加一个可控的参数
     *
     * SoftReference与WeakReference的区别是：
     * 1. 一旦GC发现对象只有Weak Reference时，就会将其放入其ReferenceQueue，等待下次GC时回收（此时应用已经无法获取之)
     * 2. 如果对象仅有SoftReference，那么GC可能会向系统申请更多的内存，而不是立刻释放
     * 所以，使用SoftReference将会使单元测试变得困难，但系统实际可用性好一点（缓存生命周期长）
     */
    public static boolean WEAK = false;
    /**
     * 这个contents的存储意义与父类jarEntryContents类似，只是它的内容可以在内存不足时释放
     */
    protected Map<String, Reference<byte[]>> contents = new HashMap<String, Reference<byte[]>>();
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 主要对外公开API
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 是否有特定资源
     *
     * @param name 资源路径/名称
     * @return 判断结果
     */
    public boolean hasResource(String name) {
        return contents.containsKey(name);
    }

    /**
     * 判断某个资源是否已经加载
     *
     * @param name 资源路径/名称
     * @return 是否加载，如果这个资源不存在，那么一定返回false
     */
    public boolean isLoaded(String name) {
        if (!hasResource(name)) return false;
        Reference<byte[]> reference = contents.get(name);
        return reference != null && reference.get() != null;
    }

    /**
     * 获取相应的资源，如果资源尚未加载，会进行延迟加载
     *
     * @param name 资源路径/名称
     * @return 结果
     */
    public byte[] getResource(String name) {
        Reference<byte[]> reference = contents.get(name);
        // Pre-load之后，有Key，但Value为null
        byte[] bytes = reference == null ? null : reference.get();
        if (bytes != null)
            return bytes;
        else {
            bytes = lazyLoad(name);
            reference = createReference(bytes);
            contents.put(name, reference);
        }
        return bytes;
    }

    protected Reference<byte[]> createReference(byte[] bytes) {
        return WEAK ? new WeakReference<byte[]>(bytes) : new SoftReference<byte[]>(bytes);
    }

    /**
     * 获取所有的资源Key
     *
     * @return 资源Key集合
     */
    public Set<String> getResourceKeys() {
        return contents.keySet();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 生命周期方法
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 主动释放所有的内存
     */
    public void clear() {
        for (Reference<byte[]> reference : contents.values()) {
            if (reference != null) reference.clear();
        }
    }

    /**
     * 预先遍历加载一遍，了解这个里面有哪些资源
     */
    abstract void preLoad();

    /**
     * 当目标资源未加载或者被卸载后，重新加载资源
     *
     * @param resourceName 资源路径
     * @return 资源二进制
     */
    abstract byte[] lazyLoad(String resourceName);

    /**
     * 上下文路径
     *
     * @return url 路径
     */
    abstract URL contextURL();

    /**
     * 根据模式过滤已知内容
     *
     * @param pattern  针对资源路径的正则表达式
     * @return 资源路径列表
     */
    public Collection<String> filter(Pattern pattern) {
        List<String> founds = new ArrayList<String>();
        Set<String> paths = contents.keySet();
        for (String path : paths) {
            Matcher matcher = pattern.matcher(path);
            if(matcher.matches()){
                founds.add(path);
            }
        }
        return founds;
    }
}
