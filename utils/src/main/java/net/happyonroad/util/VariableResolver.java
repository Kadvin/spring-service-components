package net.happyonroad.util;

import java.util.Arrays;
import java.util.Map;

/**
 * <h1>Variable Resolver</h1>
 *
 * @author Jay Xiong
 */
public interface VariableResolver {
    Object resolve(String key);

    /**
     * <h1>根据替换字符串体内的字符，到map中寻找对应的表项</h1>
     * 如: ${name} 的 map resolver 将会 去map中找键为name的value
     */
    public class MapResolver implements VariableResolver {
        private Map<String, Object> map;

        public MapResolver(Map<String, Object> map) {
            this.map = map;
        }

        @Override
        public Object resolve(String key) {
            return map.get(key);
        }

        @Override
        public String toString() {
            return "MapResolver(" + map + ')';
        }
    }

    /**
     * <h1>根据替换字符串体内的字符，到bean中寻找对应的属性值</h1>
     * 如: ${name} 的 bean resolver 将会去读取bean的name属性
     */
    public class BeanResolver implements VariableResolver {
        private Object bean;

        public BeanResolver(Object bean) {
            this.bean = bean;
        }

        @Override
        public Object resolve(String key) {
            try {
                return MiscUtils.getProperty(bean, key);
            } catch (Exception e) {
                throw new IllegalArgumentException("Can't get property " + key + " from " + bean, e);
            }
        }

        @Override
        public String toString() {
            return "BeanResolver(" + bean + ')';
        }
    }

    /**
     * <h1>根据替换字符串体内的位置index，到array中寻找对应的表项</h1>
     * 如: ${2} 的 array resolver 将会去读取array的第三个元素
     */
    public class ArrayResolver implements VariableResolver {
        private Object[] args;

        public ArrayResolver(Object[] args) {
            this.args = args;
        }

        @Override
        public Object resolve(String key) {
            int index = Integer.valueOf(key);
            if (index >= args.length)
                throw new IllegalArgumentException("Can't get value from " +
                                                   StringUtils.join(args, ",") + " at position " + index);
            return args[index];
        }

        @Override
        public String toString() {
            return "ArrayResolver("  + Arrays.toString(args) + ')';
        }
    }

    /**
     * <h1>能够理解缺省字符串</h1>
     * 如: ${name=xxx} 的 Default Aware resolver 将会委托下级resolver读取name值，如果没有读到，则返回 xxx
     */
    public class DefaultAwareResolver implements VariableResolver {
        private VariableResolver resolver;

        public DefaultAwareResolver(VariableResolver resolver) {
            this.resolver = resolver;
        }

        @Override
        public Object resolve(String key) {
            String[] keyAndDefault = key.split("=");
            if( keyAndDefault.length == 2 ){
                String newKey = keyAndDefault[0];
                String defaultValue = keyAndDefault[1];
                Object value = resolver.resolve(newKey);
                if( value == null ) value = defaultValue;
                return value;
            }else{
                //原样委托 可能为 source=
                return resolver.resolve(keyAndDefault[0]);
            }
        }

        @Override
        public String toString() {
            return "DefaultAwareResolver{" + resolver + '}';
        }
    }
}
