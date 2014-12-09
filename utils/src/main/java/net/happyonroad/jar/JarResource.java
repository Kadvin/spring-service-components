/**
 * @author XiongJie, Date: 13-8-28
 */
package net.happyonroad.jar;

import net.happyonroad.util.BytesURLConnection;
import net.happyonroad.util.PatternUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xeustechnologies.jcl.exception.JclException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.*;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Pattern;

/**
 * 基本的Jar资源类，它代表一个Jar包对象，与JDK一般的 JarFile相比
 * 它具有根据Manifest里面的定义的class-path属性指示，额外加载jar的能力
 * 另外，它是Lazy Load的，但不带有任何探针包的特性
 *
 * @author XiongJie(Kadvin)
 * @since 2013/7/1
 */
public class JarResource {
    private static Logger logger = LoggerFactory.getLogger(JarResource.class);

    protected JarFile file;
    protected Manifest manifest;
    protected PlainJarStorage mainResources;
    //所有待加载的内部jar的路径
    protected String[] classPaths;
    //内部加载之后的jar resources， 如果对应的资源无法加载，也会在这其中保留一个null对象
    protected JarStorage[] classPathResources;
    private JarStorage[] allResources;

    public JarResource(File file) {
        try {
            Validate.notNull(file, "The jar file must presence");
            this.file = new JarFile(file);
            this.manifest = this.file.getManifest();
            this.classPaths = this.parseClassPaths(manifest);
            //设置为一个同长度的数组，但其中内容为空
            this.classPathResources = new JarStorage[this.classPaths.length];
        } catch (IOException e) {
            throw new IllegalArgumentException("Bad file: " + file.getPath(), e);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 主要对外公开API
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 判断其中是否有特定的资源
     *
     * @param resourcePath 资源路径
     * @return 判断结果
     */
    public boolean hasResource(String resourcePath) {
        Validate.isTrue(this.isLoaded(), "You should load the jar resource before " + getClass().getSimpleName() + "#getResource!");
        for (JarStorage resources : this.allResources) {
            boolean found = resources.hasResource(resourcePath);
            if (found) return true;
        }
        return false;
    }

    /**
     * 获取特定的资源
     *
     * @param resourcePath 资源路径，该路径也可以是其内部Manifest.MF的Class-Path指定的jar中的文件路径
     * @return 资源流
     */
    public byte[] getResource(String resourcePath) {
        Validate.isTrue(this.isLoaded(), "You should load the jar resource before " + getClass().getSimpleName() + "#getResource!");
        for (JarStorage resources : this.allResources) {
            boolean found = resources.hasResource(resourcePath);
            if (found) return resources.getResource(resourcePath);
        }
        return null;
    }

    /**
     * 获取特定资源的读取流
     *
     * @param resourcePath 资源路径
     * @return 资源流
     */
    public InputStream getInputStream(String resourcePath) {
        Validate.isTrue(this.isLoaded(), "You should load the jar resource before " + getClass().getSimpleName() + "#getResource!");
        boolean found = this.hasResource(resourcePath);
        if (found){
            byte[] bytes = this.getResource(resourcePath);
            return new ByteArrayInputStream(bytes);
        }
        return null;
    }

    /**
     * 获取到某个资源的URL对象
     *
     * @param resourcePath 资源表征路径
     * @return 被封装的URL对象
     */
    public URL findResource(String resourcePath) {
        Validate.isTrue(this.isLoaded(), "You should load the jar resource before " + getClass().getSimpleName() + "#getResource!");
        boolean found = hasResource(resourcePath);
        if (!found) return null;
        JarStorage storage = actualStorage(resourcePath);
        return wrapBytesAsURL(storage.contextURL(), resourcePath, getResource(resourcePath));
    }

    /**
     * <pre>
     * 获取到某个资源名称对应的所有资源，之所以会出现一个资源对应多个，是因为jar中有jar，
     * 如果外部jar和内部jar都有同名的资源；或者几个内部jar中有同名的资源，这都会出现一个资源对应多个
     *
     *   loader.getResources("path/to/resource")
     *   返回 a.jar!/path/to/resource，b.jar!/path/to/resource
     *
     * 另外支持以Pattern模式查找多个符合该Pattern的资源
     *   主要是为了探针包预加载groovy文件使用
     *   如 loader.getResource("script/** /*.groovy")
     *
     * </pre>
     *
     * 新的语义为传入pattern，pattern支持的语义为：
     * <ul>
     * <li>** 任意字符串，包括目录分隔符 /
     * <li>* 除目录分隔符之外的任意字符串
     * <li>? 任意字符
     * </ul>
     *
     * @param resourcePath 资源表征路径
     * @return 被封装的URL枚举对象
     */
    public Enumeration<URL> findResources(String resourcePath) {
        Validate.isTrue(this.isLoaded(), "You should load the jar resource before " + getClass().getSimpleName() + "#getResource!");
        Vector<URL> urls = new Vector<URL>();
        if( resourcePath.indexOf('?' ) >= 0 || resourcePath.indexOf('*') >=0 ){
            Pattern pattern = PatternUtils.compileResource(resourcePath);
            //按照现有的情况，直接从mainResource里面寻找预加载资源性能足矣
            //所以没有便利所有的resource，也就是说，jar包里面的jar的资源，
            //暂时无法通过这个api预加载，如果有这个需求，请将此处的代码改为
            //便利allResource并加载
            Collection<String> resources = mainResources.filter(pattern);
            for (String resource : resources) {
                digResource(mainResources, urls, resource);
            }
        }else{
            digResource(mainResources, urls, resourcePath);
            for (JarStorage resources : this.classPathResources) {
                digResource(resources, urls, resourcePath);
            }
        }
        return urls.elements();
    }

    /**
     * 寻找特定路径的资源，并放入urls容器中
     *
     * @param urls 容器
     * @param resourcePath 资源的路径
     */
    private void digResource(JarStorage storage, Vector<URL> urls, String resourcePath){
        boolean found= storage.hasResource(resourcePath);
        if (found) {
            URL url = wrapBytesAsURL(storage.contextURL(), resourcePath, storage.getResource(resourcePath));
            urls.add(url);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 次要对外API
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 返回背后的Jar文件
     *
     * @return jar文件
     */
    public File getFile() {
        return new File(file.getName());
    }

    /**
     * 返回背后的Jar文件路径
     *
     * @return 路径
     */
    public String getFilePath() {
        return file.getName();
    }

    /**
     * 获取该Jar的摘要信息，无需加载就可以开始读取摘要信息
     *
     * @return 摘要信息
     */
    public Manifest getManifest() {
        return manifest;
    }

    /**
     * 获取其Manifest.MF文件指定的Class-Path属性
     * <ul>
     * <li> 该属性暂不支持通配符，写为: lib/*.jar并不会得到支持
     * </ul>
     * 另外对于直接使用该类，而不是探针包对应类的情况，需要注意：
     * <ul>
     * <li> 没有默认值: META-INF\lib\**\*.jar并不会被自动加载
     * </ul>
     *
     * @return 内部 class path
     */
    protected String[] getClassPaths() {
        return this.classPaths;
    }

    protected String[] parseClassPaths(Manifest manifest) {
        if (manifest != null) {
            String paths = manifest.getMainAttributes().getValue("Class-Path");
            if (paths == null)
                return new String[0];
            else
                return StringUtils.split(paths);
        } else {
            return new String[0];
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 生命周期方法
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 加载jar中的资源到内存中
     * 本类的实例构造时，并不会默认加载，使用前请先调用该方法
     */
    public void load() {
        loadMainJarByFile(this.file.getName());
        loadInnerJarByClassPath(this.classPaths);
    }

    /**
     * 释放Jar中的资源
     */
    public void release() {
        try {
            this.file.close();
            for (JarStorage storage : allResources) {
                storage.clear();
            }
        } catch (IOException ioe) {
            logger.warn("Failed to close jar file: " + ioe.getMessage(), ioe);
        } finally {
            //必须释放该对象，否则不能完全释放对jar file的流
            this.manifest = null;
            mainResources = null;
            this.classPathResources = null;
            this.allResources = null;
        }
    }

    /**
     * 判断该jar resource是否已经被加载
     *
     * @return 是否被加载
     */
    public boolean isLoaded() {
        return this.mainResources != null;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 内部实现方法
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected void loadMainJarByFile(String path) {
        mainResources = new PlainJarStorage(this.file);
        mainResources.preLoad();
    }

    protected void loadInnerJarByClassPath(String[] classPaths) {
        List<JarStorage> list = new ArrayList<JarStorage>(classPaths.length);
        for (String classPath : classPaths) {
            if (mainResources.hasResource(classPath)) {
                try {
                    JarStorage storage = new NestedJarStorage(mainResources, classPath);
                    storage.preLoad();
                    list.add(storage);
                } catch (JclException ex) {
                    logger.warn("Error while loading inner jar resource: {}", classPath);
                    list.add(null);
                }
            } else {
                list.add(null);
            }
        }
        this.classPathResources = list.toArray(new JarStorage[list.size()]);
        this.allResources = new JarStorage[list.size() + 1];
        this.allResources[0] = mainResources;
        System.arraycopy(this.classPathResources, 0, this.allResources, 1, this.classPathResources.length);
    }

    /**
     * 找出资源的实际存储对象
     *
     * @param resourcePath 资源外部路径， 如 xx.xml
     * @return 资源存储对象
     */
    JarStorage actualStorage(String resourcePath){
        Validate.isTrue(this.isLoaded(), "You should load the jar resource before #mapInnerPath!");
        if (this.mainResources.hasResource(resourcePath)) return this.mainResources;
        for (int i = 0; i < classPaths.length; i++) {
            JarStorage storage = this.classPathResources[i];
            if (storage.hasResource(resourcePath)) return storage;
        }
        return null;
    }

    URL wrapBytesAsURL(URL contextURL, String path, final byte[] bytes) {
        try {
            //暂时不清楚这样出来的url，外部的jar后缀名后面会不会再带上一个感叹号
            return new URL(contextURL, path, new URLStreamHandler() {
                @Override
                protected URLConnection openConnection(URL u) throws IOException {
                    return new BytesURLConnection(u, bytes);
                }
            });
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Through we have the resource data, but can't convert it as url", e);
        }
    }

    public String dump() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.file.getName()).append("!\n");
        this.dumpKeys(sb, 1, this.mainResources);
        sb.append("\n");
        for (int i = 0; i < classPaths.length; i++) {
            String classPath = classPaths[i];
            JarStorage resources = classPathResources[i];
            sb.append("\t").append(classPath).append("!\n");
            if (resources != null) {
                this.dumpKeys(sb, 2, resources);
            } else {
                sb.append("\tWithout Inner Resources!");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private void dumpKeys(StringBuffer sb, int indent, JarStorage resources) {
        Object[] keys = resources.getResourceKeys().toArray();
        for (Object key : keys) {
            sb.append(StringUtils.repeat("\t", indent)).append(key).append("\n");
        }
    }
}
