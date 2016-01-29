/**
 * Developer: Kadvin Date: 14-9-22 下午5:37
 */
package net.happyonroad.system;

import net.happyonroad.listener.InvocationEventBroadcaster;
import net.happyonroad.listener.ListenerNotifier;
import net.happyonroad.util.NamedThreadFactory;
import net.happyonroad.util.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The abstract process test
 */
public abstract class AbstractProcessTest {
    protected ExecutorService executorService;
    protected static String                     remoteHost  = "app2.dev.itsnow.com";
    protected static String                     remoteDir   = "/opt/system/test";
    protected        InvocationEventBroadcaster broadcaster = new InvocationEventBroadcaster() {
        @Override
        public void broadcast(ListenerNotifier notifier) {

        }
    };

    @Before
    public void setUp() throws Exception {
        String tmpDir = System.getProperty("java.io.tmpdir");
        System.setProperty("APP_HOME", tmpDir);
        File scriptDir = new File(tmpDir, "script/msc");
        File logDir = new File(tmpDir, "tmp");
        FileUtils.forceMkdir(scriptDir);
        FileUtils.forceMkdir(logDir);
        InputStream klassResource = getClass().getClassLoader().getResourceAsStream("script/test.sh");
        File testShellFile = new File(scriptDir, "test.sh");
        OutputStream tmpResource = new FileOutputStream(testShellFile);
        try {
            IOUtils.copy(klassResource, tmpResource);
            //需要保证该文件有被执行的权限 (chmod +x script/test.sh)
            Runtime.getRuntime().exec("chmod +x " + testShellFile.getAbsolutePath());
        } finally {
            IOUtils.closeQuietly(klassResource);
            IOUtils.closeQuietly(tmpResource);
        }

        String[] sshCommands = new String[]{"ssh", "root@" + remoteHost, "mkdir -p "+ remoteDir + ""};
        String[] scpCommands = new String[]{"scp", testShellFile.getAbsolutePath(), "root@"+ remoteHost + ":" + remoteDir};
        java.lang.Process mkdir = Runtime.getRuntime().exec(sshCommands);
        if( mkdir.waitFor() != 0 )
            throw new Exception("Can't execute: " + StringUtils.join(sshCommands, " ") + ", exit code: " + mkdir.exitValue());
        java.lang.Process scp = Runtime.getRuntime().exec(scpCommands);
        if( scp.waitFor() != 0 )
            throw new Exception("Can't execute: " + StringUtils.join(scpCommands, " ") + ", exit code: " + mkdir.exitValue());

        executorService = Executors.newFixedThreadPool(2, new NamedThreadFactory("ProcessInvokeExecutor")) ;
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(new File(System.getProperty("APP_HOME"), "tmp/"));
    }
}
