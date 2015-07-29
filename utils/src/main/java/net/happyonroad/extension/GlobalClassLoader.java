/**
 * Developer: Kadvin Date: 14/12/30 下午8:18
 */
package net.happyonroad.extension;

import net.happyonroad.component.classworld.MainClassLoader;
import net.happyonroad.component.classworld.ManipulateClassLoader;
import net.happyonroad.component.core.Component;
import net.happyonroad.component.core.support.ComponentURLStreamHandlerFactory;
import net.happyonroad.service.ExtensionContainer;
import net.happyonroad.util.StringUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * <h1>能够从所有的组件中加载类的加载器</h1>
 * <p/>
 * Web应用需要根据类的名称反射类对象，如果类是由服务包定义的，Jetty/WebServer
 * 默认的类加载器(platform class loader)无法加载到这些类
 * 即便是总体的类加载器(release class loader)也无法加载到这些类
 */
public class GlobalClassLoader extends ClassLoader implements Observer {
    private static GlobalClassLoader          instance;

    private final  ExtensionContainer         container;
    //按照依赖关系倒序排列
    //  依赖别人最多的最先被检查
    private        List<ExtensionClassLoader> ecls;

    public GlobalClassLoader(ClassLoader parent, ExtensionContainer container) {
        super(parent);
        this.container = container;
        this.container.addObserver(this);
        instance = this;
    }

    public static GlobalClassLoader getInstance() {
        return instance;
    }

    public static synchronized ClassLoader getDefaultClassLoader(){
        GlobalClassLoader gcl = getInstance();
        if( gcl == null )
            return MainClassLoader.getInstance();
        return gcl;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // find in parent class loader first
        try {
            return super.loadClass(name, resolve);
        } catch (ClassNotFoundException e) {
            //skip

            for (ExtensionClassLoader ecl : ecls()) {
                try {
                    if (ecl == null) continue;
                    return ecl.loadClass(name);
                } catch (ClassNotFoundException ex) {
                    //try next
                }
            }
            throw e;
        }
    }

    @Override
    public URL getResource(String name) {
        URL url = super.getResource(name);
        if( url == null ){
            for (ExtensionClassLoader ecl : ecls()) {
                if (ecl == null) continue;
                url = ecl.getResource(name);
                if(url != null ) return url;
            }
        }
        return url;
    }

    protected synchronized List<ExtensionClassLoader> ecls() {
        if (ecls == null || ecls.isEmpty() ) {
            List<Component> components = container.getExtensions();
            if (!components.isEmpty()) {
                ecls = new ArrayList<ExtensionClassLoader>(components.size());
                for (Component component : components) {
                    if( component.getClassLoader() instanceof ExtensionClassLoader )
                        ecls.add((ExtensionClassLoader) component.getClassLoader());
                }
                Collections.reverse(ecls);
            }else{
                //noinspection unchecked
                return Collections.EMPTY_LIST;
            }
        }
        return ecls;
    }

    @Override
    public synchronized void update(Observable o, Object arg) {
        this.ecls = null;// last update
    }

    public String getClassPath() {
        Set<URL> urls = new LinkedHashSet<URL>();
        ClassLoader parent = getParent();
        if( parent instanceof MainClassLoader){
            urls.addAll(((MainClassLoader) parent).getSysUrls());
            urls.addAll(((MainClassLoader) parent).getMainUrls());
        }else if( parent instanceof URLClassLoader){
            urls.addAll(Arrays.asList(((URLClassLoader) parent).getURLs()));
        }
        for (ExtensionClassLoader loader : ecls()) {
            urls.addAll(Arrays.asList(loader.getURLs()));
        }
        Set<String> paths = new LinkedHashSet<String>();
        ComponentURLStreamHandlerFactory factory = ComponentURLStreamHandlerFactory.getFactory();
        for (URL url : urls) {
            if ("component".equals(url.getProtocol())) {
                try {
                    String path = factory.getMappingFile(url).getAbsolutePath();
                    path = FilenameUtils.normalize(path);
                    paths.add(path);
                } catch (IOException e) {
                    System.err.println("Can't find mapping file for " + url + ", " + e.getMessage());
                }
            } else {
                paths.add(FilenameUtils.normalize(url.getFile()));
            }
        }
        return StringUtils.join(paths, File.pathSeparator);
    }

    private ManipulateClassLoader parentFor(Component component) {
        List<ExtensionClassLoader> depends = findDepends(component);
        if( depends.isEmpty() )
            return MainClassLoader.getInstance();
        if(depends.size() == 1 ){
            return depends.get(0);
        }else {
            return new CombinedManipulateClassLoader(MainClassLoader.getInstance(), depends);
        }
    }

    private List<ExtensionClassLoader> findDepends(Component component) {
        List<ExtensionClassLoader> found = new ArrayList<ExtensionClassLoader>();
        for (ExtensionClassLoader cl: ecls()) {
            Component checking = cl.getComponent();
            if( checking == null ) continue;
            if(component.dependsOn(checking)) {
                boolean depended = false;
                //将这个被依赖的组件和已经找到的依赖进行比较
                Iterator<ExtensionClassLoader> it = found.iterator();
                while (it.hasNext()) {
                    ExtensionClassLoader exist = it.next();
                    Component existComponent = exist.getComponent();
                    if( existComponent.dependsOn(checking)){
                        //在依赖链里面了，就不用添加进去
                        depended = true;
                        break;
                    } else if (checking.dependsOn(existComponent)){
                        //已有组件被它依赖，把已有组件移除掉，待会儿添加它
                        depended = false;
                        it.remove();
                    }
                }
                if( !depended )
                    found.add(cl);
            }
        }
        return found;
    }

    public static ManipulateClassLoader parentClassLoad(Component component) {
        if( instance == null )
            return MainClassLoader.getInstance();
        return instance.parentFor(component);
    }
}
