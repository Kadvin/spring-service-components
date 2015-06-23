package net.happyonroad.platform.helper;

import org.apache.ibatis.session.SqlSession;

/**
 * <h1>SQL Session Aware</h1>
 *
 * @author Jay Xiong
 */
public interface SqlSessionAware {
    void setSqlSession(SqlSession session);
}
