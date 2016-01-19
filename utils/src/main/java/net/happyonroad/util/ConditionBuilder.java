package net.happyonroad.util;

/**
 * <h1>Map Builder</h1>
 *
 * @author Jay Xiong
 */
public final class ConditionBuilder {
    /**
     * <h2>将输入的参数序列，转换为一个map</h2>
     *
     * 如果pairs的value为null，则忽略
     *
     * @param pairs 参数序列
     * @return 转换之后的map
     */
    public static String buildCondition(String abbr, Object... pairs) {
        if (pairs.length % 2 != 0)
            throw new IllegalArgumentException("The pairs number should be even, but I got "
                                               + StringUtils.join(pairs, ","));
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < pairs.length; i += 2) {
            String key = pairs[i].toString().replaceAll("'","''");
            key = "`" + key + "`";
            if( !StringUtils.isEmpty(abbr)){
                key = abbr + "." + key;
            }
            Object value = pairs[i+1];
            if( value == null ){
                builder.append("(").append(key).append("IS NULL)") ;
            }else{
                //Escape SQL by replaceAll
                String strValue = value.toString().replaceAll("'", "''");
                builder.append("(").append(key).append(" = '").append(strValue).append("'").append(")") ;
            }
            if(i+1 < pairs.length - 1){
                builder.append(" AND ");
            }
        }
        return builder.toString();
    }

    public static Predicate buildPredicate(final Object...pairs){
        if (pairs.length % 2 != 0)
            throw new IllegalArgumentException("The pairs number should be even, but I got "
                                               + StringUtils.join(pairs, ","));
        return new Predicate() {
            @Override
            public boolean evaluate(Object challenge) {
                for (int i = 0; i < pairs.length; i += 2) {
                    String property = pairs[i].toString();
                    property = StringUtils.camelCase(property);
                    property = Character.toLowerCase(property.charAt(0)) + property.substring(1, property.length());
                    Object value = pairs[i+1];
                    Object actual ;
                    try {
                        actual = MiscUtils.getProperty(challenge, property);
                    } catch (Exception e) {
                        return false;
                    }
                    if( value == null ){
                        if (actual != null )  return false;
                    }else{
                        if( !actual.equals(value) ) return false;
                    }
                }
                return true;
            }
        };
    }
}
