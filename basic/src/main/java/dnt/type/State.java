/**
 * Developer: Kadvin Date: 14-3-31 下午7:17
 */
package dnt.type;

/**
 * 监控状态，代表管理者的意志，而不是实际的情况
 */
public enum State {
    Unknown,
    Running,//PRTG: started
    Stopped //PRTG: paused
}
