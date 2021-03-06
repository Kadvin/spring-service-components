/**
 * Developer: Kadvin Date: 15/1/23 下午2:24
 */
package net.happyonroad.event;

import net.happyonroad.component.core.Component;

/**
 * the service package is unloading
 */
public class ExtensionUnloadingEvent extends ExtensionEvent {
    private static final long serialVersionUID = -5816857946695562970L;

    public ExtensionUnloadingEvent(Component source) {
        super(source);
    }
}
