/**
 * Developer: Kadvin Date: 14-7-15 上午9:55
 */
package org.apache.ibatis.migration;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * 扩展的能够读取 jar path 指定目录下的script的 migration loader
 */
public class FileAndJarMigrationLoader extends FileMigrationLoader implements FileFilter {
    public static final String APP_JAR_MIGRATE = "META-INF/migrate/";
    private File applicationDir;

    public FileAndJarMigrationLoader(File scriptsDir, File applicationDir, String charset, Properties properties) {
        super(scriptsDir, charset, properties);
        this.applicationDir = applicationDir;
    }

    @Override
    public List<Change> getMigrations() {
        List<Change> migrations = super.getMigrations();
        //读取 applicationDir下 app/*/*.jar, lib/*/*.jar, repository/*/*.jar
        //中所有的应用组件中的migrations
        if (applicationDir.exists()) {
            scanAppJars(migrations, "app", "lib", "repository");
        }
        //重新排序
        Change[] changes = migrations.toArray(new Change[migrations.size()]);
        Arrays.sort(changes);
        return Arrays.asList(changes);
    }

    @Override
    public Reader getScriptReader(Change change, boolean undo) {
        //目录下的普通文件对应的change
        if (!change.getFilename().contains("!"))
            return super.getScriptReader(change, undo);
        try {
            String[] jarAndPath = change.getFilename().split("!");
            JarFile jar = new JarFile(jarAndPath[0]);
            ZipEntry entry = jar.getEntry(jarAndPath[1]);
            InputStream stream = jar.getInputStream(entry);
            return new MigrationReader(stream, charset, undo, properties);
        } catch (IOException e) {
            throw new MigrationException("Error reading " + change.getFilename(), e);
        }

    }

    private void scanAppJars(List<Change> migrations, String ... folders) {
        for (String folder : folders) {
            File jarsDir = new File(applicationDir, folder);
            if( !jarsDir.exists() ) continue;
            Collection<File> appJars = new LinkedList<File>();
            findJars(appJars, jarsDir);
            for (File appJar : appJars) {
                try {
                    scanMigrations(appJar, migrations);
                } catch (IOException e) {
                    System.err.println("Can't scan app jar: " + appJar + ", because of :" + e.getMessage());
                }
            }
        }
    }

    private void findJars(Collection<File> jars, File folder){
        File[] files = folder.listFiles(this);
        for (File file : files) {
            if( file.isFile() ) jars.add(file);
            else findJars(jars, file);
        }
    }

    public boolean accept(File pathname) {
        return pathname.isDirectory() ||
               (pathname.getPath().endsWith(".jar") && isApplication(pathname.getParentFile().getName()));
    }

    private void scanMigrations(File file, List<Change> migrations) throws IOException {
        JarFile jar = new JarFile(file);
        try {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory()) continue;
                if (entry.getName().startsWith(APP_JAR_MIGRATE)) {
                    migrations.add(parseChangeFromJarEntry(jar, entry));
                }
            }
        } finally {
            jar.close();
        }
    }

    private Change parseChangeFromJarEntry(JarFile jarFile, JarEntry entry) {
        try {
            String filename = entry.getName().substring(entry.getName().lastIndexOf("/") + 1);
            Change change = new Change();
            String[] parts = filename.split("\\.")[0].split("_");
            change.setId(new BigDecimal(parts[0]));
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < parts.length; i++) {
                if (i > 1) {
                    builder.append(" ");
                }
                builder.append(parts[i]);
            }
            change.setDescription(builder.toString());
            change.setFilename(jarFile.getName() + "!" + entry.getName());
            return change;
        } catch (Exception e) {
            throw new MigrationException("Error parsing change from file.  Cause: " + e, e);
        }
    }

    static boolean isApplication(String briefId) {
        if( briefId.startsWith("net.happyonroad") ) return true;
        //这个属性可以通过设置 app.prefix系统属性进行干预，支持多个prefix用分号分隔
        //当符合该条件的模块没有被解析出来时，系统将会停止解析，抛出异常；
        //而不符合该条件的模块，往往是第三方未用到的模块，解析失败不应该停止
        String prefixes = System.getProperty("app.prefix", "dnt.");
        String[] strings = prefixes.split(";");
        for (String prefix : strings) {
            if (briefId.startsWith(prefix)) return true;
        }
        return false;
    }
}
