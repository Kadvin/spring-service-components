/**
 * Developer: Kadvin Date: 15/1/26 下午7:39
 */
package net.happyonroad.util;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;

import java.util.EnumMap;
import java.util.EnumSet;

/**
 * <h1>支持 CGLib的相关类的Class Name Id Resolver </h1>
  * 许多模型实例，经过Mybatis的查询出来，构建的实例类如：
  * dnt.monitor.model.MonitorEngine$$EnhancerByCGLIB$$18f0d220
  * 规避 CGLib 产生的代理类
 */
@SuppressWarnings("UnusedDeclaration")
public class CglibCompactClassNameIdResolver extends TypeIdResolverBase {

    public CglibCompactClassNameIdResolver() {
        this(Object.class);
    }

    public CglibCompactClassNameIdResolver(Class baseType) {
        this(SimpleType.construct(baseType), TypeFactory.defaultInstance());
    }

    public CglibCompactClassNameIdResolver(JavaType baseType, TypeFactory typeFactory) {
        super(baseType, typeFactory);
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CLASS;
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> type) {
        String className = _idFrom(value, type);
        return reduceCglibName(className);
    }

    String reduceCglibName(String className) {
        int pos = className.indexOf("$$EnhancerByCGLIB");
        return pos > 0 ? className.substring(0, pos) : className;
    }

    @Override
    public JavaType typeFromId(String id) {
        String className =  reduceCglibName(id);
        return _typeFromId(className, _typeFactory);
    }

    @Override
    public String idFromValue(Object value) {
        String className = _idFrom(value, value.getClass());
        return reduceCglibName(className);
    }

    protected JavaType _typeFromId(String id, TypeFactory typeFactory)
    {
        /* 30-Jan-2010, tatu: Most ids are basic class names; so let's first
         *    check if any generics info is added; and only then ask factory
         *    to do translation when necessary
         */
        if (id.indexOf('<') > 0) {
            // note: may want to try combining with specialization (esp for EnumMap)?
            return typeFactory.constructFromCanonical(id);
        }
        try {
            Class<?> cls =  ClassUtil.findClass(id);
            return typeFactory.constructSpecializedType(_baseType, cls);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Invalid type id '"+id+"' (for id type 'Id.class'): no such class found");
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid type id '"+id+"' (for id type 'Id.class'): "+e.getMessage(), e);
        }
    }

    protected String _idFrom(Object value, Class<?> cls)
    {
        // [JACKSON-380] Need to ensure that "enum subtypes" work too
        if (Enum.class.isAssignableFrom(cls)) {
            if (!cls.isEnum()) { // means that it's sub-class of base enum, so:
                cls = cls.getSuperclass();
            }
        }
        String str = cls.getName();
        if (str.startsWith("java.util")) {
            /* 25-Jan-2009, tatu: There are some internal classes that
             *   we can not access as is. We need better mechanism; for
             *   now this has to do...
             */
            /* Enum sets and maps are problematic since we MUST know
             * type of contained enums, to be able to deserialize.
             * In addition, EnumSet is not a concrete type either
             */
            if (value instanceof EnumSet<?>) { // Regular- and JumboEnumSet...
                Class<?> enumClass = ClassUtil.findEnumType((EnumSet<?>) value);
                // not optimal: but EnumSet is not a customizable type so this is sort of ok
                str = TypeFactory.defaultInstance().constructCollectionType(EnumSet.class, enumClass).toCanonical();
            } else if (value instanceof EnumMap<?,?>) {
                Class<?> enumClass = ClassUtil.findEnumType((EnumMap<?,?>) value);
                Class<?> valueClass = Object.class;
                // not optimal: but EnumMap is not a customizable type so this is sort of ok
                str = TypeFactory.defaultInstance().constructMapType(EnumMap.class, enumClass, valueClass).toCanonical();
            } else {
                String end = str.substring(9);
                if ((end.startsWith(".Arrays$") || end.startsWith(".Collections$"))
                       && str.contains("List")) {
                    /* 17-Feb-2010, tatus: Another such case: result of
                     *    Arrays.asList() is named like so in Sun JDK...
                     *   Let's just plain old ArrayList in its place
                     * NOTE: chances are there are plenty of similar cases
                     * for other wrappers... (immutable, singleton, synced etc)
                     */
                    str = "java.util.ArrayList";
                }
            }
        } else if (str.indexOf('$') >= 0) {
            /* Other special handling may be needed for inner classes, [JACKSON-584].
             * The best way to handle would be to find 'hidden' constructor; pass parent
             * value etc (which is actually done for non-anonymous static classes!),
             * but that is just not possible due to various things. So, we will instead
             * try to generalize type into something we will be more likely to be able
             * construct.
             */
            Class<?> outer = ClassUtil.getOuterClass(cls);
            if (outer != null) {
                /* one more check: let's actually not worry if the declared
                 * static type is non-static as well; if so, deserializer does
                 * have a chance at figuring it all out.
                 */
                Class<?> staticType = _baseType.getRawClass();
                if (ClassUtil.getOuterClass(staticType) == null) {
                    // Is this always correct? Seems like it should be...
                    cls = _baseType.getRawClass();
                    str = cls.getName();
                }
            }
        }
        return str;
    }

}
