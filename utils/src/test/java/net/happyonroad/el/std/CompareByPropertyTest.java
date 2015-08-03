package net.happyonroad.el.std;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CompareByPropertyTest {
    HashMap<String, Object>[] candidates;

    @Before
    public void setUp() throws Exception {
        candidates = new HashMap[2];
        candidates[0] = new HashMap<String, Object>();
        candidates[0].put("IfIndex", 123);
        candidates[0].put("IfDescr", "lo0");
        candidates[0].put("IfUNcastPkts", 1000);
        candidates[1] = new HashMap<String, Object>();
        candidates[1].put("IfIndex", 124);
        candidates[1].put("IfDescr", "eth0");
        candidates[1].put("IfUNcastPkts", 2000);
    }

    @Test
    public void testCalcNumber() throws Exception {
        CompareByProperty<Map> calculator = new CompareByProperty<Map>("IfIndex", "=", "123");
        Map found = calculator.calc(candidates);
        assertEquals(found, candidates[0]);
    }

    @Test
    public void testCalcString() throws Exception {
        CompareByProperty<Map> calculator = new CompareByProperty<Map>("IfDescr", "=", "\"lo0\"");
        Map found = calculator.calc(candidates);
        assertEquals(found, candidates[0]);
    }
}