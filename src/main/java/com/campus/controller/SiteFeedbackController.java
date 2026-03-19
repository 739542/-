package com.campus.controller;

import com.campus.entity.SiteFeedback;
import com.campus.entity.User;
import com.campus.service.SiteFeedbackService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/feedback")
public class SiteFeedbackController {

    @Resource
    private SiteFeedbackService siteFeedbackService;

    /**
     * 我的留言列表
     */
    @RequestMapping("/my")
    public ModelAndView myFeedback(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return new ModelAndView("redirect:/login");

        ModelAndView mav = new ModelAndView("index");
        SiteFeedback search = new SiteFeedback();
        search.setUserId(user.getId());

        Page<SiteFeedback> pageList = siteFeedbackService.list(1, 100, search);

        // 变量名: contactList
        mav.addObject("contactList", pageList.getContent());
        mav.addObject("mainPage", "page/myContact");
        mav.addObject("title", "我的留言");
        return mav;
    }

    /**
     * 跳转到联系管理员页面
     */
    @RequestMapping("/toContact")
    public ModelAndView toContact(HttpSession session) {
        if (session.getAttribute("currentUser") == null) return new ModelAndView("redirect:/login");

        ModelAndView mav = new ModelAndView("index");
        mav.addObject("mainPage", "page/contact"); // 指向 templates/page/contact.html
        mav.addObject("title", "联系管理员");
        return mav;
    }

    /**
     * 保存留言 (新增或修改)
     */
    @PostMapping("/save")
    public String save(SiteFeedback feedback, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        if (feedback.getId() == null) {
            feedback.setUserId(user.getId());
            feedback.setCreateTime(new Date());
            siteFeedbackService.save(feedback);
        } else {
            // 修改 (仅限本人且保留原时间)
            SiteFeedback old = siteFeedbackService.findById(feedback.getId());
            if (old != null && old.getUserId().equals(user.getId())) {
                old.setContent(feedback.getContent());
                siteFeedbackService.save(old);
            }
        }
        // 提交后跳转回“我的留言”列表，方便查看
        return "redirect:/feedback/my";
    }

    /**
     * 删除留言
     */
    @RequestMapping("/delete")
    public String delete(@RequestParam("id") Integer id, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";

        SiteFeedback feedback = siteFeedbackService.findById(id);
        if (feedback != null && feedback.getUserId().equals(user.getId())) {
            siteFeedbackService.delete(id);
        }
        return "redirect:/feedback/my";
    }
}