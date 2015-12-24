/**
 * Developer: Kadvin Date: 14-9-20 上午11:24
 */
package net.happyonroad.system;

import net.happyonroad.listener.InvocationEventBroadcaster;
import net.happyonroad.model.LocalInvocation;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * <h1>Local Shell</h1>
 */
public class LocalProcess extends AbstractProcess<LocalInvocation> {


    public LocalProcess(LocalInvocation invocation, InvocationEventBroadcaster broadcaster, ExecutorService systemInvokeExecutor) {
        super(invocation, broadcaster, systemInvokeExecutor);
    }

    @Override
    protected String[] assembleCommand(String command, Object[] args) {
        builder.directory(new File(invocation.getWd()));
        List<String> commands = getCommands(command, args);
        return commands.toArray(new String[commands.size()]);
    }
}
