/**
 * Developer: Kadvin Date: 14-2-27 下午3:28
 */
package dnt.util;

import java.rmi.RemoteException;

/**
 * RmiServiceExporter just for tell outer some service is exported
 */
public class RmiServiceExporter extends org.springframework.remoting.rmi.RmiServiceExporter {
    private String name;
    private int    port;
    private String host;

    @Override
    public void afterPropertiesSet() throws RemoteException {
        super.afterPropertiesSet();
        logger.info("Exported " + getServiceInterface().getSimpleName() + " at " + getServiceUrl());
    }

    public String getServiceUrl() {
        if (host == null) host = "127.0.0.1";
        return "rmi://" + host + ":" + port + "/" + name;
    }

    @Override
    public void setServiceName(String serviceName) {
        super.setServiceName(serviceName);
        this.name = serviceName;
    }

    @Override
    public void setRegistryPort(int registryPort) {
        super.setRegistryPort(registryPort);
        this.port = registryPort;
    }

    @Override
    public void setRegistryHost(String registryHost) {
        super.setRegistryHost(registryHost);
        this.host = registryHost;
    }
}
