package net.happyonroad.cache;

import net.happyonroad.extension.GlobalClassLoader;
import net.happyonroad.util.AbstractCache;
import net.happyonroad.util.MiscUtils;
import net.happyonroad.util.ParseUtils;
import net.happyonroad.view.WebSocketView;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.Callable;

import static net.happyonroad.support.BinarySupport.parseBinary;
import static net.happyonroad.support.BinarySupport.toBinary;

/**
 * <h1>抽象的Engine对象存储器</h1>
 *
 * @author Jay Xiong
 */
public abstract class AbstractStore<K, V> extends AbstractCache<K, V> {
    @Autowired
    protected CacheService cacheService;
    // 原始缓存，json/binary形式
    // 一般存储的是与服务器一致的原始的对象
    protected MapContainer container;
    //对象存储形式，JSON | Binary
    boolean binaryMode = false;

    public AbstractStore(Class<V> objectClass) {
        super(objectClass);
    }

    protected void initContainer() {
        container = cacheService.getMapContainer(getName());
    }

    protected String getName() {
        return getClass().getSimpleName();
    }

    public boolean isBinaryMode() {
        return binaryMode;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setBinaryMode(boolean binaryMode) {
        this.binaryMode = binaryMode;
    }

    public String[] keys() {
        return container.keys();
    }

    /**
     * <h2>从container里面加载对象</h2>
     *
     * @param key 被加载的对象的key
     * @return 加载的对象实例
     */
    protected V load(K key) {
        if (isBinaryMode()) {
            byte[] bytes = container.getBinary(key.toString());
            if (bytes == null) return null;
            return parseBinary(bytes, getObjectClass());
        } else {
            final String json = container.get(key.toString());
            if (json == null) return null;
            try {
                //先用当前class loader解析
                return parseObject(json);
            } catch (RuntimeException ex) {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                GlobalClassLoader gcl = GlobalClassLoader.getInstance();
                if (gcl == cl) {
                    throw ex;
                }
                return MiscUtils.callWithClassLoaderSilently(gcl, new Callable<V>() {
                    @Override
                    public V call() throws Exception {
                        return parseObject(json);
                    }
                });
            }
        }
    }

    private V parseObject(String json) {
        if (viewClass() == null)
            return ParseUtils.parseJson(json, getObjectClass());
        else
            return ParseUtils.parseJson(json, getObjectClass(), viewClass());
    }

    protected K persist(V value) {
        K key = parseKey(value);
        if (isBinaryMode()) {
            container.put(String.valueOf(key), toBinary(value));
        } else {
            container.put(String.valueOf(key), ParseUtils.toJSONString(value));
        }
        return key;
    }

    protected void purgeContainer() {
        container.clear();
    }

    @Override
    protected void innerAdd(V value) {
        persist(value);
        cache(value);
    }

    @Override
    protected void innerUpdate(V value) {
        persist(value);
        cache(value);
    }

    @Override
    protected void innerRemove(V value) {
        K key = parseKey(value);
        uncache(key);
        container.remove(String.valueOf(key));
    }

    protected Class viewClass() {
        return WebSocketView.class;
    }
}
