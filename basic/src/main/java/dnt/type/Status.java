/**
 * Developer: Kadvin Date: 14-5-14 下午1:27
 */
package dnt.type;

/**
 * 资源状态，代表实际的状态
 */
public enum Status {
    Up, Down, Error
    // Up: 正常监控中
    // Down: 无法联系，无法监控
    // Error：虽然正常，但出现错误，如Refused等
}
