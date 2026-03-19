package com.campus.controller.admin;

import com.campus.entity.IdleItem;
import com.campus.entity.TradeOrder;
import com.campus.entity.User;
import com.campus.service.IdleItemService;
import com.campus.service.TradeOrderService;
import com.campus.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/order")
public class AdminTradeOrderController {

    @Resource
    private TradeOrderService tradeOrderService;
    @Resource
    private UserService userService;
    @Resource
    private IdleItemService idleItemService;

    @RequestMapping("/list")
    public Map<String, Object> list(TradeOrder search,
                                    @RequestParam(value = "page", defaultValue = "1") Integer page,
                                    @RequestParam(value = "rows", defaultValue = "10") Integer rows) {
        Page<TradeOrder> orderPage = tradeOrderService.list(page, rows, search);

        orderPage.getContent().forEach(order -> {
            IdleItem item = idleItemService.findById(order.getItemId());
            if(item != null) order.setItemName(item.getName());

            User user = userService.findById(order.getBuyerId());
            if(user != null) order.setBuyerName(user.getUsername());
        });

        Map<String, Object> map = new HashMap<>();
        map.put("rows", orderPage.getContent());
        map.put("total", orderPage.getTotalElements());
        return map;
    }

    @PostMapping("/delete")
    public Map<String, Object> delete(String ids) {
        Map<String, Object> map = new HashMap<>();
        String[] idArray = ids.split(",");
        for (String idStr : idArray) {
            int id = Integer.parseInt(idStr);
            TradeOrder order = tradeOrderService.findById(id);
            if (order != null) {
                // 恢复商品状态
                IdleItem item = idleItemService.findById(order.getItemId());
                if (item != null) {
                    item.setStatus(1); // 重新上架
                    idleItemService.save(item);
                }
                tradeOrderService.delete(id);
            }
        }
        map.put("success", true);
        return map;
    }
}