/**
 * Developer: Kadvin Date: 15/1/23 下午2:24
 */
package net.happyonroad.event;

import net.happyonroad.component.core.Component;

/**
 * the service package is unloaded
 */
public class ExtensionUnloadedEvent extends ExtensionEvent {
    private static final long serialVersionUID = -9087565335997301408L;

    public ExtensionUnloadedEvent(Component source) {
        super(source);
    }
}
