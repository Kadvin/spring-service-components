package net.happyonroad.service;

/**
 * <h1>Migrate Service</h1>
 *
 * @author Jay Xiong
 */
public interface MigrateService {
    /**
     * Migrate up
     */
    void up();

    /**
     * Create a migrate step
     *
     * @param name step name, such as create_hosts
     * @return the new migrate file path
     */
    String createStep(String name);
}
