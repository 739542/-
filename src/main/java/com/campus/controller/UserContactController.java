package com.campus.controller;

import com.campus.entity.User;
import com.campus.entity.UserContact;
import com.campus.service.UserContactService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/contact") // 对应 UserContact 实体
public class UserContactController {

    @Resource
    private UserContactService userContactService;

    @RequestMapping("/my")
    public ModelAndView myContact(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return new ModelAndView("redirect:/login");

        ModelAndView mav = new ModelAndView("index");
        UserContact search = new UserContact();
        search.setUserId(user.getId());

        Page<UserContact> list = userContactService.list(1, 100, search);
        mav.addObject("contactInformationList", list.getContent());
        mav.addObject("mainPage", "page/myContactInformation");
        mav.addObject("title", "我的联系方式");
        return mav;
    }

    @PostMapping("/save")
    public String save(UserContact contact, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user != null) {
            contact.setUserId(user.getId());
            userContactService.save(contact);
        }
        return "redirect:/contact/my";
    }

    @RequestMapping("/delete")
    public String delete(Integer id) {
        userContactService.delete(id);
        return "redirect:/contact/my";
    }
}