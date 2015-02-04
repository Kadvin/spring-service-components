/**
 * Developer: Kadvin Date: 14-7-14 下午5:55
 */
package net.happyonroad.extension;

import net.happyonroad.component.container.ComponentLoader;
import net.happyonroad.component.container.ComponentRepository;
import net.happyonroad.component.container.event.ContainerEvent;
import net.happyonroad.component.container.event.ContainerStartedEvent;
import net.happyonroad.component.container.event.ContainerStoppingEvent;
import net.happyonroad.component.core.Component;
import net.happyonroad.component.core.ComponentContext;
import net.happyonroad.component.core.exception.DependencyNotMeetException;
import net.happyonroad.component.core.exception.InvalidComponentNameException;
import net.happyonroad.component.core.support.DefaultComponent;
import net.happyonroad.component.core.support.Dependency;
import net.happyonroad.event.*;
import net.happyonroad.exception.ExtensionException;
import net.happyonroad.service.ExtensionContainer;
import net.happyonroad.spring.ApplicationSupportBean;
import org.springframework.beans.factory.access.BootstrapException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationListener;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.apache.commons.lang.time.DurationFormatUtils.formatDurationHMS;

/**
 * The extension manager
 */
public class ExtensionManager extends ApplicationSupportBean
        implements ApplicationListener<ContainerEvent>, FilenameFilter, ExtensionContainer {

    @Autowired
    private ComponentLoader     componentLoader;
    @Autowired
    private ComponentContext    componentContext;
    @Autowired
    private ComponentRepository componentRepository;

    //这里面的顺序是按照加载顺序
    //  最被依赖的最先被加载，排在最前面
    List<Component> loadedExtensions = new LinkedList<Component>();

    public List<Component> getExtensions() {
        return Collections.unmodifiableList(loadedExtensions);
    }

    @Override
    public void onApplicationEvent(ContainerEvent event) {
        if (event instanceof ContainerStartedEvent) {
            try {
                long start = System.currentTimeMillis();
                loadExtensions();
                publishEvent(new SystemStartedEvent(componentContext));
                logger.info("Load extensions took {}", formatDurationHMS(System.currentTimeMillis() - start));
            } catch (Exception e) {
                throw new BootstrapException("Can't load extensions", e);
            }
        } else if (event instanceof ContainerStoppingEvent) {
            try {
                long start = System.currentTimeMillis();
                publishEvent(new SystemStoppingEvent(componentContext));
                unloadExtensions();
                logger.info("Unload extensions took {}", formatDurationHMS(System.currentTimeMillis() - start));
            } catch (Exception e) {
                throw new ApplicationContextException("Can't unload extensions", e);
            }
        }

    }

    void loadExtensions() throws Exception {
        File repository = new File(System.getProperty("app.home"), "repository");
        File[] packageJars = repository.listFiles(this);
        if (packageJars == null)
            packageJars = new File[0]; /*也可能在目录下没有jar*/
        logger.debug("Loading {} extensions from: {}", packageJars.length, repository.getAbsolutePath());
        // sort the model packages by them inner dependency
        componentRepository.sortCandidates(packageJars);
        outputPackageJars(packageJars);
        for (File jar : packageJars) {
            loadExtension(jar);
        }
    }

    private void outputPackageJars(File[] packageJars)
            throws DependencyNotMeetException, InvalidComponentNameException {
        StringBuilder sb = new StringBuilder();
        for (File packageJar : packageJars) {
            Component pkg = componentRepository.resolveComponent(packageJar.getName());
            sb.append("\t").append(pkg.getBriefId()).append("\n");
        }
        logger.debug("Sorted extensions is list as: \n{}", sb);
    }

    void loadExtension(File jar)
            throws InvalidComponentNameException, DependencyNotMeetException, ExtensionException {
        Dependency dependency = Dependency.parse(jar.getName());
        Component component = componentRepository.resolveComponent(dependency);
        try {
            logger.info("Loading extension: {}", component);
            long start = System.currentTimeMillis();
            //仅发给容器
            publishEvent(new ExtensionLoadingEvent(component));
            componentLoader.load(component);
            loadedExtensions.add(component);
            DefaultComponent comp = (DefaultComponent) component;
            registerMbean(comp, comp.getObjectName());
            publishEvent(new ExtensionLoadedEvent(component));
            logger.info("Loaded  extension: {} ({})", component, formatDurationHMS(System.currentTimeMillis() - start));
        } catch (Exception e) {
            logger.error("Can't load extension: " + component + ", ignore it and going on", e);
        }
    }


    void unloadExtensions() throws Exception {
        List<Component> servicePackages = new LinkedList<Component>(loadedExtensions);
        componentRepository.sortComponents(servicePackages);
        Collections.reverse(servicePackages);
        for (Component component : servicePackages) {
            unloadExtension(component);
        }
    }

    void unloadExtension(Component component) {
        logger.info("Unloading extension: {}", component);
        long start = System.currentTimeMillis();
        publishEvent(new ExtensionUnloadingEvent(component));
        componentLoader.unloadSingle(component);
        loadedExtensions.remove(component);
        //这个事件就仅发给容器
        publishEvent(new ExtensionUnloadedEvent(component));
        logger.info("Unloaded  extension: {}({})", component, formatDurationHMS(System.currentTimeMillis()-start));
    }

    @Override
    public boolean accept(File dir, String name) {
        if (!name.endsWith(".jar")) return false;
        Dependency dependency;
        try {
            dependency = Dependency.parse(name);
        } catch (InvalidComponentNameException e) {
            return false;
        }
        String id = dependency.getArtifactId().toLowerCase();
        return !id.endsWith("_api");
    }

}
