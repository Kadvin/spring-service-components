/**
 * Developer: Kadvin Date: 14-9-20 上午11:19
 */
package net.happyonroad.model;


import java.io.File;

/**
 * <h1>Invoke some executable/script in localhost</h1>
 */
public abstract class LocalInvocation extends SystemInvocation{

    public LocalInvocation() {
        this(new File(System.getProperty("APP_HOME"), "script").getAbsolutePath());
    }

    public LocalInvocation(String wd) {
        super(wd);
    }


    @Override
    public String toString() {
        return String.format("%s@%d\n%s %s\n\n", id, seq, wd, getCommand());
    }
}
