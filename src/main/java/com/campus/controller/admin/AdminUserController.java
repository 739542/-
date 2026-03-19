package com.campus.controller.admin;

import com.campus.entity.User;
import com.campus.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/user")
public class AdminUserController {

    @Resource
    private UserService userService;

    @RequestMapping("/list")
    public Map<String, Object> list(User searchUser,
                                    @RequestParam(value = "page", defaultValue = "1") Integer page,
                                    @RequestParam(value = "rows", defaultValue = "10") Integer rows) {
        searchUser.setType(2); // 只查询普通用户
        Page<User> userPage = userService.list(page, rows, searchUser);

        Map<String, Object> map = new HashMap<>();
        map.put("rows", userPage.getContent());
        map.put("total", userPage.getTotalElements());
        return map;
    }

    @PostMapping("/save")
    public Map<String, Object> save(User user) {
        Map<String, Object> map = new HashMap<>();
        if (user.getId() == null) {
            user.setType(2); // 默认为普通用户
            user.setStatus(1); // 默认为正常
        }
        userService.save(user);
        map.put("success", true);
        return map;
    }

    @PostMapping("/toggleStatus") // 合并 banUser
    public Map<String, Object> toggleStatus(Integer id, Integer status) {
        Map<String, Object> map = new HashMap<>();
        User user = userService.findById(id);
        if (user != null) {
            user.setStatus(status);
            userService.save(user);
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
            userService.delete(Integer.parseInt(id));
        }
        map.put("success", true);
        return map;
    }
}