package com.campus.controller;

import com.campus.entity.Category;
import com.campus.entity.IdleItem;
import com.campus.entity.User;
import com.campus.service.CategoryService;
import com.campus.service.IdleItemService;
import com.campus.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Resource
    private UserService userService;
    @Resource
    private IdleItemService idleItemService;
    @Resource
    private CategoryService categoryService;

    @GetMapping("/")
    public ModelAndView root() {
        ModelAndView mav = new ModelAndView();

        IdleItem searchItem = new IdleItem();
        searchItem.setStatus(1);

        // 获取最新发布的物品 (取前9个)
        Page<IdleItem> newItems = idleItemService.list(1, 9, searchItem);
        mav.addObject("newItemList", newItems.getContent());

        // 热门商品 (暂用最新代替)
        mav.addObject("hotItemList", newItems.getContent());

        mav.setViewName("index");
        mav.addObject("mainPage", "page/indexFirst");
        mav.addObject("title", "首页 - 校园闲置易");

        //防止首页头部消失
        mav.addObject("categoryList", categoryService.findAll());
        return mav;
    }

    @GetMapping("/login")
    public ModelAndView toLogin() {
        ModelAndView mav = new ModelAndView("index");
        mav.addObject("mainPage", "page/login");
        mav.addObject("title", "用户登录");
        return mav;
    }

    @GetMapping("/register")
    public ModelAndView toRegister() {
        ModelAndView mav = new ModelAndView("index");
        mav.addObject("mainPage", "page/register");
        mav.addObject("title", "用户注册");
        return mav;
    }

    // 管理员登录
    @ResponseBody
    @RequestMapping("/admin/login")
    public Map<String, Object> adminLogin(User user, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        String checkCode = (String) session.getAttribute("checkCode");

        if (checkCode == null || !checkCode.equalsIgnoreCase(user.getImageCode())) {
            map.put("success", false);
            map.put("errorInfo", "验证码错误");
            return map;
        }

        User dbUser = userService.findByUsername(user.getUsername());
        if (dbUser != null && dbUser.getType() == 1 && dbUser.getPassword().equals(user.getPassword())) {
            session.setAttribute("currentUserAdmin", dbUser);
            map.put("success", true);
        } else {
            map.put("success", false);
            map.put("errorInfo", "用户名或密码错误");
        }
        return map;
    }
}