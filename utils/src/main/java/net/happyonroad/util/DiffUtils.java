/**
 * Developer: Kadvin Date: 15/1/26 下午1:23
 */
package net.happyonroad.util;


import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.util.*;

/**
 * 比较两个对象的Difference
 */
@SuppressWarnings("unchecked")
public class DiffUtils {
    public static <T> String describeDiff(T one, T another, String... ignores){
        return describe(difference(one, another, ignores), ignores);
    }

    public static <T> MapDifference<String, Object > difference(T one, T another, String...ignores){
        Arrays.sort(ignores);
        try{
            Map<String, PropertyDescriptor> descriptors = BeanUtils.describe(one);
            Map<String, Object> oneProperties = new HashMap<String, Object>();
            Map<String, Object> anotherProperties = new HashMap<String, Object>();
            for (Map.Entry<String, PropertyDescriptor> entry : descriptors.entrySet()) {
                if(Arrays.binarySearch(ignores, entry.getKey()) >= 0 ) continue;
                Object oneProperty = PropertyUtils.getProperty(one, entry.getKey());
                if( oneProperty != null && oneProperty.getClass().isArray()){
                    oneProperties.put(entry.getKey(), Arrays.asList((Object[])oneProperty));
                }else{
                    oneProperties.put(entry.getKey(), oneProperty);
                }
                Object anotherProperty = PropertyUtils.getProperty(another, entry.getKey());
                if( anotherProperty != null && anotherProperty.getClass().isArray()){
                    anotherProperties.put(entry.getKey(), Arrays.asList((Object[])anotherProperty));
                }else{
                    anotherProperties.put(entry.getKey(), anotherProperty);
                }
            }
            return Maps.difference(oneProperties, anotherProperties);
        }catch (Exception ex){
            throw new RuntimeException("Can't diff "+ one + " with " + another, ex );
        }
    }

    public static String describe(MapDifference differences, String... ignores){
        if( differences.areEqual() ){
            return "nothing";
        }
        // to be searched
        Arrays.sort(ignores);
        Map<String, MapDifference.ValueDifference> entries = differences.entriesDiffering();
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<String, MapDifference.ValueDifference>> it = entries.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, MapDifference.ValueDifference> entry = it.next();
            if(Arrays.binarySearch(ignores, entry.getKey()) >= 0 ) continue;
            Object leftValue = entry.getValue().leftValue();
            Object rightValue = entry.getValue().rightValue();
            if(isNullAndEmpty(leftValue, rightValue)) continue;
            sb.append(entry.getKey()).append("(").
                    append(leftValue).append(" -> ").
                    append(rightValue).append(")");
            if( it.hasNext() )sb.append(", ");
        }
        return sb.toString();
    }

    static boolean isNullAndEmpty(Object leftValue, Object rightValue) {
        if( leftValue == null ){
            if( rightValue instanceof Collection){
                return ((Collection) rightValue).isEmpty();
            }
            if( rightValue instanceof String ){
                return StringUtils.isBlank((String) rightValue);
            }
        }
        if( rightValue == null ){
            if( leftValue instanceof Collection ){
                return ((Collection) leftValue).isEmpty();
            }
            if( leftValue instanceof String ){
                return StringUtils.isBlank((String) leftValue);
            }
        }
        return false;
    }
}
