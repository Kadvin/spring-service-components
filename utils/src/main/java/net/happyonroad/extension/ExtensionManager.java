/**
 * Developer: Kadvin Date: 14-7-14 下午5:55
 */
package net.happyonroad.extension;

import net.happyonroad.component.classworld.MainClassLoader;
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
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.access.BootstrapException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationListener;

import java.io.File;
import java.util.*;

import static org.apache.commons.lang.time.DurationFormatUtils.formatDurationHMS;

/**
 * The extension manager
 */
public class ExtensionManager extends ApplicationSupportBean
        implements ApplicationListener<ContainerEvent>, ExtensionContainer {

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
        if( !repository.isDirectory() ) return;
        Collection<File> jars = FileUtils.listFiles(repository, new String[]{"jar"}, true);
        Iterator<File> it = jars.iterator();
        while (it.hasNext()) {
            File jar = it.next();
            if( !DefaultComponent.isApplication(jar.getParentFile().getName()))
                it.remove();
        }
        logger.debug("Loading {} extensions from: {}", jars.size(), repository.getAbsolutePath());
        File[] packageJars = jars.toArray(new File[jars.size()]);
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
            Component pkg = componentRepository.resolveComponent(packageJar.getPath());
            sb.append("\t").append(pkg.getBriefId()).append("\n");
        }
        logger.debug("Sorted extensions is list as: \n{}", sb);
    }

    void loadExtension(File jar)
            throws InvalidComponentNameException, DependencyNotMeetException, ExtensionException {
        Dependency dependency = Dependency.parse(jar);
        Component component = componentRepository.resolveComponent(dependency);
        try {
            logger.info("Loading extension: {}", component);
            long start = System.currentTimeMillis();
            //仅发给容器
            publishEvent(new ExtensionLoadingEvent(component));
            ClassLoader legacy = Thread.currentThread().getContextClassLoader();
            ExtensionClassLoader ecl = new ExtensionClassLoader(MainClassLoader.getInstance());
            Thread.currentThread().setContextClassLoader(ecl);
            componentLoader.load(component);
            Thread.currentThread().setContextClassLoader(legacy);
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
        List<Component> extensions = new LinkedList<Component>(loadedExtensions);
        componentRepository.sortComponents(extensions);
        Collections.reverse(extensions);
        for (Component component : extensions) {
            unloadExtension(component);
        }
    }

    void unloadExtension(Component component) {
        logger.info("Unloading extension: {}", component);
        long start = System.currentTimeMillis();
        publishEvent(new ExtensionUnloadingEvent(component));
        componentLoader.quickUnload(component);
        loadedExtensions.remove(component);
        //这个事件就仅发给容器
        publishEvent(new ExtensionUnloadedEvent(component));
        logger.info("Unloaded  extension: {}({})", component, formatDurationHMS(System.currentTimeMillis()-start));
    }

}
