/**
 * @author XiongJie, Date: 13-11-26
 */
package net.happyonroad.config;

import java.rmi.Naming;
import java.rmi.RemoteException;

/** A RMI Registry List */
public class RmiRegistryList {
    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 1099;
        if (args.length == 2) {
            host = args[0];
            port = Integer.valueOf(args[1]);
        }
        String prefix = "//" + host + ":" + port + "/";
        try {
            String[] names = Naming.list(prefix);
            for (String name : names)
                System.out.println("rmi" + name);
        } catch (RemoteException e) {
            System.err.println("There is no objects on \n\t" + prefix +"\n or RMI is not ready!");
        }

    }
}
