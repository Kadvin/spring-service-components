package net.happyonroad.util;

/**
 * <h1>支持泛型的过滤器</h1>
 *
 * @author Jay Xiong
 */
public interface Predicate<T> {
    /**
     * <h2>评估特定对象是否符合条件</h2>
     *
     * @param challenge 被评估的对象
     * @return 是否符合条件
     */
    boolean evaluate(T challenge);

    Predicate TRUE = new Predicate() {
        @Override
        public boolean evaluate(Object challenge) {
            return true;
        }
    };

    Predicate FALSE = new Predicate() {
        @Override
        public boolean evaluate(Object challenge) {
            return false;
        }
    };
}
