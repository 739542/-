package com.campus.controller.admin;

import com.campus.entity.Notification;
import com.campus.entity.User;
import com.campus.service.NotificationService;
import com.campus.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/notification") // URL 从 /admin/message 变为 /admin/notification
public class AdminNotificationController {

    @Resource
    private NotificationService notificationService;
    @Resource
    private UserService userService;

    @RequestMapping("/list")
    public Map<String, Object> list(Notification search,
                                    @RequestParam(value = "page", defaultValue = "1") Integer page,
                                    @RequestParam(value = "rows", defaultValue = "10") Integer rows) {
        if (search.getUserName() != null && !search.getUserName().isEmpty()) {
            User user = userService.findByUsername(search.getUserName());
            search.setUserId(user != null ? user.getId() : -1);
        }

        Page<Notification> pageList = notificationService.list(page, rows, search);

        pageList.getContent().forEach(n -> {
            User user = userService.findById(n.getUserId());
            if(user != null) n.setUserName(user.getUsername());
        });

        Map<String, Object> map = new HashMap<>();
        map.put("rows", pageList.getContent());
        map.put("total", pageList.getTotalElements());
        return map;
    }

    @PostMapping("/delete")
    public Map<String, Object> delete(String ids) {
        Map<String, Object> map = new HashMap<>();
        String[] idArray = ids.split(",");
        for (String id : idArray) {
            notificationService.delete(Integer.parseInt(id));
        }
        map.put("success", true);
        return map;
    }
}