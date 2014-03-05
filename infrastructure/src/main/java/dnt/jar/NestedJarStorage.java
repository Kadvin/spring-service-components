/**
 * @author XiongJie, Date: 13-8-28
 */
package dnt.jar;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * 代表一个Jar中的Jar文件存取器
 */
public class NestedJarStorage extends JarStorage {
    private static Logger logger = LoggerFactory.getLogger(NestedJarStorage.class);
    private JarStorage storage;
    private String innerJarPath;
    private URL url;

    public NestedJarStorage(JarStorage parent, String innerJarPath) {
        this.storage = parent;
        this.innerJarPath = innerJarPath;
        try {
            url = new URL(storage.contextURL().toString() + this.innerJarPath + "!/");
            //return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Can't convert file as url", e);
        }
    }

    @Override
    void preLoad() {
        byte[] total = storage.getResource(this.innerJarPath);
        Validate.notNull(total, "The inner jar `" + this.innerJarPath + "`is not exist or can't be read!");
        JarInputStream jis = null;
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(total);
        try {
            jis = new JarInputStream(byteArrayInputStream);
            JarEntry jarEntry;
            while (( jarEntry = jis.getNextJarEntry() ) != null) {
                if (jarEntry.isDirectory()) continue;
                contents.put(jarEntry.getName(), null);
            }
        } catch (IOException e) {
            logger.warn("Failed to pre load `{}` from storage: `{}`", this.innerJarPath, storage.contextURL());
        } finally {
            IOUtils.closeQuietly(byteArrayInputStream);
            IOUtils.closeQuietly(jis);
        }
    }

    @Override
    byte[] lazyLoad(String resourceName) {
        byte[] total = storage.getResource(this.innerJarPath);
        Validate.notNull(total, "The inner jar `" + this.innerJarPath + "`is not exist or can't be read!");
        ByteArrayInputStream bis = null;
        JarInputStream jis = null;
        try {
            bis = new ByteArrayInputStream(total);
            jis = new JarInputStream(bis);
            JarEntry jarEntry;
            while (( jarEntry = jis.getNextJarEntry() ) != null) {
                if (jarEntry.isDirectory()) continue;
                if(jarEntry.getName().equals(resourceName)){
                    ByteArrayOutputStream out = null;
                    byte[] b = new byte[2048];
                    try {
                        out = new ByteArrayOutputStream();
                        int len = 0;
                        while (( len = jis.read( b ) ) > 0) {
                            out.write( b, 0, len );
                        }
                        return out.toByteArray();
                    } finally {
                        IOUtils.closeQuietly(out);
                    }
                }
            }
        } catch (IOException e) {
            logger.warn("Failed to lazy load `{}` from storage: `{}`", resourceName, storage.contextURL());
        } finally {
            IOUtils.closeQuietly(bis);
            IOUtils.closeQuietly(jis);
        }
        return null;
    }

    @Override
    URL contextURL() {
        return url;
    }

}
