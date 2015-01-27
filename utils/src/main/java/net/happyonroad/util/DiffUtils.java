/**
 * Developer: Kadvin Date: 15/1/26 下午1:23
 */
package net.happyonroad.util;


import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 比较两个对象的Difference
 */
@SuppressWarnings("unchecked")
public class DiffUtils {
    public static <T> String describeDiff(T one, T another){
        return describe(difference(one, another));
    }

    public static <T> MapDifference<String, Object > difference(T one, T another){
        try{
            Map<String, PropertyDescriptor> descriptors = BeanUtils.describe(one);
            Map<String, Object> oneProperties = new HashMap<String, Object>();
            Map<String, Object> anotherProperties = new HashMap<String, Object>();
            for (Map.Entry<String, PropertyDescriptor> entry : descriptors.entrySet()) {
                if( entry.getKey().equals("callbacks")) continue;
                Object oneProperty = PropertyUtils.getProperty(one, entry.getKey());
                oneProperties.put(entry.getKey(), oneProperty);
                Object anotherProperty = PropertyUtils.getProperty(another, entry.getKey());
                anotherProperties.put(entry.getKey(), anotherProperty);
            }
            return Maps.difference(oneProperties, anotherProperties);
        }catch (Exception ex){
            throw new RuntimeException("Can't diff "+ one + " with " + another, ex );
        }
    }

    public static String describe(MapDifference differences){
        if( differences.areEqual() ){
            return "nothing";
        }
        Map<String, MapDifference.ValueDifference> entries = differences.entriesDiffering();
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<String, MapDifference.ValueDifference>> it = entries.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, MapDifference.ValueDifference> entry = it.next();
            if( entry.getValue().leftValue() != null ){
                if( entry.getValue().leftValue().getClass().getName().contains("net.sf.cglib.proxy"))
                    continue;
            }
            if( entry.getValue().rightValue() != null ){
                if( entry.getValue().rightValue().getClass().getName().contains("net.sf.cglib.proxy"))
                    continue;
            }
            sb.append(entry.getKey()).append(": ").
                    append(entry.getValue().leftValue()).append(" -> ").
                    append(entry.getValue().rightValue());
            if( it.hasNext() )sb.append(", ");
        }
        return sb.toString();
    }
}
