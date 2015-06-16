package net.happyonroad.support;

import net.happyonroad.service.MigrateService;
import net.happyonroad.spring.Bean;
import org.apache.commons.io.FileUtils;
import org.apache.ibatis.migration.CommandLine;
import org.springframework.context.ApplicationContextException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * <h1>Migrate Manager</h1>
 *
 * @author Jay Xiong
 */
public class MigrateManager extends Bean implements MigrateService {
    private static final String DATE_FORMAT = "yyyyMMddHHmmss";
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
        String filename = getNextIDAsString() + "_" + name.replace(' ', '_') + ".sql";
        File file = new File(base, "scripts/" + filename);
        try {
            FileUtils.touch(file);
        } catch (IOException e) {
            throw new ApplicationContextException("Can't touch " + file, e);
        }
        return file.getPath();
    }

    protected String getNextIDAsString() {
        try {
            // Ensure that two subsequent calls are less likely to return the same value.
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // ignore
        }
        String timezone = "GMT+8:00";
        final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        final Date now = new Date();
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        return dateFormat.format(now);
    }

}
