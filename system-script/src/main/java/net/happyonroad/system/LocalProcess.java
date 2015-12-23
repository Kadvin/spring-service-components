/**
 * Developer: Kadvin Date: 14-9-20 上午11:24
 */
package net.happyonroad.system;

import net.happyonroad.model.LocalInvocation;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * <h1>Local Shell</h1>
 */
public class LocalProcess extends AbstractProcess<LocalInvocation> {


    public LocalProcess(LocalInvocation invocation, ExecutorService systemInvokeExecutor) {
        super(invocation, systemInvokeExecutor);
    }

    @Override
    protected String[] assembleCommand(String command, Object[] args) {
        builder.directory(new File(invocation.getWd()));
        List<String> commands = getCommands(command, args);
        return commands.toArray(new String[commands.size()]);
    }
}
