/**
 * @author XiongJie, Date: 13-8-28
 */
package net.happyonroad.test;

import junit.framework.TestCase;

import java.io.File;
import java.net.URL;

/**
 * 测试类的父类
 */
public class TestSupport extends TestCase{
    protected File zip(String zipFilePath){
        URL resource = getClass().getClassLoader().getResource(zipFilePath);
        if( resource != null ){
            String path = resource.getPath();
            return new File(path);
        }else{
            String msg = "Can't find any zip file with name = " + zipFilePath;
            fail(msg);
            throw new RuntimeException(msg);
        }
    }
}
