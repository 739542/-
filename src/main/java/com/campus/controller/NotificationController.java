package com.campus.controller;

import com.campus.entity.Notification;
import com.campus.entity.User;
import com.campus.service.NotificationService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/notification")
public class NotificationController {

    @Resource
    private NotificationService notificationService;

    @RequestMapping("/my")
    public ModelAndView myNotification(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return new ModelAndView("redirect:/login");

        ModelAndView mav = new ModelAndView("index");
        Notification search = new Notification();
        search.setUserId(user.getId());

        Page<Notification> list = notificationService.list(1, 100, search);

        mav.addObject("messageList", list.getContent());
        mav.addObject("mainPage", "page/myMessage");
        mav.addObject("title", "我的消息");
        return mav;
    }

    /**
     * 获取聊天记录
     */
    @GetMapping("/chatHistory")
    @ResponseBody
    public Map<String, Object> chatHistory(Integer otherUserId, Integer itemId, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            map.put("success", false);
            return map;
        }

        List<Notification> history = notificationService.getChatHistory(user.getId(), otherUserId, itemId);
        map.put("success", true);
        map.put("data", history);
        map.put("currentUserId", user.getId());
        return map;
    }

    /**
     * 发送回复
     */
    @PostMapping("/reply")
    @ResponseBody
    public Map<String, Object> reply(Integer receiverId, Integer itemId, String content, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            map.put("success", false);
            return map;
        }

        Notification notification = new Notification();
        notification.setUserId(receiverId); // 接收者
        notification.setSenderId(user.getId()); // 发送者：我
        notification.setItemId(itemId);
        notification.setContent(content);
        notification.setCreateTime(new Date());
        notification.setIsRead(0);

        notificationService.save(notification);
        map.put("success", true);
        return map;
    }
}