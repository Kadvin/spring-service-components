/**
 * @author XiongJie, Date: 13-12-16
 */
package net.happyonroad.util;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils extends org.apache.commons.lang.StringUtils {

    private static final Pattern FILE_SPACE_PATTERN = Pattern.compile("(\\d+)\\s*([a-zA-Z])");

    private static final Pattern UNDERSCORE_PATTERN_1 = Pattern.compile("([A-Z]+)([A-Z][a-z])");

    private static final Pattern UNDERSCORE_PATTERN_2 = Pattern.compile("([a-z\\d])([A-Z])");

    private static List<RuleAndReplacement> plurals = new ArrayList<RuleAndReplacement>();

    private static List<RuleAndReplacement> singulars = new ArrayList<RuleAndReplacement>();

    private static List<String> uncountables = new ArrayList<String>();

    public static final Pattern ERROR_CODE_PATTERN = Pattern.compile("(\\d{6})\\:");
    public static final Pattern EVENT_CODE_PATTERN = Pattern.compile("(\\d{5})\\:");
    public static final Pattern ARGUMENT_EXTRACTOR = Pattern.compile("`([^`]*)`");
    public static final Pattern INTERPOLATE_PTN    = Pattern.compile("\\$\\{([^}]+)\\}");

    static {
        plural("$", "s");
        plural("s$", "s");
        plural("(ax|test)is$", "$1es");
        plural("(octop|vir)us$", "$1i");
        plural("(alias|status)$", "$1es");
        plural("(bu)s$", "$1es");
        plural("(buffal|tomat)o$", "$1oes");
        plural("([ti])um$", "$1a");
        plural("sis$", "ses");
        plural("(?:([^f])fe|([lr])f)$", "$1$2ves");
        plural("(hive)$", "$1s");
        plural("([^aeiouy]|qu)y$", "$1ies");
        plural("([^aeiouy]|qu)ies$", "$1y");
        plural("(x|ch|ss|sh)$", "$1es");
        plural("(matr|vert|ind)ix|ex$", "$1ices");
        plural("([m|l])ouse$", "$1ice");
        plural("(ox)$", "$1en");
        plural("(quiz)$", "$1zes");
        singular("s$", "");
        singular("(n)ews$", "$1ews");
        singular("([ti])a$", "$1um");
        singular("((a)naly|(b)a|(d)iagno|(p)arenthe|(p)rogno|(s)ynop|(t)he)ses$", "$1$2sis");
        singular("(^analy)ses$", "$1sis");
        singular("([^f])ves$", "$1fe");
        singular("(hive)s$", "$1");
        singular("(tive)s$", "$1");
        singular("([lr])ves$", "$1f");
        singular("([^aeiouy]|qu)ies$", "$1y");
        singular("(s)eries$", "$1eries");
        singular("(m)ovies$", "$1ovie");
        singular("(x|ch|ss|sh)es$", "$1");
        singular("([m|l])ice$", "$1ouse");
        singular("(bus)es$", "$1");
        singular("(o)es$", "$1");
        singular("(shoe)s$", "$1");
        singular("(cris|ax|test)es$", "$1is");
        singular("([octop|vir])i$", "$1us");
        singular("(alias|status)es$", "$1");
        singular("^(ox)en", "$1");
        singular("(vert|ind)ices$", "$1ex");
        singular("(matr)ices$", "$1ix");
        singular("(quiz)zes$", "$1");
        irregular("person", "people");
        irregular("man", "men");
        irregular("child", "children");
        irregular("sex", "sexes");
        irregular("move", "moves");
        uncountable("equipment", "information", "rice", "money", "species", "series", "fish", "sheep");
    }

    /**
     * <h2>将字符串中的 ${key} 里面的内容替换为map里面key的内容</h2>
     *
     * @param origin  原始字符串
     * @param context 上下文
     * @return 替换之后的字符串
     */
    public static String interpolate(String origin, Map<String, Object> context) {
        return interpolate(origin, INTERPOLATE_PTN, new VariableResolver.MapResolver(context));
    }

    /**
     * <h2>将字符串中的 ${key} 里面的内容替换为bean的对应属性</h2>
     *
     * @param origin 原始字符串
     * @param bean   对象
     * @return 替换之后的字符串
     */
    public static String interpolate(String origin, Object bean) {
        return interpolate(origin, INTERPOLATE_PTN, new VariableResolver.BeanResolver(bean));
    }

    /**
     * <h2>将字符串中的 ${key} 里面的内容替换为对应的参数</h2>
     *
     * @param origin 原始字符串
     * @param args   参数
     * @return 替换之后的字符串
     */
    public static String interpolate(String origin, Object... args) {
        return interpolate(origin, INTERPOLATE_PTN, new VariableResolver.ArrayResolver(args));
    }

    /**
     * <h2>将特定字符串中符合某些pattern的内容替换为另外的内容</h2>
     *
     * @param origin   原始字符串
     * @param pattern  抽取需要替换内容的正则表达式，其中应该有一个捕获型分组
     * @param resolver 可以抽取出来的key转换为实际需要替换的内容的对象
     * @return 替换之后的字符串
     */
    public static String interpolate(String origin, Pattern pattern, VariableResolver resolver) {
        Matcher m = pattern.matcher(origin);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String variable = m.group(1);
            Object replacement = resolver.resolve(variable);
            if (replacement == null) {
                throw new IllegalArgumentException("Can't find " + variable + " by " + resolver);
            }
            String value = replacement.toString();
            //解析出来的变量可能还需要再解析
            if (pattern.matcher(value).find()) {
                value = interpolate(value, resolver);
            }
            try {
                m.appendReplacement(sb, value);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();//just for catch it to debug
                throw e;
            }
        }
        m.appendTail(sb);
        return sb.toString().trim();
    }

    // check str only contains [a-zA-Z] or [.]
    public static boolean isCharDot(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            int ch = str.charAt(i);
            if ((ch < 65 || ch > 122 || (ch > 90 && ch < 97)) && (str.charAt(i) != '.')) {
                return false;
            }
        }
        return true;
    }

    // check str only contains [a-zA-Z] or [-_]or[0-9]
    public static boolean isCharDigitUnderscoreHyphen(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            int ch = str.charAt(i);
            if ((ch < 65 || ch > 122 || (ch > 90 && ch < 97))
                && (str.charAt(i) != '_')
                && (str.charAt(i) != '-')
                && !Character.isDigit(ch)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sample:
     * <p><b> china_company => ChinaCompany</b></p>
     *
     * @param name the string formed in underscore
     * @return camel string
     */
    public static String camelCase(final String name) {
        final StringBuilder builder = new StringBuilder();
        for (final String part : name.split("_")) {
            builder.append(Character.toTitleCase(part.charAt(0)))
                   .append(part.substring(1));
        }
        return builder.toString();
    }


    /**
     * Sample:
     * <p><b> china_company => China Company</b></p>
     *
     * @param name the string formed in underscore
     * @return camel string
     */
    public static String titlize(final String name) {
        final StringBuilder builder = new StringBuilder();
        for (final String part : name.split("_")) {
            builder.append(Character.toTitleCase(part.charAt(0)))
                   .append(" ")
                   .append(part.substring(1));
        }
        return builder.toString();

    }

    /**
     * Register two string are not regular, such as:
     * <p/>
     * person -> people
     *
     * @param singular singular form string
     * @param plural   plural form string
     */
    public static void irregular(final String singular, final String plural) {
        plural(singular + "$", plural);
        singular(plural + "$", singular);
    }

    /**
     * Register word plural rule
     *
     * @param rule        the rule(regexp string value)
     * @param replacement the replacement for previous regexp captured group
     */
    public static void plural(final String rule, final String replacement) {
        plurals.add(0, new RuleAndReplacement(rule, replacement));
    }

    /**
     * Register word singular rule
     *
     * @param rule        the rule(regexp string value)
     * @param replacement the replacement for previous regexp captured group
     */
    public static void singular(final String rule, final String replacement) {
        singulars.add(0, new RuleAndReplacement(rule, replacement));
    }

    /**
     * Register uncountable words
     *
     * @param words the uncountable words
     */
    public static void uncountable(final String... words) {
        uncountables.addAll(Arrays.asList(words));
    }

    /**
     * Return word's plural form value
     *
     * @param word the word
     * @return the plural word
     */
    public static String pluralize(final String word) {
        if (uncountables.contains(word.toLowerCase())) {
            return word;
        }
        return replaceWithFirstRule(word, plurals);
    }

    /**
     * Return the word's singular form value
     *
     * @param word the word
     * @return the singular word
     */
    public static String singularize(final String word) {
        if (uncountables.contains(word.toLowerCase())) {
            return word;
        }
        return replaceWithFirstRule(word, singulars);
    }

    /**
     * Underscore a word, such as:
     * <p/>
     * <p><b>ChinaCompany-> china_company</b></p>
     *
     * @param camelCasedWord the camel case word
     * @return the underscored word
     */
    public static String underscore(final String camelCasedWord) {
        String underscoredWord = UNDERSCORE_PATTERN_1.matcher(camelCasedWord)
                                                     .replaceAll("$1_$2");
        underscoredWord = UNDERSCORE_PATTERN_2.matcher(underscoredWord)
                                              .replaceAll("$1_$2");
        underscoredWord = underscoredWord.replace('-', '_')
                                         .toLowerCase();
        return underscoredWord;
    }

    /**
     * Return a tableized word for class name, such as:
     * <p><b>ChinaCompany-> china_companies</b></p>
     *
     * @param className the class name
     * @return the underscored and plural words
     */
    public static String tableize(final String className) {
        return pluralize(underscore(className));
    }

    /**
     * Return a tableized word for class name, such as:
     * <p><b>ChinaCompany.class -> china_companies</b></p>
     *
     * @param klass the class
     * @return the underscored and plural words
     */
    public static String tableize(final Class<?> klass) {
        return tableize(klass.getSimpleName());
    }

    /**
     * Encode the string as an URL.
     *
     * @param s the string to encode
     * @return the encoded string
     */
    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (Exception e) {
            // UnsupportedEncodingException
            return "";
        }
    }

    public static String escapeJMXObjectName(String name) {
        return name.replaceAll(":", "-")
                   .replaceAll("\"", "-")
                   .replaceAll("=", "-")
                   .replaceAll("\\*", "-")
                   .replaceAll("\\?", "-");
    }


    public static String[] split3(String message, String s) {
        int p1 = message.indexOf(s);
        if (-1 == p1) {
            return new String[]{message};
        }
        int p2 = message.indexOf(s, p1 + s.length());
        if (-1 == p2) {
            return new String[]{message.substring(0, p1), message.substring(p1 + s.length())};
        }
//        String[] result = new String[3];
//        result[0] = message.substring(0, p1);
//        result[1] = message.substring(p1 + s.length(), p2);
//        result[2] = message.substring(p2 + s.length());

//        return result;
        return new String[]{message.substring(0, p1)
                , message.substring(p1 + s.length(), p2)
                , message.substring(p2 + s.length())};
    }

    /**
     * Decode the URL to a string.
     *
     * @param encoded the encoded URL
     * @return the decoded string
     */
    public static String urlDecode(String encoded) {
        try {
            return URLDecoder.decode(encoded, "UTF-8");
        } catch (Exception e) {
            // UnsupportedEncodingException
            return "";
        }
    }


    /**
     * Convert a byte count as human readable
     *
     * @param bytes the bytes value
     * @return string can be read
     */
    public static String humanReadableByteCount(long bytes) {
        return humanReadableByteCount(bytes, false);
    }

    /**
     * Convert a byte count as human readable
     *
     * @param bytes the bytes value
     * @param si    true: unit as 1000, false, unit as 1024
     * @return string can be read
     */
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        //约定: 如果是一1000为单位的，则用小写字母做单位，否则以大写字母做单位
        char pre = (si ? "kmgtpe" : "KMGTPE").charAt(exp - 1);// + (si ? "" : "");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static long parseBytes(String s) {
        Matcher matcher = FILE_SPACE_PATTERN.matcher(s);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format("Wrong bytes format: %s", s));
        }
        int number = Integer.valueOf(matcher.group(1));
        String unit = matcher.group(2);

        return number * byteUnitValue(unit);
    }


    private static long byteUnitValue(String unit) {
        if ("k".equalsIgnoreCase(unit) || "kb".equalsIgnoreCase(unit)) {
            return 1024;
        } else if ("m".equalsIgnoreCase(unit) || "mb".equalsIgnoreCase(unit)) {
            return 1024 * 1024;
        } else if ("g".equalsIgnoreCase(unit) || "gb".equalsIgnoreCase(unit)) {
            return 1024 * 1024 * 1024;
        } else if ("t".equalsIgnoreCase(unit) || "tb".equalsIgnoreCase(unit)) {
            return 1024L * 1024 * 1024 * 1024;
        } else if ("p".equalsIgnoreCase(unit) || "pb".equalsIgnoreCase(unit)) {
            return 1024L * 1024 * 1024 * 1024 * 1024;
        } else if ("e".equalsIgnoreCase(unit) || "eb".equalsIgnoreCase(unit)) {
            return 1024L * 1024 * 1024 * 1024 * 1024 * 1024;
        } else {
            throw new IllegalArgumentException(String.format("Unrecognized unit: %s", unit));
        }
    }

    private static String replaceWithFirstRule(final String word, final List<RuleAndReplacement> ruleAndReplacements) {
        for (final RuleAndReplacement rar : ruleAndReplacements) {
            final String rule = rar.getRule();
            final String replacement = rar.getReplacement();
            final Matcher matcher = Pattern.compile(rule, Pattern.CASE_INSENSITIVE)
                                           .matcher(word);
            if (matcher.find()) {
                return matcher.replaceAll(replacement);
            }
        }
        return word;
    }


    public static void toString(StringBuilder sb, Map<?, ?> errors) {
        if (null == errors) {
            return;
        }
        if (errors.isEmpty()) {
            sb.append("{ }");
            return;
        }
        sb.append("{");
        for (Map.Entry<?, ?> entry : errors.entrySet()) {

            sb.append(entry.getKey());
            sb.append("=");
            if (null == entry.getValue()) {
                sb.append("<null>");
            } else {
                sb.append(entry.getValue());
            }
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" }");
    }

    /**
     * Resolve arguments from exception message
     *
     * @param message the exception message, I will think of string in `` as argument
     * @return the arguments array
     */
    public static String[] resolveArguments(String message) {
        Matcher matcher = ARGUMENT_EXTRACTOR.matcher(message);
        List<String> args = new LinkedList<String>();
        while (matcher.find()) {
            args.add(matcher.group(1));
        }
        return args.toArray(new String[args.size()]);
    }

    public static String translate(MessageSource msg, String code, Object... args) {
        try {
            Locale locale = Locale.getDefault();
            String message = msg.getMessage(code, args, locale);
            if (locale.equals(Locale.CHINA)) {
                try {
                    return new String(message.getBytes("iso8859-1"), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    return message;
                }
            } else {
                return message;
            }
        } catch (NoSuchMessageException e) {
            if (args.length == 1 && args[0] instanceof String)
                return (String) args[0]; //default
            else throw e;
        }
    }


    /**
     * Resolve a 6-length code from the message
     *
     * @param message the message
     * @return null if not found, otherwise the error code number
     */
    public static String resolveCode(String message) {
        Matcher matcher = ERROR_CODE_PATTERN.matcher(message);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    public static String resolveEventCode(String message) {
        Matcher matcher = EVENT_CODE_PATTERN.matcher(message);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    private static final class RuleAndReplacement {

        private final String rule;

        private final String replacement;

        public RuleAndReplacement(final String rule, final String replacement) {
            this.rule = rule;
            this.replacement = replacement;
        }

        public String getReplacement() {
            return replacement;
        }

        public String getRule() {
            return rule;
        }

    }

}
