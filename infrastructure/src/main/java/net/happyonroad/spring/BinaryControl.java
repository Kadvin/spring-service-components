/**
 * @author XiongJie, Date: 13-12-5
 */
package net.happyonroad.spring;

import org.springframework.jmx.export.annotation.ManagedAttribute;

/**
 * Advice for binary or not
 */
public class BinaryControl extends Bean {
    private boolean binary = true;

    @ManagedAttribute
    public boolean isBinary() {
        return binary;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setBinary(boolean binary) {
        this.binary = binary;
    }
}
