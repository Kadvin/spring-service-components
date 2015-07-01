/**
 * Developer: Kadvin Date: 15/1/23 下午2:24
 */
package net.happyonroad.event;

import net.happyonroad.component.core.Component;

/**
 * Extension loaded, include system depended loaded library(organized as extension)
 */
public class ExtensionLoadedMockEvent extends ExtensionEvent {
    private static final long serialVersionUID = -238778051719486525L;

    public ExtensionLoadedMockEvent(Component source) {
        super(source);
    }
}
