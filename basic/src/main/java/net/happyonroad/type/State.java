/**
 * Developer: Kadvin Date: 14-3-31 下午7:17
 */
package net.happyonroad.type;

/**
 * <h1>监控状态</h1>
 *
 * 这个状态与电信网管中 Admin State比较相似
 * 代表管理者的意志，而不是实际的情况
 */
public enum State {
    Running,//PRTG: started
    Stopped //PRTG: paused
}
