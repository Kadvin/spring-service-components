/**
 * @author XiongJie, Date: 13-11-1
 */
package dnt.test.user;

import dnt.test.DemoRemoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/** 演示使用远程服务的对象 */
@Component
public class DemoRemoteServiceUser {
    @Autowired
    @Qualifier("demoRemoteServiceFactoryBean")
    private DemoRemoteService remoteService;

    public String work() {
        return remoteService.sayHello("DNT");
    }

    public DemoRemoteService getRemoteService() {
        return remoteService;
    }
}
