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
import net.happyonroad.util.MiscUtils;
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

    public ExtensionManager() {
        setOrder(100);
    }

    public List<Component> getExtensions() {
        return Collections.unmodifiableList(loadedExtensions);
    }

    @Override
    public void onApplicationEvent(ContainerEvent event) {
        if (event instanceof ContainerStartedEvent) {
            try {
                long start = System.currentTimeMillis();
                loadExtensions();
                logger.info("Loaded  extensions took {}", formatDurationHMS(System.currentTimeMillis() - start));
                publishEvent(new SystemStartedEvent(componentContext));
            } catch (Exception e) {
                throw new BootstrapException("Can't load extensions", e);
            }
        } else if (event instanceof ContainerStoppingEvent) {
            try {
                publishEvent(new SystemStoppingEvent(componentContext));
                long start = System.currentTimeMillis();
                unloadExtensions();
                logger.info("Unloaded  extensions took {}", formatDurationHMS(System.currentTimeMillis() - start));
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

    public Component loadExtension(File file) throws ExtensionException {
        Component component;
        try {
            Dependency dependency = Dependency.parse(file);
            component = componentRepository.resolveComponent(dependency);
        } catch (InvalidComponentNameException e) {
            throw new ExtensionException("The extension file path is illegal", e);
        } catch (DependencyNotMeetException e) {
            throw new ExtensionException("There is some other depends is not meet", e);
        }
        try {
            //由于现在将扩展统一放在 repository 目录，其实有些系统依赖的组件已经加载过了
            // 现在这样只是提示下，让外部用户以为我们将其当做扩展加载
            logger.info("Loading extension: {}", component);
            long start = System.currentTimeMillis();
            if( !componentLoader.isLoaded(component) ){
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
            }else{
                loadedExtensions.add(component);
            }
            logger.info("Loaded  extension: {} ({})", component, formatDurationHMS(System.currentTimeMillis() - start));
            return component;
        } catch (Exception e) {
            logger.error("Can't load extension: " + component + ", ignore it: {}", MiscUtils.describeException(e));
            return null;
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

    public void unloadExtension(File file) throws ExtensionException{
        try {
            Dependency dependency = Dependency.parse(file);
            Component component = componentRepository.resolveComponent(dependency);
            unloadExtension(component);
        } catch (InvalidComponentNameException e) {
            throw new ExtensionException("The extension file path is illegal", e);
        } catch (DependencyNotMeetException e) {
            throw new ExtensionException("There is some other depends is not meet", e);
        }
    }

    void unloadExtension(Component component) {
        logger.info("Unloading extension: {}", component);
        long start = System.currentTimeMillis();
        if( componentLoader.isLoaded(component) ){
            publishEvent(new ExtensionUnloadingEvent(component));
            componentLoader.quickUnload(component);
            loadedExtensions.remove(component);
            //这个事件就仅发给容器
            publishEvent(new ExtensionUnloadedEvent(component));
        }else{
            loadedExtensions.remove(component);
        }
        logger.info("Unloaded  extension: {}({})", component, formatDurationHMS(System.currentTimeMillis()-start));
    }

}
