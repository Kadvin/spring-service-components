/**
 * Developer: Kadvin Date: 14-3-3 下午1:33
 */
package dnt.type;

/**
 * <h1>通知的紧急程度</h1>
 *
 * 与Event的Severity有类似含义
 * 但是event的severity用于表达实际发生事情的严重程度；
 * 而这个往往用于控制向用户发送通知的急迫程度
 */
public enum Urgency {
    Normal,
    Urgent,
    VeryUrgent,
}
