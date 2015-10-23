package net.happyonroad.util;

import net.happyonroad.model.Criteria;

/**
 * <h1>Map Handler</h1>
 *
 * @author Jay Xiong
 */
public class CriteriaHandler extends GenericJsonHandler<Criteria> {
    @Override
    protected Class<Criteria> objectClass() {
        return Criteria.class;
    }
}
