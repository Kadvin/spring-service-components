package net.happyonroad.util;

import org.apache.commons.beanutils.PropertyUtils;

import java.util.Map;

/**
 * <h1>Variable Resolver</h1>
 *
 * @author Jay Xiong
 */
public interface VariableResolver {
    Object resolve(String key);

    public class MapResolver implements VariableResolver{
        private Map<String, Object> map;

        public MapResolver(Map<String, Object> map) {
            this.map = map;
        }

        @Override
        public Object resolve(String key) {
            return map.get(key);
        }
    }

    public class BeanResolver implements VariableResolver{
        private Object bean;

        public BeanResolver(Object bean) {
            this.bean = bean;
        }

        @Override
        public Object resolve(String key) {
            try {
                return PropertyUtils.getProperty(bean, key);
            } catch (Exception e) {
                throw new IllegalArgumentException("Can't get property " + key + " from " + bean, e);
            }
        }
    }

    public class ArrayResolver implements VariableResolver{
        private Object[] args;

        public ArrayResolver(Object[] args) {
            this.args = args;
        }

        @Override
        public Object resolve(String key) {
            int index = Integer.valueOf(key);
            if( index >= args.length)
                throw new IllegalArgumentException("Can't get value from " +
                                                   StringUtils.join(args, ",") + " at position " + index);
            return args[index];
        }
    }
}
