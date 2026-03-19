package com.campus.controller.admin;

import com.campus.entity.Category;
import com.campus.service.CategoryService;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/category")
public class AdminCategoryController {

    @Resource
    private CategoryService categoryService;

    @GetMapping("/comboList")
    public List<Category> comboList() {
        return categoryService.findAll();
    }

    @RequestMapping("/list")
    public Map<String, Object> list(Category search,
                                    @RequestParam(value = "page", defaultValue = "1") Integer page,
                                    @RequestParam(value = "rows", defaultValue = "10") Integer rows) {
        Page<Category> pageList = categoryService.list(page, rows, search);
        Map<String, Object> map = new HashMap<>();
        map.put("rows", pageList.getContent());
        map.put("total", pageList.getTotalElements());
        return map;
    }

    @PostMapping("/save")
    public Map<String, Object> save(Category category) {
        Map<String, Object> map = new HashMap<>();
        categoryService.save(category);
        map.put("success", true);
        return map;
    }

    @PostMapping("/delete")
    public Map<String, Object> delete(String ids) {
        Map<String, Object> map = new HashMap<>();
        String[] idArray = ids.split(",");
        for (String id : idArray) {
            categoryService.delete(Integer.parseInt(id));
        }
        map.put("success", true);
        return map;
    }
}