/**
 * Developer: Kadvin Date: 15/1/23 下午2:24
 */
package net.happyonroad.event;

import net.happyonroad.component.core.Component;

/**
 * the service package is loaded and ready for further works
 */
public class ExtensionLoadedEvent extends ExtensionEvent {
    public ExtensionLoadedEvent(Component source) {
        super(source);
    }
}
