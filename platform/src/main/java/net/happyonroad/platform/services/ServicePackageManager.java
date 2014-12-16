/**
 * Developer: Kadvin Date: 14-7-14 下午5:55
 */
package net.happyonroad.platform.services;

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
import net.happyonroad.spring.ApplicationSupportBean;
import org.springframework.beans.factory.access.BootstrapException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The service package manager
 */
public class ServicePackageManager extends ApplicationSupportBean
        implements ApplicationListener<ContainerEvent>, FilenameFilter {

    @Autowired
    private ComponentLoader     componentLoader;
    @Autowired
    private ComponentContext    componentContext;
    @Autowired
    private ComponentRepository componentRepository;

    List<Component> loadedServicePackages = new LinkedList<Component>();


    @Override
    public void onApplicationEvent(ContainerEvent event) {
        if (event instanceof ContainerStartedEvent) {
            try {
                loadServicePackages();
                publish(new ServicePackagesEvent.LoadedEvent(this));
            } catch (Exception e) {
                throw new BootstrapException("Can't load service packages", e);
            }
        } else if (event instanceof ContainerStoppingEvent) {
            try {
                unloadServicePackages();
                publish(new ServicePackagesEvent.UnloadedEvent(this));
            } catch (Exception e) {
                throw new ApplicationContextException("Can't unload service packages", e);
            }
        }

    }

    void loadServicePackages() throws Exception {
        File repository = new File(System.getProperty("app.home"), "repository");
        File[] packageJars = repository.listFiles(this);
        if (packageJars == null)
            packageJars = new File[0]; /*也可能在目录下没有jar*/
        logger.debug("Loading {} service packages from: {}", packageJars.length, repository.getAbsolutePath());
        // sort the model packages by them inner dependency
        //componentRepository.sortCandidates(packageJars);
        //outputPackageJars(packageJars);
        // TODO  不知道为什么，排序结果中会出错
        // 临时规则，让系统能先正常的跑起来，以后再来解决
        Arrays.sort(packageJars, new ServicePackageComparator());
        outputPackageJars(packageJars);
        for (File jar : packageJars) {
            loadServicePackage(jar);
        }
    }

    private void outputPackageJars(File[] packageJars)
            throws DependencyNotMeetException, InvalidComponentNameException {
        StringBuilder sb = new StringBuilder();
        for (File packageJar : packageJars) {
            Component pkg = componentRepository.resolveComponent(packageJar.getName());
            sb.append("\t").append(pkg.getBriefId()).append("\n");
        }
        logger.debug("Sorted service packages is list as: \n{}", sb);
    }

    void loadServicePackage(File jar)
            throws InvalidComponentNameException, DependencyNotMeetException, ServicePackageException {
        Dependency dependency = Dependency.parse(jar.getName());
        Component component = componentRepository.resolveComponent(dependency);
        try {
            logger.info("Loading service package: {}", component);
            //仅发给容器
            publish(new ServicePackageEvent.LoadingEvent(component));
            componentLoader.load(component);
            loadedServicePackages.add(component);
            DefaultComponent comp = (DefaultComponent) component;
            registerMbean(comp, comp.getObjectName());
            publish(new ServicePackageEvent.LoadedEvent(component));
            logger.info("Loaded  service package: {}", component);
        } catch (Exception e) {
            logger.error("Can't load service package: " + component + ", ignore it and going on", e);
        }
    }


    void unloadServicePackages() throws Exception {
        List<Component> servicePackages = new LinkedList<Component>(loadedServicePackages);
        componentRepository.sortComponents(servicePackages);
        Collections.reverse(servicePackages);
        for (Component component : servicePackages) {
            unloadServicePackage(component);
        }
    }

    void unloadServicePackage(Component component) {
        logger.info("Unloading service package: {}", component);
        publish(new ServicePackageEvent.UnloadingEvent(component));
        componentLoader.unloadSingle(component);
        loadedServicePackages.remove(component);
        //这个事件就仅发给容器
        publish(new ServicePackageEvent.UnloadedEvent(component));
        logger.info("Unloaded  service package: {}", component);
    }

    protected void publish(ApplicationEvent event) {
        ApplicationContext mainApp = componentContext.getMainApp();
        if (mainApp != null)
            //通过 pom class world's main component 进行广播
            mainApp.publishEvent(event);
        else//通过当前(platform application context) 向外广播
            applicationContext.publishEvent(event);
        //向业已加载的服务包广播事件，为了避免上层容器收到额外的消息，不向他们的parent发送
        for (Component component : loadedServicePackages) {
            ApplicationContext app = component.getApplication();
            if (app != null) {
                ApplicationEventMulticaster multicaster = app.getBean(ApplicationEventMulticaster.class);
                multicaster.multicastEvent(event);
            }
        }
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
