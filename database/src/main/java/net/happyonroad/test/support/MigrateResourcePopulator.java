/**
 * xiongjie on 14-8-6.
 */
package net.happyonroad.test.support;

import net.happyonroad.util.StringUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.util.*;

/**
 * <h1>能够识别 Mybatis Migrate资源，并根据up/down方向决定执行哪个部分script populator</h1>
 */
public class MigrateResourcePopulator implements DatabasePopulator {

    private List<ResourceWithDir> scripts = new ArrayList<ResourceWithDir>();

    private String sqlScriptEncoding;

    private String separator = ScriptUtils.DEFAULT_STATEMENT_SEPARATOR;

    private String commentPrefix = ScriptUtils.DEFAULT_COMMENT_PREFIX;

    private String blockCommentStartDelimiter = ScriptUtils.DEFAULT_BLOCK_COMMENT_START_DELIMITER;

    private String blockCommentEndDelimiter = ScriptUtils.DEFAULT_BLOCK_COMMENT_END_DELIMITER;

    private boolean continueOnError = false;

    private boolean ignoreFailedDrops = false;


    /**
     * Construct a new {@code MigrateResourcePopulator} with default settings.
     * @since 4.0.3
     */
    public MigrateResourcePopulator() {
		/* no-op */
    }

    /**
     * Add a script to execute to initialize or populate the database.
     * @param script the path to an SQL script
     */
    public void addScript(Resource script, String direction) {
        this.scripts.add(new ResourceWithDir(script, direction));
    }


    /**
     * Specify the encoding for SQL scripts, if different from the platform encoding.
     * @param sqlScriptEncoding the encoding used in scripts
     * @see #addScript(Resource, String)
     */
    public void setSqlScriptEncoding(String sqlScriptEncoding) {
        this.sqlScriptEncoding = sqlScriptEncoding;
    }

    /**
     * Specify the statement separator, if a custom one.
     * <p>Defaults to {@code ";"} if not specified and falls back to {@code "\n"}
     * as a last resort; may be set to {@link ScriptUtils#EOF_STATEMENT_SEPARATOR}
     * to signal that each script contains a single statement without a separator.
     * @param separator the script statement separator
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }

    /**
     * Set the prefix that identifies single-line comments within the SQL scripts.
     * <p>Defaults to {@code "--"}.
     * @param commentPrefix the prefix for single-line comments
     */
    public void setCommentPrefix(String commentPrefix) {
        this.commentPrefix = commentPrefix;
    }

    /**
     * Set the start delimiter that identifies block comments within the SQL
     * scripts.
     * <p>Defaults to {@code "/*"}.
     * @param blockCommentStartDelimiter the start delimiter for block comments
     * @since 4.0.3
     * @see #setBlockCommentEndDelimiter
     */
    public void setBlockCommentStartDelimiter(String blockCommentStartDelimiter) {
        this.blockCommentStartDelimiter = blockCommentStartDelimiter;
    }

    /**
     * Set the end delimiter that identifies block comments within the SQL
     * scripts.
     * <p>Defaults to <code>"*&#47;"</code>.
     * @param blockCommentEndDelimiter the end delimiter for block comments
     * @since 4.0.3
     * @see #setBlockCommentStartDelimiter
     */
    public void setBlockCommentEndDelimiter(String blockCommentEndDelimiter) {
        this.blockCommentEndDelimiter = blockCommentEndDelimiter;
    }

    /**
     * Flag to indicate that all failures in SQL should be logged but not cause a failure.
     * <p>Defaults to {@code false}.
     * @param continueOnError {@code true} if script execution should continue on error
     */
    public void setContinueOnError(boolean continueOnError) {
        this.continueOnError = continueOnError;
    }

    /**
     * Flag to indicate that a failed SQL {@code DROP} statement can be ignored.
     * <p>This is useful for non-embedded databases whose SQL dialect does not support an
     * {@code IF EXISTS} clause in a {@code DROP} statement.
     * <p>The default is {@code false} so that if the populator runs accidentally, it will
     * fail fast if the script starts with a {@code DROP} statement.
     * @param ignoreFailedDrops {@code true} if failed drop statements should be ignored
     */
    public void setIgnoreFailedDrops(boolean ignoreFailedDrops) {
        this.ignoreFailedDrops = ignoreFailedDrops;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(Connection connection) throws ScriptException {
        for (ResourceWithDir resource : this.scripts) {

            try {
                ScriptUtils.executeSqlScript(connection,
                        encodeScript(resource.script, resource.direction),
                        this.continueOnError,
                        this.ignoreFailedDrops,
                        this.commentPrefix,
                        this.separator,
                        this.blockCommentStartDelimiter,
                        this.blockCommentEndDelimiter);
            } catch (ScriptException e) {
                throw new RuntimeException("Can't execute " + resource, e);
            }
        }
    }

    /**
     * {@link EncodedResource} is not a sub-type of {@link Resource}. Thus we
     * always need to wrap each script resource in an encoded resource.
     */
    private EncodedResource encodeScript(Resource script, String direction) {
        if( direction == null )
            return new EncodedResource(script, this.sqlScriptEncoding);
        else{
            List<String> lines;
            InputStream stream = null;
            try{
                stream = script.getInputStream();
                lines = IOUtils.readLines(stream, this.sqlScriptEncoding);
            } catch (IOException e) {
                throw new RuntimeException("Can't read the script: " + script, e);
            } finally {
                IOUtils.closeQuietly(stream);
            }
            List<String> remains = new ArrayList<String>(lines.size());
            if( "up".equalsIgnoreCase(direction)) {
                for (String line : lines) {
                    if( line.toUpperCase().contains("@UNDO") )break;
                    remains.add(line);
                }
            }else if ("down".equalsIgnoreCase(direction)){
                boolean remain = false;
                for (String line : lines) {
                    if(remain) remains.add(line);
                    if( line.toUpperCase().contains("@UNDO") ) remain = true;
                }

            }else {
                throw new IllegalArgumentException("Invalid migrate direction " + direction + ", must be `up` or `down`");
            }
            Charset charset = sqlScriptEncoding == null ? Charset.defaultCharset() : Charset.forName(sqlScriptEncoding);
            Resource filtered = new ByteArrayResource(StringUtils.join(remains, "\n").getBytes(charset));
            return new EncodedResource(filtered, charset);
        }
    }

    public void reset() {
        this.scripts.clear();
    }

    public void reverse() {
        Collections.reverse(this.scripts);
        for (ResourceWithDir script : scripts) {
            script.reverse();
        }
    }

    static class ResourceWithDir{
        Resource script;
        String direction;

        public ResourceWithDir(Resource script, String direction) {
            this.script = script;
            this.direction = direction;

        }

        public void reverse() {
            if(StringUtils.equalsIgnoreCase(direction, "up") ){
                direction = "down";
            }else if (StringUtils.equalsIgnoreCase(direction, "down")){
                direction = "up";
            }else {
                throw new UnsupportedOperationException("Can't not reverse the sql script, " +
                                                        "there is a script " + script +
                                                        " with unknown direction " + direction);
            }

        }

        @Override
        public String toString() {
            return direction + ":" + script;
        }
    }

}
