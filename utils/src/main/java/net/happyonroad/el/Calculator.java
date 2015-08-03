package net.happyonroad.el;

/**
 * <h1>计算器接口</h1>
 *
 * @author Jay Xiong
 */
public interface Calculator<In, Out> {
    /**
     * <h2>对输入进行计算的算子</h2>
     *
     * @param input 输入对象
     * @return 计算之后的输出
     */
    Out calc(In input) throws CalculateException;

}
