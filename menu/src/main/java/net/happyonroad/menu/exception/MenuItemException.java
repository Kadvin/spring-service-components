package net.happyonroad.menu.exception;

import net.happyonroad.platform.service.ServiceException;

/**
 * <h1>自定义菜单异常类</h1>
 */
public class MenuItemException extends ServiceException {
    public MenuItemException(String message) {
        super(message);
    }
}
