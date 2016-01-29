/**
 * Developer: Kadvin Date: 14-9-15 下午4:55
 */
package net.happyonroad.model;

import net.happyonroad.system.Process;
import net.happyonroad.type.TimeInterval;
import net.happyonroad.util.StringUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;


/**
 * <h1>进行系统调用的任务</h1>
 */
public abstract class SystemInvocation {
    public static final String DEFAULT_WD = "/opt/";
    protected           String           id;      // 整体调用的标识符
    protected           int              seq;     // 调用序号
    private             long             timeout; // 超时(单位ms)
    protected           SystemInvocation next;    // 下一个任务
    protected           String           wd;      // 当前任务的working dir
    protected transient Process          process; // 关联shell process
    private             int              userFlag;// 用户设置的标记
    private             int           progress;   // 用户设置的本项工作的工作量，百分比

    public SystemInvocation(String wd) {
        this.wd = wd;
        this.seq = 0;
        this.progress = 10;
        timeout(1000 * 60 * 5);
    }

    public String getWd() {
        return wd;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        if (this.next != null) this.next.setId(id);
    }

    public long getTimeout() {
        return timeout;
    }

    public SystemInvocation timeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    public SystemInvocation timeout(String timeout) {
        this.timeout = new TimeInterval(timeout).getMilliseconds();
        return this;
    }

    public long totalTimeout() {
        if (this.next == null) return getTimeout();
        return getTimeout() + this.next.totalTimeout();
    }

    public abstract int perform(Process process) throws Exception;

    //
    // 把该invocation放到chain的末尾
    // 作用上类似于append
    public SystemInvocation next(SystemInvocation invocation) {
        if (this.next == null) {
            this.next = invocation;
            this.next.id = this.id;
            this.next.seq = this.seq + 1;
        } else {
            this.next.next(invocation);
        }
        return this;
    }

    public SystemInvocation getNext() {
        return next;
    }

    String totalFileName() {
        return getId() + ".log";
    }

    public void bind(Process process) {
        this.process = process;
    }

    public Process getProcess() {
        return this.process;
    }

    protected String getCommand() {
        if (process == null) return "<no execution>";
        return StringUtils.join(process.getCommand(), " ");
    }

    public int getSequence() {
        return seq;
    }

    public File totalFile() {
        return new File(System.getProperty("app.home", System.getProperty("user.dir")), "tmp/" + this.totalFileName());
    }

    public List<String> getOutputs() {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(totalFile());
            return IOUtils.readLines(fis);
        } catch (Exception e) {
            return null;
        } finally {
            IOUtils.closeQuietly(fis);
        }
    }

    public String getOutput() {
        return StringUtils.join(getOutputs(), "\n");
    }

    public int getUserFlag() {
        return userFlag;
    }

    public void setUserFlag(int userFlag) {
        this.userFlag = userFlag;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public SystemInvocation progress(int progress){
        setProgress(progress);
        return this;
    }
}
