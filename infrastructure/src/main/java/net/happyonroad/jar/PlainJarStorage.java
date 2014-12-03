/**
 * @author XiongJie, Date: 13-8-28
 */
package net.happyonroad.jar;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 普通的Jar文件存储读取器
 */
public class PlainJarStorage extends JarStorage {
    private static Logger logger = LoggerFactory.getLogger(PlainJarStorage.class);
    /**
     * 可能有多个JarStorage依赖同一个 jarFile
     * 所以，这个JarFile的close不归属JarStorage处理
     */
    protected JarFile jarFile;
    private URL contextUrl;

    public PlainJarStorage(JarFile file) {
        this.jarFile = file;
        try {
            contextUrl = new URL(new File(jarFile.getName()).toURI().toURL() + "!/");
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Can't convert file as url", e);
        }
    }

    @Override
    void preLoad() {
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            if (jarEntry.isDirectory()) continue;
            contents.put(jarEntry.getName(), null);
        }
    }

    URL contextURL() {
        return contextUrl;
    }

    @Override
    byte[] lazyLoad(String resourceName) {
        InputStream input = null;
        try {
            JarEntry entry = jarFile.getJarEntry(resourceName);
            if (entry.isDirectory()) {
                throw new IllegalArgumentException("The resource `" + resourceName + "` is a dictionary");
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            input = jarFile.getInputStream(entry);
            IOUtils.copy(input, out);
            return out.toByteArray();
        } catch (IOException ioe){
            logger.warn("Can't lazy load resource: `{}` from jar file: `{}`", resourceName, jarFile.getName());
            return null;
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    /**
     * 当探针包等封装对象移动后，重新校正这个引用
     *
     * @param jarFile jar 文件
     */
    public void setJarFile(JarFile jarFile) {
        this.jarFile = jarFile;
    }
}
