package net.happyonroad.util;

import net.happyonroad.el.Calculator;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExpressionUtilsTest {
    @Test
    public void testCompileLocateByIndex() throws Exception {
        Calculator calculator = ExpressionUtils.compile("result[0]");
        assertEquals("result[0]", calculator.toString());
    }

    @Test
    public void testCompileGetByProperty() throws Exception {
        Calculator calculator = ExpressionUtils.compile("result['name']");
        assertEquals("result['name']", calculator.toString());
    }

    @Test
    public void testCompileCompareByStringProperty() throws Exception {
        Calculator calculator = ExpressionUtils.compile("result['name' = \"linux\"]");
        assertEquals("result['name' = \"linux\"]", calculator.toString());
    }

    @Test
    public void testCompileCompareByIntProperty() throws Exception {
        Calculator calculator = ExpressionUtils.compile("result['IfIndex' = 2]");
        assertEquals("result['IfIndex' = 2]", calculator.toString());
    }

    @Test
    public void testCompileCompound() throws Exception {
        Calculator calculator = ExpressionUtils.compile("value['name' = \"linux\"][0]['age']");
        assertEquals("value['name' = \"linux\"][0]['age']", calculator.toString());
    }
}