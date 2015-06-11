/**
 * xiongjie on 14-7-23.
 */
package net.happyonroad.platform.repository;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;

import java.sql.Connection;
import java.util.Properties;

/**
 * <h1>修改对象中的schema</h1>
 */
@Intercepts({@Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {Connection.class})})
public class StatementInterceptor implements Interceptor {


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler)invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();
        boundSql.setAdditionalParameter("schema", System.getProperty("db.name"));
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
