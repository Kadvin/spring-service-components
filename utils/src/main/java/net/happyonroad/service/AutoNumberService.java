/**
 * xiongjie on 14-8-6.
 */
package net.happyonroad.service;

/**
 * <h1>自动序号服务</h1>
 */
public interface AutoNumberService {
    /**
     * 设置某个分类的编序规则
     *
     * @param catalog 分类，如 msu
     * @param rule， 编序规则，如: "msu_%06d/1000"
     *            意思是，第一个为msu_001001，第二个为msu_001002
     */
    void configure(String catalog, String rule);

    /**
     * 为某个分类获取下一个编序
     *
     * @param catalog 分类名称
     * @return 编序字符串
     */
    String next(String catalog);
}
