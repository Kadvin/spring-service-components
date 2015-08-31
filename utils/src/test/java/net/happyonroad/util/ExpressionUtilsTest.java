package net.happyonroad.util;

import net.happyonroad.el.Calculator;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@SuppressWarnings("unchecked")
public class ExpressionUtilsTest {
    Object[] array = new Object[2];
    Map nic1, nic2;

    @Before
    public void setUp() throws Exception {
        nic1 = new HashMap();
        nic1.put("IfIndex", 1);
        nic1.put("name", "GigaEthernet 0/1");
        nic1.put("flow", 0);
        nic1.put("os", "IOS 10.1");
        nic2 = new HashMap();
        nic2.put("IfIndex", 2);
        nic2.put("name", "FastEthernet 1/1");
        nic2.put("flow", 100);
        nic2.put("os", "VxWorks 2.3.1");
        array[0] = nic1;
        array[1] = nic2;
    }

    @Test
    public void testCalculatorConstNumber() throws Exception {
        Calculator calculator = ExpressionUtils.calculator("12");
        assertEquals("12", calculator.toString());
        assertEquals(12, calculator.calc(Integer.class));
    }

    @Test
    public void testCalculatorConstString() throws Exception {
        Calculator calculator = ExpressionUtils.calculator("\"12\"");
        assertEquals("\"12\"", calculator.toString());
        assertEquals("12", calculator.calc(String.class));
    }

    @Test
    public void testCalculatorConstNull() throws Exception {
        Calculator calculator = ExpressionUtils.calculator("null");
        assertEquals("null", calculator.toString());
        assertNull(calculator.calc(Void.class));
    }

    @Test
    public void testCalculatorLocateByIndex() throws Exception {
        Calculator calculator = ExpressionUtils.calculator("result[0]");
        assertEquals("result[0]", calculator.toString());
        assertTrue(calculator.calc(array) == nic1);
    }


    @Test
    public void testCalculatorGetByBeanProperty() throws Exception {
        Calculator calculator = ExpressionUtils.calculator("result.length");
        assertEquals("result.length", calculator.toString());
        assertEquals(2, calculator.calc(array));
    }

    @Test
    public void testCalculatorGetByBeanMethod() throws Exception {
        Calculator calculator = ExpressionUtils.calculator("nic.size()");
        assertEquals("nic.size()", calculator.toString());
        assertEquals(4, calculator.calc(nic1));
    }

    @Test
    public void testCalculatorGetByMapProperty() throws Exception {
        Calculator calculator = ExpressionUtils.calculator("result['name']");
        assertEquals("result['name']", calculator.toString());
        assertEquals("FastEthernet 1/1", calculator.calc(nic2));
    }

    @Test
    public void testCalculatorCompareByStringProperty() throws Exception {
        Calculator calculator = ExpressionUtils.calculator("result['name' =~ \"Fast\"]");
        assertEquals("result['name' =~ \"Fast\"]", calculator.toString());
        assertTrue(calculator.calc(array) == nic2);
    }

    @Test
    public void testCalculatorCompareByIntProperty() throws Exception {
        Calculator calculator = ExpressionUtils.calculator("result['IfIndex' = 2]");
        assertEquals("result['IfIndex' = 2]", calculator.toString());
        assertTrue(calculator.calc(array) == nic2);
    }

    @Test
    public void testCalculatorCompound() throws Exception {
        Calculator calculator = ExpressionUtils.calculator("value['name' = \"linux\"][0]['age']");
        assertEquals("value['name' = \"linux\"][0]['age']", calculator.toString());
    }

    @Test
    public void testExpressionCalAndConst() throws Exception {
        Calculator<Object, Boolean> expression = ExpressionUtils.expression("result['IfIndex' = 2]['flow'] > 0 ");
        assertEquals("(result['IfIndex' = 2]['flow'] > 0)", expression.toString());
        assertTrue(expression.calc(array));
    }

    @Test
    public void testExpressionCalAndCal() throws Exception {
        Calculator<Object, Boolean> expression =
                ExpressionUtils.expression("result['name' =~ \"Giga\"]['flow'] > result['IfIndex' = 2]['flow'] ");
        assertEquals("(result['name' =~ \"Giga\"]['flow'] > result['IfIndex' = 2]['flow'])", expression.toString());
        assertFalse(expression.calc(array));
    }

    @Test
    public void testExpressionLogic1() throws Exception {
        String expr =
                "((result['name' =~ \"Giga\"]['os'] =~ '^IOS') || (result['name' =~ \"Fast\"]['os'] !~ \"win27\"))";
        Calculator<Object, Boolean> expression = ExpressionUtils.expression(expr);
        assertEquals(expr, expression.toString());
        assertTrue(expression.calc(array));
    }

    @Test
    public void testExpressionLogic2() throws Exception {
        Calculator<Object, Boolean> expression = ExpressionUtils
                .expression("(result['name' = \"linux\"] =~ 'centos') || (result['name' = \"windows\"] !~ \"win27\")");
        assertEquals("((result['name' = \"linux\"] =~ 'centos') || (result['name' = \"windows\"] !~ \"win27\"))",
                     expression.toString());
    }

    @Test
    public void testExpressionLogic3() throws Exception {
        String expr = "((result['name' =~ \"Giga\"]['os'] =~ '^IOS') && " +
                      "(result['name' =~ \"Fast\"]['IfIndex'] > result.length))";
        Calculator<Object, Boolean> expression = ExpressionUtils.expression(expr);
        assertEquals(expr, expression.toString());
        assertFalse(expression.calc(array));
    }

    @Test
    public void testRealCase() throws Exception {
        String expr = "(node.label = '192.168.12.77') and (node.age >= 10)";
        Calculator<Object, Boolean> expression = ExpressionUtils.expression(expr);
        Bean bean = new Bean();
        bean.setLabel("192.168.12.77");
        bean.setAge(10) ;
        assertTrue(expression.calc(bean));
    }

    public static class Bean {
        private String label;
        private int age;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}