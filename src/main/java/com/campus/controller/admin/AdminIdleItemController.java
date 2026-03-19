package com.campus.controller.admin;

import com.campus.entity.IdleItem;
import com.campus.entity.User;
import com.campus.service.CategoryService;
import com.campus.service.IdleItemService;
import com.campus.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 后台物品管理
 */
@RestController
@RequestMapping("/admin/item")
public class AdminIdleItemController {

    @Resource
    private IdleItemService idleItemService;
    @Resource
    private CategoryService categoryService;
    @Resource
    private UserService userService;

    @RequestMapping("/list")
    public Map<String, Object> list(IdleItem searchItem,
                                    @RequestParam(value = "page", defaultValue = "1") Integer page,
                                    @RequestParam(value = "rows", defaultValue = "10") Integer rows) {
        // 如果有用户名搜索，先转为 userId
        if (searchItem.getUserName() != null && !searchItem.getUserName().isEmpty()) {
            User user = userService.findByUsername(searchItem.getUserName());
            if (user != null) {
                searchItem.setUserId(user.getId());
            } else {
                searchItem.setUserId(-1); // 查无此人
            }
        }

        Page<IdleItem> itemPage = idleItemService.list(page, rows, searchItem);

        // 填充关联数据
        itemPage.getContent().forEach(item -> {
            if (item.getCategoryId() != null) {
                item.setCategoryName(categoryService.findById(item.getCategoryId()).getName());
            }
            if (item.getUserId() != null) {
                User u = userService.findById(item.getUserId());
                if (u != null) item.setSellerName(u.getUsername());
            }
        });

        Map<String, Object> map = new HashMap<>();
        map.put("rows", itemPage.getContent());
        map.put("total", itemPage.getTotalElements());
        return map;
    }

    @PostMapping("/updateStatus")
    public Map<String, Object> updateStatus(Integer id, Integer status, Integer isRecommended) {
        Map<String, Object> map = new HashMap<>();
        IdleItem item = idleItemService.findById(id);
        if (item == null) {
            map.put("success", false);
            return map;
        }

        if (status != null) item.setStatus(status);
        if (isRecommended != null) item.setIsRecommended(isRecommended);

        idleItemService.save(item);
        map.put("success", true);
        return map;
    }

    @PostMapping("/delete")
    public Map<String, Object> delete(String ids) {
        Map<String, Object> map = new HashMap<>();
        String[] idArray = ids.split(",");
        for (String id : idArray) {
            idleItemService.delete(Integer.parseInt(id));
        }
        map.put("success", true);
        return map;
    }
}