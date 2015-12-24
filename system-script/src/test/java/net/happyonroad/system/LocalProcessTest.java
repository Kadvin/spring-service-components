/**
 * Developer: Kadvin Date: 14-9-22 下午3:58
 */
package net.happyonroad.system;

import net.happyonroad.model.LocalInvocation;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * <h1>测试对本地命令的调用</h1>
 */
//@Ignore("Windows need set SH in env.PATHEXT")
public class LocalProcessTest extends AbstractProcessTest {

    @Test
    public void testInvokeLocalCommand() throws Exception {
        LocalInvocation invocation = new LocalInvocation() {
            @Override
            public int perform(Process process) throws Exception {
                return process.run("./test.sh", "hello", "world");
            }
        };
        invocation.setId("local-invocation");
        LocalProcess process = new LocalProcess(invocation, broadcaster, executorService);
        int exitCode = invocation.perform(process);
        Assert.assertEquals(0, exitCode);
        Assert.assertEquals("hello world", invocation.getOutput());
    }
}
