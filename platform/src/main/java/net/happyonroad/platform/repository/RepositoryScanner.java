/**
 * xiongjie on 14-8-6.
 */
package net.happyonroad.platform.repository;

/**
 * <h1>DB Repository Scanner</h1>
 */
public interface RepositoryScanner {

    /**
     * Configure mybatis by config resource location
     *
     * @param config the config resource location
     * @throws Exception any exception while configure
     */
    void configure(String config) throws Exception;

    /**
     * Scan mybatis repository/mapper xml in given packages
     *
     * @param packages the packages to be scanned
     * @return scanned packages
     */
    int scan(String... packages);

}
