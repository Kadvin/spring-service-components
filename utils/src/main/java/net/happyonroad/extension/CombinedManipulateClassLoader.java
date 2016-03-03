package net.happyonroad.extension;

import net.happyonroad.component.classworld.ManipulateClassLoader;

import java.net.URL;
import java.util.List;
import java.util.Set;

/**
 * <h1>Combined Manipulate ClassLoader</h1>
 *
 * @author Jay Xiong
 */
public class CombinedManipulateClassLoader extends ManipulateClassLoader {
    private final List<ExtensionClassLoader> depends;

    public CombinedManipulateClassLoader(ManipulateClassLoader parent, List<ExtensionClassLoader> depends) {
        super(parent);
        this.depends = depends;
        for (ExtensionClassLoader depend : depends) {
            innerAddURL(depend.getComponent().getURL());
        }
    }

    @Override
    public void addURLs(Set<URL> urls) {
        ((ManipulateClassLoader)getParent()).addURLs(urls);
    }

    @Override
    public void addURL(URL url) {
        this.depends.get(0).addURL(url);
    }
}
