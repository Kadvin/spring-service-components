package net.happyonroad.support;

import net.happyonroad.service.MigrateService;
import net.happyonroad.spring.Bean;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.ibatis.migration.CommandLine;

import java.io.File;
import java.util.Collection;

/**
 * <h1>Migrate Manager</h1>
 *
 * @author Jay Xiong
 */
public class MigrateManager extends Bean implements MigrateService {
    private final String base;

    public MigrateManager(String base) {
        this.base = base;
    }

    @Override
    public void up() {
        new CommandLine(new String[]{"--path=" + base, "up"}).execute();
    }

    @Override
    public String createStep(String name) {
        new CommandLine(new String[]{"--path=" + base, "new", name}).execute();
        Collection<File> files = FileUtils.listFiles(new File(base + "/scripts"), new String[]{"sql"}, false);
        for (File file : files) {
            if (FilenameUtils.getBaseName(file.getName()).contains(name))
                return file.getName();
        }
        throw new IllegalStateException("Failed to create step " + name);
    }
}
