/**
 * Developer: Kadvin Date: 14/11/5 上午9:59
 */
package net.happyonroad.util;

/**
 * <h1>The number rule</h1>
 * 格式: FORMAT[@START][/INCREMENT], 其中:
 * <ol>
 * <li>format里面采用java string format语法, %d, %0d, %.2d
 * <li>括号中为可省略部分，以下均为合法的原始格式：
 * <ul>
 *   <li>TEST_%d           普通的数字连接, 如 TEST_1
 *   <li>TEST_%05d         5位数字连接,   如 TEST_0001
 *   <li>TEST_%06d@100     从100开始的5位，如 TEST_00100
 *   <li>TEST_%06d@100/2   从一百开始，每次增加的步长为二
 *   <li>TEST_%06d/2       从一开始，每次增加的步长为二
 * </ul>
 * </ol>
 * 转换之后，可以为db中的sequence
*/
public class NumberRule {

    final String format;
    final long start;
    final int increment;

    public NumberRule(String format, long start, int increment) {
        this.format = format;
        this.start = start;
        this.increment = increment;
    }

    public String getFormat() {
        return format;
    }

    public long getStart() {
        return start;
    }

    public int getIncrement() {
        return increment;
    }

    public static NumberRule parse(String rule) {
        String format = rule;
        String start = "1";
        String increment = "1";
        if( format.contains("/") ){
            String[] formatAndIncrement = format.split("/");
            format = formatAndIncrement[0];
            increment = formatAndIncrement[1];
        }
        if (format.contains("@")){
            String[] formatAndStart = format.split("@");
            format = formatAndStart[0];
            start = formatAndStart[1];
        }
        return new NumberRule(format, Long.valueOf(start), Integer.valueOf(increment));
    }
}
