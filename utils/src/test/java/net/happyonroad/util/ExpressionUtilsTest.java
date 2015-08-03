package net.happyonroad.util;

import net.happyonroad.el.Calculator;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExpressionUtilsTest {
    @Test
    public void testCompileLocateByIndex() throws Exception {
        Calculator calculator = ExpressionUtils.compile("result[0]");
        assertEquals("[0]", calculator.toString());
    }

    @Test
    public void testCompileGetByProperty() throws Exception {
        Calculator calculator = ExpressionUtils.compile("result['name']");
        assertEquals("['name']", calculator.toString());
    }

    @Test
    public void testCompileCompareByProperty() throws Exception {
        Calculator calculator = ExpressionUtils.compile("result['name' = \"linux\"]");
        assertEquals("['name' = \"linux\"]", calculator.toString());
    }

    @Test
    public void testCompileCompound() throws Exception {
        Calculator calculator = ExpressionUtils.compile("value['name' = \"linux\"][0]['age']");
        assertEquals("result['name' = \"linux\"][0]['age']", calculator.toString());
    }
}