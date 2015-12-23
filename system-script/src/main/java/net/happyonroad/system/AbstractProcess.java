/**
 * Developer: Kadvin Date: 14-9-21 下午4:40
 */
package net.happyonroad.system;

import net.happyonroad.exception.SystemInvokeException;
import net.happyonroad.model.SystemInvocation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * <h1>The abstract shell inherited by local/remote shell</h1>
 */
public abstract class AbstractProcess<T extends SystemInvocation> implements Process {
    protected final ProcessBuilder builder = new ProcessBuilder();
    protected final ExecutorService   systemInvokeExecutor;
    protected final T                 invocation;
    protected       java.lang.Process underlying;
    private         Redirector        redirector;

    public AbstractProcess(T invocation, ExecutorService systemInvokeExecutor) {
        this.invocation = invocation;
        this.systemInvokeExecutor = systemInvokeExecutor;
    }

    protected List<String> getCommands(String command, Object[] args) {
        List<String> commands = new LinkedList<String>();
        commands.add(command);
        for (Object arg : args) {
            commands.add(arg.toString());
        }
        return commands;
    }

    @Override
    public int run(String command, Object... args) throws SystemInvokeException {
        File logFile = invocation.totalFile();
        try {
            Future<?> stdoutFuture = background(command, args);
            int exitCode = underlying.waitFor();
            stdoutFuture.get();
            return exitCode;
        } catch (InterruptedException e) {
            recordError(e, logFile);
            throw new SystemInvokeException("Command execution is interrupted", e);
        } catch (ExecutionException e) {
            recordError(e, logFile);
            throw new SystemInvokeException("Command execution exception", e);
        }
    }

    @Override
    public Future background(String command, Object... args) throws SystemInvokeException {
        String[] realCommands = assembleCommand(command, args);
        builder.command(realCommands).redirectErrorStream(true);
        File logFile = invocation.totalFile();
        try {
            underlying = builder.start();
            return pipe(underlying.getInputStream(), logFile);
        } catch (IOException e) {
            recordError(e, logFile);
            throw new SystemInvokeException("Can't run the command: " + command, e);
        }
    }

    @Override
    public String getContent() {
        if (redirector == null)
            return null;
        return redirector.getPiped();
    }

    private void recordError(Exception e, File file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, true);
            recordError(e, fos, 0);
        } catch (Exception e1) {
            //ignore
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    private void recordError(Throwable e, OutputStream outputStream, int indent) throws IOException {
        for (int i = 0; i < indent; i++)
            IOUtils.write("\t", outputStream);
        IOUtils.write(e.getClass().getName().getBytes(), outputStream);
        IOUtils.write(":", outputStream);
        IOUtils.write(e.getLocalizedMessage().getBytes(Charset.forName("UTF-8")), outputStream);
        IOUtils.write("\n", outputStream);
        if (e.getCause() != null && e.getCause() != e) {
            recordError(e.getCause(), outputStream, indent + 1);
        }
    }


    protected abstract String[] assembleCommand(String command, Object[] args);

    public List<String> getCommand() {
        return builder.command();
    }

    private Future<?> pipe(final InputStream src, final File file) {
        redirector = new Redirector(src, file, invocation.getSequence() > 0);
        return systemInvokeExecutor.submit(redirector);
    }

}

class Redirector implements Runnable {
    private final InputStream   src;
    private final File          file;
    private final boolean       append;
    private       StringBuilder piped;

    public Redirector(InputStream src, File file, boolean append) {
        this.src = src;
        this.file = file;
        this.append = append;
        this.piped = new StringBuilder();
    }

    public void run() {
        FileOutputStream dest = null;
        try {
            if (!file.exists()) FileUtils.touch(file);
            dest = new FileOutputStream(file, append);// not use append mode
            byte[] buffer = new byte[1024];
            for (int n = 0; n != -1; n = src.read(buffer)) {
                dest.write(buffer, 0, n);
                dest.flush();
                piped.append(new String(buffer, 0, n));
            }
        } catch (IOException e) {
            // just exit
        } finally {
            IOUtils.closeQuietly(dest);
        }
    }

    public String getPiped() {
        return piped.toString();
    }
}
