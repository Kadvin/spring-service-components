/**
 * @author Administrator, Date: 13-7-9
 */
package net.happyonroad.jar;

import net.happyonroad.jar.PlainJarStorage;
import net.happyonroad.test.TestSupport;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

/**
 * 测试对普通jar文件的加载/释放能力
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
public class PlainJarStorageTest extends TestSupport {

    private PlainJarStorage storage;

    public PlainJarStorageTest() {
        try {
            File file = zip("jar_resource_test.jar");
            JarFile jarFile = new JarFile(file);
            storage = new PlainJarStorage(jarFile);
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
     *   2.了解普通的jar资源存在
     *   3.嵌套jar中的资源不存在，即便该嵌套jar包被声明为依赖
     *   4.实际资源内容未被加载到内存里面
     */
    public void testPreLoad() throws Exception {
        assertTrue(storage.hasResource("top.xml"));
        assertFalse(storage.isLoaded("top.xml"));
        assertTrue(storage.hasResource("deep.xml"));
        assertFalse(storage.isLoaded("deep.xml"));
        assertTrue(storage.hasResource("lib/depend-1.jar"));
        assertFalse(storage.isLoaded("lib/depend-1.jar"));
        assertFalse(storage.hasResource("deep-1.xml"));
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
        assertNotNull(storage.getResource("top.xml"));
        assertTrue(storage.isLoaded("top.xml"));
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
        assertNotNull(storage.getResource("top.xml"));
        assertTrue(storage.isLoaded("top.xml"));
        storage.clear();
        assertFalse(storage.isLoaded("top.xml"));
        assertNotNull(storage.getResource("top.xml"));
        assertTrue(storage.isLoaded("top.xml"));
    }

    /**
     * 测试：
     *   加载后的资源，可以被GC回收；回收之后，下次再获取还可以重新加载出来
     * 验证方式：
     *   1.资源可以获取得到
     *   2.获取之后，资源为已加载状态
     *   3.垃圾回收之后，资源为未加载状态
     *   4.再次获取，会重新加载
     */
    public void testResourceCanBeRecycled() throws Exception {
        //便于测试，就是当GC的时候就释放，而不是像SoftReference那样，要等到内存真的不够才释放
        PlainJarStorage.WEAK = true;

        assertNotNull(storage.getResource("top.xml"));
        assertTrue(storage.isLoaded("top.xml"));
        System.gc();
        assertFalse(storage.isLoaded("top.xml"));
        assertNotNull(storage.getResource("top.xml"));
        assertTrue(storage.isLoaded("top.xml"));
    }
}
