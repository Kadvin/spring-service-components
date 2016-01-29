package net.happyonroad.system;

import net.happyonroad.listener.InvocationEventBroadcaster;
import net.happyonroad.listener.ListenerNotifier;
import net.happyonroad.listener.SystemInvocationListener;
import net.happyonroad.model.SystemInvocation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * <h1>Class Title</h1>
 *
 * @author Jay Xiong
 */
class Redirector implements Runnable {
    private final InputStream                src;
    private final File                       file;
    private final boolean                    append;
    private final InvocationEventBroadcaster broadcaster;
    private final SystemInvocation           invocation;
    private       StringBuilder              piped;

    public Redirector(InputStream src, File file, boolean append, SystemInvocation invocation,
                      InvocationEventBroadcaster broadcaster) {
        this.src = src;
        this.file = file;
        this.append = append;
        this.invocation = invocation;
        this.broadcaster = broadcaster;
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
                final String message = new String(buffer, 0, n);
                piped.append(message);
                broadcaster.broadcast(new ListenerNotifier() {
                    @Override
                    public void notify(SystemInvocationListener listener) {
                        if( listener.care(invocation)) listener.onMessage(invocation, message);
                    }
                });
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
