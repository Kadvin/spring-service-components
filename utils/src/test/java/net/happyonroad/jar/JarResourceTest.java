package net.happyonroad.jar;

import net.happyonroad.test.TestSupport;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
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
public class JarResourceTest extends TestSupport {
    private JarResource jar;

    public JarResourceTest() {
        File file = zip("jar_resource_test.jar");
        jar = new JarResource(file);
        jar.load();
    }

    ////////////////////////////////////////////////////////////////
    // Test Context: Constructor
    ////////////////////////////////////////////////////////////////

    /**
     * 测试：
     *   传入错误对象进行构造
     * 验证方式：
     *   1.抛出异常
     */
    public void testNewWithBadFile() throws Exception {
        try {
            new JarResource(new File("not/exist/file.jar"));
            fail("It should raise error when create with bad file");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Bad file: "));
        }
    }

    /**
     * 测试：
     *   传入空对象进行构造
     * 验证方式：
     *   1.抛出异常
     */
    public void testNewWithNull() throws IOException {
        //using null
        try {
            new JarResource(null);
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException ignore) {
            assertTrue(ignore.getMessage().contains("The jar file must presence"));
        }
    }

    ////////////////////////////////////////////////////////////////
    // Test Context: #getManifest
    ////////////////////////////////////////////////////////////////

    /**
     * 测试：
     *   获取Manifest文件
     * 验证方式：
     *   1.不为空
     *   2.其ClassPath属性也应该读取到
     */
    public void testGetManifest() throws Exception {
        assertNotNull(jar.getManifest());
        assertTrue(jar.getClassPaths().length > 0);
    }

    ////////////////////////////////////////////////////////////////
    // Test Context: #hasResource
    ////////////////////////////////////////////////////////////////

    /**
     * 测试：
     *   判断 以上 NetworkDevice.jar 中的各种资源 是否存在
     * 验证方式：
     *   top.xml 应该存在
     *   deep.xml 应该存在
     *   deep-1.xml 应该存在
     *   deep-2.xml 应该存在
     *   deep-x.xml 应该存在
     *   nest.xml   不应该存在
     *   deep-z.xml 不应该存在
     */
    public void testHasResource() throws Exception{
        assertTrue(jar.hasResource("top.xml"));
        assertTrue(jar.hasResource("deep.xml"));
        assertTrue(jar.hasResource("deep-1.xml"));
        assertTrue(jar.hasResource("deep-2.xml"));
        assertTrue(jar.hasResource("deep-x.xml"));
        assertFalse(jar.hasResource("nest.xml"));
        assertFalse(jar.hasResource("deep-z.xml"));
    }
    ////////////////////////////////////////////////////////////////
    // Test Context: #getResource
    ////////////////////////////////////////////////////////////////

    /**
     * 测试：
     *   加载以上 NetworkDevice.jar 中的普通资源 top.xml
     * 验证方式：
     *   1.读取到相应的字节流
     */
    public void testGetResourceTopDotXml() throws Exception {
        assertNotNull(jar.getResource("top.xml"));
    }

    /**
     * 测试：
     *   加载以上 NetworkDevice.jar 中的 META-INF 目录中的资源
     *   (这个目录是jar的元目录，比较特殊，验证底层API没有对其特殊处理)
     * 验证方式：
     *   1.读取到相应的字节流
     */
    public void testGetResourceFromMetaInfo() throws Exception {
        assertNotNull(jar.getResource("META-INF/package.xml"));
    }

    /**
     * 测试：
     *  加载以上NetworkDevice.jar中的直接文件与依赖jar包均存在的资源
     * 验证方式：
     *  1.能读取到相应的字节流
     *  2.且真实对应的资源路径为 deep.xml， 而不是 lib/depend-1.jar!/deep.xml, lib/depend-2.jar!/deep.xml 等
     */
    public void testGetResourceDeepDotXml() throws Exception {
        assertNotNull(jar.getResource("deep.xml"));
        assertTrue(jar.findResource("deep.xml").toString().endsWith("jar_resource_test.jar!/deep.xml"));
    }

    /**
     * 测试：
     *   加载以上NetworkDevice.jar中内嵌依赖包里面的资源，且该资源是唯一的
     * 验证方式：
     *   1.能读取到相应的字节流
     */
    public void testGetResourceDeep1DotXml() throws Exception {
        assertNotNull(jar.getResource("deep-1.xml"));
        assertNotNull(jar.getResource("deep-2.xml"));
    }

    /**
     * 测试：
     *   加载以上NetworkDevice.jar中内嵌依赖包里面的资源，且多个内嵌包里面均含有该资源
     * 验证方式：
     *   1.能读取到相应的字节流
     *   2.读取到的实际资源路径为第一个jar中的资源
     *     lib/depend1-jar!/deep-x.xml
     */
    public void testGetResourceDeepXDotXml() throws Exception {
        assertNotNull(jar.getResource("deep-x.xml"));
        assertTrue(jar.findResource("deep-x.xml").toString().endsWith("jar_resource_test.jar!/lib/depend-1.jar!/deep-x.xml"));
    }

    /**
     * 测试：
     *   加载以上NetworkDevice.jar中内嵌依赖包的内嵌依赖包的资源
     * 验证方式：
     *   1.不应该读取到相应的字节流
     */
    public void testGetResourceNestDotXml() throws Exception {
        assertNull(jar.getResource("nest.xml"));
    }


    /**
     * 测试：
     *   加载以上NetworkDevice.jar中内嵌非依赖包的资源
     * 验证方式：
     *   1.不应该读取到相应的字节流
     */
    public void testGetResourceDeepZDotXml() throws Exception {
        assertNull(jar.getResource("deep-z.xml"));
    }

    ////////////////////////////////////////////////////////////////
    // Test Context: #findResource
    ////////////////////////////////////////////////////////////////

    /**
     * 测试：
     *   读取jar中不存在的资源
     * 验证方式：
     *   返回值应该为null
     */
    public void testFindResourceNotExist() throws Exception {
        assertNull(jar.findResource("not/exist/resource"));
    }

    /**
     * 测试：
     *   读取jar中存在的普通资源
     * 验证方式：
     *   1. 返回值应该不为null
     *   2. URL路径为对应的顶级路径
     */
    public void testFindResourceTopDotXml() throws Exception {
        URL url = jar.findResource("top.xml");
        assertNotNull(url);
        assertTrue(url.toString().endsWith("!/top.xml"));
    }

    public void testFindResourceFolder() throws Exception {


    }

    /**
     * 测试：
     *   读取jar中存在的内嵌资源
     * 验证方式：
     *   1. 返回值应该不为null
     *   2. URL路径为对应的jar中的jar资源路径
     */
    public void testFindResourceDeep1DotXml() throws Exception {
        URL url = jar.findResource("deep-1.xml");
        assertNotNull(url);
        assertTrue(url.toString().endsWith("!/lib/depend-1.jar!/deep-1.xml"));
    }

    ////////////////////////////////////////////////////////////////
    // Test Context: #findResources
    ////////////////////////////////////////////////////////////////

    /**
     * 测试：
     *   读取jar中存在的多个内嵌资源
     * 验证方式：
     *   1. 资源应该按照加载顺序，成为一个迭代器返回
     */
    public void testFindResourcesDeepDotXml() throws Exception {
        Enumeration<URL> resources = jar.findResources("deep.xml");
        int i = 0;
        String[] values = new String[]{
                "!/deep.xml",
                "!/lib/depend-1.jar!/deep.xml",
                "!/lib/depend-2.jar!/deep.xml",
        };
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            assertTrue(url.toString().endsWith(values[i]));
            i++;
        }
    }

    /**
     * 测试：
     *   读取jar中存在的多个内嵌元资源 (在META-INF下面)
     * 验证方式：
     *   1. 资源应该按照加载顺序，成为一个迭代器返回
     */
    public void testFindResourcesManifestDotMF() throws Exception {
        Enumeration<URL> resources = jar.findResources("META-INF/Manifest.MF");
        int i = 0;
        String[] values = new String[]{
                "!/META-INF/Manifest.MF",
                "!/vendor/depend-3.jar!/META-INF/Manifest.MF",
        };
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            assertTrue(url.toString().endsWith(values[i]));
            i++;
        }
    }

    /**
     *
     * 测试：
     *   模糊匹配方式获取资源
     * 验证方式：
     *   1. 应该返回本jar中的符合模式的资源
     *   2. 不应该返回jar中jar的符合模式的资源
     */
    public void testFindResourcesByPattern() throws Exception{
        Enumeration<URL> resources = jar.findResources("**/*.jar");
        List<URL> urls = new ArrayList<URL>();
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            urls.add(url);
        }
        assertEquals(4, urls.size());

        urls.clear();
        resources = jar.findResources("*.xml");
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            urls.add(url);
        }
        assertEquals(2, urls.size());
    }

}
