/**
 * xiongjie on 14-8-20.
 */
package net.happyonroad.platform.services;

import java.io.File;
import java.util.Comparator;

/**
* <h1>Class Usage</h1>
*/
class ServicePackageComparator implements Comparator<File> {
    @Override
    public int compare(File jar1, File jar2) {
        return order(jar1.getName()) - order(jar2.getName());
    }

    private int order(String name){
        int base = baseOrder(name);
        int sub = subOrder(name);
        return base + sub;
    }

    private int baseOrder(String name){
        if( name.contains("net.happyonroad") ) {
            return 5;
        }else if( name.contains("common-") ){
            return 10;
        } else if( name.contains("activiti")) {
            return 30;
        } else if( name.contains("general-")) {
            return 50;
        }else if (name.contains("mutable-") || name.contains("system-")){
            return 70;
        }else if (name.contains("msu-")){
            return 70;
        }else if (name.contains("msp-")){
            return 70;
        }else if( name.contains("demo-")) {
            return 90;
        }else {//unknown
            return 110;
        }
    }

    private int subOrder(String name){
        if( name.contains("cmdb")) return 1;
        if( name.contains("gui")) return 2;
        if( name.contains("model")) return 3;
        if( name.contains("deploy")) return 4;
        if( name.contains("system")) return 5;
        if( name.contains("activiti")) return 5;
        if( name.contains("user")) return 6;
        if( name.contains("account")) return 7;
        if( name.contains("session")) return 8;
        if( name.contains("contract")) return 9;
        if( name.contains("service_catalog")) return 10;
        if( name.contains("incident")) return 11;
        return 12;
    }

}
