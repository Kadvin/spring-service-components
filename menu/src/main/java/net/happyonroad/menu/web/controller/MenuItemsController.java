package net.happyonroad.menu.web.controller;

import net.happyonroad.menu.exception.MenuItemException;
import net.happyonroad.menu.model.MenuItem;
import net.happyonroad.platform.web.annotation.BeforeFilter;
import net.happyonroad.platform.web.controller.ApplicationController;
import net.happyonroad.platform.web.exception.WebClientSideException;
import net.happyonroad.menu.service.MenuItemService;
import net.happyonroad.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * <h1>菜单服务的控制器</h1>
 * <pre>
 * <b>HTTP    URI                                     方法      含义  </b>
 *  GET      /api/menu_items/{path}?tree={bool}      index     列出特定菜单下所有在子菜单，如果没有设定menu path，则列出所有菜单
 *  POST     /api/menu_items/{path}                  create    在特定 menu 下建一个子菜单项
 *  PUT      /api/menu_items/{path}                  update    修改菜单项
 * </pre>
 */
@RestController
@RequestMapping("/api/menu_items/**")
class MenuItemsController extends ApplicationController {

    @Autowired
    private MenuItemService menuItemService;

    private MenuItem target;

    /**
     * <h2>展示用户菜单</h2>
     * <p></p>
     * GET /api/menu_items/{path}?tree={bool}
     *
     * @return 菜单项列表
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public MenuItem[] index(@RequestParam(value = "tree", defaultValue = "true") boolean tree) {
        logger.debug("Listing menuItem");

        List<MenuItem> list;
        if (target == null) {
            list = menuItemService.findAll(tree);
        } else {
            list = menuItemService.findAllByParent(target, tree);
        }

        logger.debug("Listed menuItem number {}", list.size());
        return list.toArray(new MenuItem[list.size()]);
    }

    /**
     * <h2>创建用户菜单</h2>
     * <p></p>
     * POST /api/menu_items/{path}
     *
     * @param menuItem MenuItem实例
     * @return created menu item
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public MenuItem create(@RequestBody @Valid MenuItem menuItem) {
        logger.info("Creating {}", menuItem.getName());

        MenuItem aMenuItem;
        try {
            aMenuItem = menuItemService.create(target, menuItem);
        } catch (MenuItemException e) {
            throw new WebClientSideException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        logger.info("Created  {}", aMenuItem.getName());
        return aMenuItem;
    }

    /**
     * <h2>修改用户菜单</h2>
     * <p></p>
     * PUT /api/menu_items/{path}
     *
     * @param menuItem MenuItem实例
     * @return updated menu item
     */
    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public MenuItem update(@RequestBody @Valid MenuItem menuItem) {
        logger.info("Updating {}", menuItem.getName());

        if (target == null) {
            throw new WebClientSideException(HttpStatus.BAD_REQUEST, "The menu item path must be specified");
        }

        MenuItem aMenuItem;
        try {
            target.apply(menuItem);
            aMenuItem = menuItemService.update(target);
        } catch (MenuItemException e) {
            throw new WebClientSideException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        logger.info("Updated  {}", aMenuItem);
        return aMenuItem;
    }

    /**
     * <h2>获取Request中的path</h2>
     * @param request 请求
     */
    @BeforeFilter
    protected void initTargetMenu(HttpServletRequest request) {

        String path = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

        AntPathMatcher apm = new AntPathMatcher();
        String finalPath = apm.extractPathWithinPattern(bestMatchPattern, path);

        if (StringUtils.isNotBlank(finalPath)) {
            this.target = menuItemService.findByPath(finalPath);
        } else {
            this.target = null;
        }
    }
}
