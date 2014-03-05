/**
 * Developer: Kadvin Date: 14-1-22 下午5:18
 */
package dnt.util;

import java.util.regex.Pattern;

/**
 * The Pattern Utils
 */
public class PatternUtils {
    public static Pattern compile(String raw){
        return compile(raw, Pattern.CASE_INSENSITIVE);
    }

    public static Pattern compile(String raw, int flags){
        if( raw == null ) return null;
        String compiled = compileString(raw);
        return Pattern.compile(compiled, flags);
    }

    /**
     * 模仿Ant语法，将面向程序员提供的 一般通配符 ** / * / ?编译成为Java的正则表达式
     * TODO 这个还比较简陋，许多边界情况没有处理
     *
     * @param resourcePath 资源表征路径
     * @return  Java的正则表达式
     */
    public static Pattern compileResource(String resourcePath) {
        String path = resourcePath.replace("**", "\\w{star}");
        path = path.replace("*", "[^\\/]*");
        path = path.replace("?", "[^\\/]");
        path = path.replace("{star}", "*");
        return Pattern.compile(path);
    }


    private static String compileString(String line)
    {
        line = line.trim();
        int strLen = line.length();
        StringBuilder sb = new StringBuilder(strLen);
        // Remove beginning and ending * globs because they're useless
        if (line.startsWith("*"))
        {
            line = line.substring(1);
            strLen--;
        }
        if (line.endsWith("*"))
        {
            line = line.substring(0, strLen-1);
        }
        boolean escaping = false;
        int inCurlies = 0;
        for (char currentChar : line.toCharArray())
        {
            switch (currentChar)
            {
                case '*':
                    if (escaping)
                        sb.append("\\*");
                    else
                        sb.append(".*");
                    escaping = false;
                    break;
                case '?':
                    if (escaping)
                        sb.append("\\?");
                    else
                        sb.append('.');
                    escaping = false;
                    break;
                case '.':
                case '(':
                case ')':
                case '+':
                case '|':
                case '^':
                case '$':
                case '@':
                case '%':
                    sb.append('\\');
                    sb.append(currentChar);
                    escaping = false;
                    break;
                case '\\':
                    if (escaping)
                    {
                        sb.append("\\\\");
                        escaping = false;
                    }
                    else
                        escaping = true;
                    break;
                case '{':
                    if (escaping)
                    {
                        sb.append("\\{");
                    }
                    else
                    {
                        sb.append('(');
                        inCurlies++;
                    }
                    escaping = false;
                    break;
                case '}':
                    if (inCurlies > 0 && !escaping)
                    {
                        sb.append(')');
                        inCurlies--;
                    }
                    else if (escaping)
                        sb.append("\\}");
                    else
                        sb.append("}");
                    escaping = false;
                    break;
                case ',':
                    if (inCurlies > 0 && !escaping)
                    {
                        sb.append('|');
                    }
                    else if (escaping)
                        sb.append("\\,");
                    else
                        sb.append(",");
                    break;
                default:
                    escaping = false;
                    sb.append(currentChar);
            }
        }
        return sb.toString();
    }


}
