/**
 * Developer: Kadvin Date: 14-9-20 上午11:25
 */
package net.happyonroad.system;

import net.happyonroad.model.RemoteInvocation;
import net.happyonroad.util.StringUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * <h1>Remote SSH Shell</h1>
 */
public class RemoteProcess extends AbstractProcess<RemoteInvocation> {

    public RemoteProcess(RemoteInvocation invocation, ExecutorService systemInvokeExecutor) {
        super(invocation, systemInvokeExecutor);
    }

    @Override
    protected String[] assembleCommand(String command, Object[] args) {
        List<String> remoteCommands = getCommands(command, args);
        String remoteCommand = StringUtils.join(remoteCommands, " ");
        remoteCommand = "cd " + this.invocation.getWd() + " && " + remoteCommand;
        if (StringUtils.isNotBlank(invocation.getUser())) {
            //sshpass -p $password ssh -o PubkeyAuthentication=no -o StrictHostKeyChecking=no user@host "the command to be executed"
            return new String[]{"sshpass", "-p", invocation.getPassword(),
                                "ssh", "-o", "PubkeyAuthentication=no", "-o", "StrictHostKeyChecking=no",
                                invocation.getUser() + "@" + this.invocation.getHost(), remoteCommand};
        } else {
            //系统之间需要建立好公钥认证方式，且以默认的root身份执行
            //ssh root@host -o PasswordAuthentication=no -o StrictHostKeyChecking=no "the command to be executed"
            return new String[]{"ssh", "-o", "PasswordAuthentication=no", "-o", "StrictHostKeyChecking=no",
                                "root@" + this.invocation.getHost(), remoteCommand};
        }
    }

}
