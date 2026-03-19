package com.campus.controller.admin;

import com.campus.entity.SiteFeedback;
import com.campus.entity.User;
import com.campus.service.SiteFeedbackService;
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
@RequestMapping("/admin/feedback")
public class AdminSiteFeedbackController {

    @Resource
    private SiteFeedbackService siteFeedbackService;
    @Resource
    private UserService userService;

    @RequestMapping("/list")
    public Map<String, Object> list(SiteFeedback search,
                                    @RequestParam(value = "page", defaultValue = "1") Integer page,
                                    @RequestParam(value = "rows", defaultValue = "10") Integer rows) {
        // 如果按用户名搜索
        if (search.getUserName() != null && !search.getUserName().isEmpty()) {
            User user = userService.findByUsername(search.getUserName());
            search.setUserId(user != null ? user.getId() : -1);
        }

        Page<SiteFeedback> pageList = siteFeedbackService.list(page, rows, search);

        // 填充用户名
        pageList.getContent().forEach(feedback -> {
            User user = userService.findById(feedback.getUserId());
            if (user != null) {
                feedback.setUserName(user.getUsername());
            }
        });

        Map<String, Object> map = new HashMap<>();
        map.put("rows", pageList.getContent());
        map.put("total", pageList.getTotalElements());
        return map;
    }

    @PostMapping("/reply")
    public Map<String, Object> reply(SiteFeedback feedback) {
        Map<String, Object> map = new HashMap<>();
        SiteFeedback old = siteFeedbackService.findById(feedback.getId());
        if (old != null) {
            old.setReply(feedback.getReply());
            // 这里可以补充设置 replyAdminId
            siteFeedbackService.save(old);
            map.put("success", true);
        } else {
            map.put("success", false);
        }
        return map;
    }

    @PostMapping("/delete")
    public Map<String, Object> delete(String ids) {
        Map<String, Object> map = new HashMap<>();
        String[] idArray = ids.split(",");
        for (String id : idArray) {
            siteFeedbackService.delete(Integer.parseInt(id));
        }
        map.put("success", true);
        return map;
    }
}