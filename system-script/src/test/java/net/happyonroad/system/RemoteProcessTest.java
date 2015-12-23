/**
 * Developer: Kadvin Date: 14-9-22 下午3:58
 */
package net.happyonroad.system;

import net.happyonroad.model.RemoteInvocation;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * <h1>测试对远程命令的调用</h1>
 */
//@Ignore("the ci or other developer's host isn't trusted by srv2")
public class RemoteProcessTest extends AbstractProcessTest {

    @Test
    public void testInvokeRemoteCommand() throws Exception {
        RemoteInvocation invocation = new RemoteInvocation(remoteHost, remoteDir) {
            @Override
            public int perform(Process process) throws Exception {
                return process.run("./test.sh", "hi", "itsnow");
            }
        };
        invocation.setId("remote-invocation");
        RemoteProcess process = new RemoteProcess(invocation, executorService);
        int exitCode = invocation.perform(process);
        Assert.assertEquals(0, exitCode);
        Assert.assertEquals("hi itsnow", invocation.getOutput());
    }
}
