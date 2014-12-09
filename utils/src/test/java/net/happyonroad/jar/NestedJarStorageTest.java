/**
 * @author Administrator, Date: 13-7-9
 */
package net.happyonroad.jar;

import net.happyonroad.jar.NestedJarStorage;
import net.happyonroad.jar.PlainJarStorage;
import net.happyonroad.test.TestSupport;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

/**
 * 测试对嵌套ar文件的加载/释放能力
 * <pre>
 * Jar 内容为:
 *  jar_resource_test.jar
 *    |- META-INF
 *    |   |- Manifest.MF
 *    |   |   |-> Class-Path: lib/depend-1.jar lib/depend-2.jar vendor/depend-3.jar
 *    |   |- package.xml
 *    |- top.xml
 *    |- deep.xml
 *    |- lib
 *    |   |-depend-1.jar
 *    |   |       |- deep.xml
 *    |   |       |- deep-1.xml
 *    |   |       |- deep-x.xml
 *    |   |-depend-2.jar
 *    |   |       |- deep.xml
 *    |   |       |- deep-2.xml
 *    |   |       |- deep-x.xml
 *    |- vendor
 *    |   |-depend-3.jar
 *    |   |       |-META-INF/Manifest.MF
 *    |   |          |-> Class-Path: nested.jar
 *    |   |       |- deep-x.xml
 *    |   |       |- deep-y.xml
 *    |   |       |- nested.jar
 *    |   |                  |- nest.xml
 *    |- other
 *    |   |- cannot-touched.jar
 *    |   |       |- deep.xml
 *    |   |       |- deep-x.xml
 *    |   |       |- deep-y.xml
 *    |   |       |- deep-z.xml
 * </pre>
 *
 * @author XiongJie(Kadvin)
 * @since 2013/7/1
 */
public class NestedJarStorageTest extends TestSupport {
    private NestedJarStorage storage;

    public NestedJarStorageTest() {
        try {
            File file = zip("jar_resource_test.jar");
            JarFile jarFile = new JarFile(file);
            PlainJarStorage parent = new PlainJarStorage(jarFile);
            parent.preLoad();

            storage = new NestedJarStorage(parent, "lib/depend-1.jar");
            storage.preLoad();
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    ////////////////////////////////////////////////////////////////
    // Test Context: preLoad,hasResource,isLoaded
    ////////////////////////////////////////////////////////////////

    /**
     * 测试：
     *   预加载后，了解Jar包中有哪些资源
     * 验证方式：
     *   1.了解普通的xml资源存在
     *   3.实际资源内容未被加载到内存里面
     *   2.上级的资源不在
     */
    public void testPreLoad() throws Exception {
        assertTrue(storage.hasResource("deep-1.xml"));
        assertFalse(storage.isLoaded("deep-1.xml"));
        assertFalse(storage.hasResource("top.xml"));
    }

    ////////////////////////////////////////////////////////////////
    // Test Context: getResource
    ////////////////////////////////////////////////////////////////

    /**
     * 测试：
     *   预加载后，获取存在的资源
     * 验证方式：
     *   1.资源可以获取得到
     *   2.获取之后，资源为已加载状态
     */
    public void testGetResource() throws Exception {
        assertNotNull(storage.getResource("deep-1.xml"));
        assertTrue(storage.isLoaded("deep-1.xml"));
    }

    /**
     * 测试：
     *   加载后的资源，可以被主动释放；释放之后还可以继续加载
     * 验证方式：
     *   1.资源可以获取得到
     *   2.获取之后，资源为已加载状态
     *   3.垃圾回收之后，资源为未加载状态
     *   4.再次获取，会重新加载
     */
    public void testClear() throws Exception {
        assertNotNull(storage.getResource("deep-1.xml"));
        assertTrue(storage.isLoaded("deep-1.xml"));
        storage.clear();
        assertFalse(storage.isLoaded("deep-1.xml"));
        assertNotNull(storage.getResource("deep-1.xml"));
        assertTrue(storage.isLoaded("deep-1.xml"));
    }

    /**
     * 测试：
     *   加载后的资源，可以被主动释放；回收之后，下次再获取还可以重新加载出来
     * 验证方式：
     *   1.资源可以获取得到
     *   2.获取之后，资源为已加载状态
     *   3.垃圾回收之后，资源为未加载状态
     *   4.再次获取，会重新加载
     */
    public void testResourceCanBeRecycled() throws Exception {
        //便于测试，就是当GC的时候就释放，而不是像SoftReference那样，要等到内存真的不够才释放
        PlainJarStorage.WEAK = true;

        assertNotNull(storage.getResource("deep-1.xml"));
        assertTrue(storage.isLoaded("deep-1.xml"));
        System.gc();
        assertFalse(storage.isLoaded("deep-1.xml"));
        assertNotNull(storage.getResource("deep-1.xml"));
        assertTrue(storage.isLoaded("deep-1.xml"));
    }

}
