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
    static String[] DEFAULT_IGNORES = new String[]{"class", "callbacks", "createdAt", "updatedAt", "new", "cascadeUpdating", "cascadeDeleting",
                 "cascadeCreating", "hierarchyDeleting"};

    /**
     * <h2>描述两个对象之间的差异</h2>
     *
     * @param one     第一个对象
     * @param another 第二个对象
     * @param ignores 忽略的属性
     * @param <T>     对象的类型
     * @return 差异描述
     */
    public static <T> String describeDiff(T one, T another, String... ignores) {
        return describe(difference(one, another, ignores), ignores);
    }

    /**
     * <h2>判断两个对象的差异</h2>
     *
     * @param one     第一个对象
     * @param another 第二个对象
     * @param ignores 忽略的属性
     * @param <T>     对象的类型
     * @return 差异信息
     */
    public static <T> MapDifference<String, Object> difference(T one, T another, String... ignores) {
        if( ignores.length == 0 ){
            ignores = DEFAULT_IGNORES;
        }
        Arrays.sort(ignores);
        try {
            Map<String, PropertyDescriptor> descriptors = BeanUtils.describe(one);
            Map<String, Object> oneProperties = new HashMap<String, Object>();
            Map<String, Object> anotherProperties = new HashMap<String, Object>();
            for (Map.Entry<String, PropertyDescriptor> entry : descriptors.entrySet()) {
                if (Arrays.binarySearch(ignores, entry.getKey()) >= 0) continue;
                Object oneProperty = PropertyUtils.getProperty(one, entry.getKey());
                if (oneProperty != null && oneProperty.getClass().isArray()) {
                    oneProperties.put(entry.getKey(), Arrays.asList((Object[]) oneProperty));
                } else {
                    oneProperties.put(entry.getKey(), oneProperty);
                }
                Object anotherProperty = PropertyUtils.getProperty(another, entry.getKey());
                if (anotherProperty != null && anotherProperty.getClass().isArray()) {
                    anotherProperties.put(entry.getKey(), Arrays.asList((Object[]) anotherProperty));
                } else {
                    anotherProperties.put(entry.getKey(), anotherProperty);
                }
            }
            return Maps.difference(oneProperties, anotherProperties);
        } catch (Exception ex) {
            throw new RuntimeException("Can't diff " + one + " with " + another, ex);
        }
    }

    /**
     * <h2>对计算出来的差异进行字符化描述</h2>
     *
     * @param differences 差异信息
     * @param ignores     忽略的属性
     * @return 差异描述
     */
    public static String describe(MapDifference differences, String... ignores) {
        if (differences.areEqual()) {
            return "nothing";
        }
        // to be searched
        Arrays.sort(ignores);
        Map<String, MapDifference.ValueDifference> entries = differences.entriesDiffering();
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<String, MapDifference.ValueDifference>> it = entries.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, MapDifference.ValueDifference> entry = it.next();
            if (Arrays.binarySearch(ignores, entry.getKey()) >= 0) continue;
            Object leftValue = entry.getValue().leftValue();
            Object rightValue = entry.getValue().rightValue();
            if (isNullAndEmpty(leftValue, rightValue)) continue;
            sb.append(entry.getKey()).append("(").
                    append(leftValue).append(" -> ").
                      append(rightValue).append(")");
            if (it.hasNext()) sb.append(", ");
        }
        return sb.toString();
    }

    /**
     * <h2>判断第二个数组与第一个数组相比的差异</h2>
     *
     * @param olds       第一个数组
     * @param news       第二个数组
     * @param property  判断两个对象是否是同一个业务对象的属性
     * @param <T>       数组中的对象类型
     * @return 包括差异信息的数组，0->数组中的内容为更新的对象，1->为新增的对象，-1->第三个为删除的对象
     */
    public static <T> Map<Integer,List<T>> diff(T[] olds, T[] news, String property) {
        Map<Integer,List<T>> result = new HashMap<Integer, List<T>>();
        if( olds == null ){
            result.put(0, Collections.EMPTY_LIST); // no updating
            result.put(1, Arrays.asList(news));     // all is creating
            result.put(-1, Collections.EMPTY_LIST);// no deleting
        }else if (news == null ){
            result.put(0, Collections.EMPTY_LIST); // no updating
            result.put(1, Collections.EMPTY_LIST); // no creating
            result.put(-1, Arrays.asList(olds));    // all is deleting
        }else{
            List<T> updating = new ArrayList<T>();
            List<T> creating = new ArrayList<T>(Arrays.asList(news));
            List<T> deleting = new ArrayList<T>(Arrays.asList(olds));
            Iterator<T> it = creating.iterator();
            try{
                while (it.hasNext()) {
                    T actual = it.next();
                    Object id = PropertyUtils.getProperty(actual, property);
                    if( id == null )  {
                        //认为id = null的对象一定是要新增的
                        //也不存在legacy的对象id为null
                        continue;
                    }
                    Object found = null;
                    Iterator<T> it2 = deleting.iterator();
                    while (it2.hasNext()) {
                        T legacy = it2.next();
                        Object legacyId = PropertyUtils.getProperty(legacy, property);
                        if( legacyId == null )
                            throw new IllegalArgumentException(property + " of " + legacy + " is null");
                        if(legacyId.equals(id)){
                            found = legacy;
                            it2.remove();     // remove from deleting
                            break;
                        }
                    }
                    if( found != null ){
                        it.remove();          // remove from creating
                        updating.add(actual); //add into updating
                    }
                }
            }catch (Exception ex){
                throw new RuntimeException("Error while diff two collection ", ex);
            }
            result.put(0, updating);
            result.put(1, creating);
            result.put(-1, deleting);
        }
        return result;
    }

    /**
     * <h2>判断两个对象是否是null或者empty</h2>
     *
     * @param leftValue  左值
     * @param rightValue 右值
     * @return 判断结果
     */
    static boolean isNullAndEmpty(Object leftValue, Object rightValue) {
        if (leftValue == null) {
            if (rightValue instanceof Collection) {
                return ((Collection) rightValue).isEmpty();
            }
            if (rightValue instanceof String) {
                return StringUtils.isBlank((String) rightValue);
            }
        }
        if (rightValue == null) {
            if (leftValue instanceof Collection) {
                return ((Collection) leftValue).isEmpty();
            }
            if (leftValue instanceof String) {
                return StringUtils.isBlank((String) leftValue);
            }
        }
        return false;
    }
}
