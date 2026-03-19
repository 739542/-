package com.campus.controller;

import com.campus.entity.IdleItem;
import com.campus.entity.TradeOrder;
import com.campus.entity.User;
import com.campus.service.IdleItemService;
import com.campus.service.TradeOrderService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class TradeOrderController {

    @Resource
    private TradeOrderService tradeOrderService;

    @Resource
    private IdleItemService idleItemService;

    /**
     * 创建订单（预订商品）
     * 包含防恶意取消机制：一天内取消超过3次禁止购买
     */
    @PostMapping("/create")
    public Map<String, Object> create(Integer itemId, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        User user = (User) session.getAttribute("currentUser");

        if (user == null) {
            map.put("success", false);
            map.put("errorInfo", "请先登录");
            return map;
        }

        // 预防恶意取消机制
        // 查询该用户今天取消的订单数
        long todayCancelCount = tradeOrderService.getTodayCancelCount(user.getId());
        if (todayCancelCount >= 3) {
            map.put("success", false);
            map.put("errorInfo", "您今日取消订单次数已达上限(3次)，已被限制购买，请明日再试。");
            return map;
        }
        // ============================

        IdleItem item = idleItemService.findById(itemId);
        if (item == null) {
            map.put("success", false);
            map.put("errorInfo", "物品不存在");
            return map;
        }

        if (item.getStatus() != 1) {
            map.put("success", false);
            map.put("errorInfo", "物品状态不可交易");
            return map;
        }

        // 更新物品状态为已预订 (type=4)
        item.setStatus(4);
        idleItemService.save(item);

        // 创建订单
        TradeOrder order = new TradeOrder();
        order.setItemId(itemId);
        order.setBuyerId(user.getId());
        order.setCreateTime(new Date());
        order.setStatus(0); // 0-交易中

        tradeOrderService.save(order);

        map.put("success", true);
        return map;
    }

    /**
     * 我的预订页面
     */
    @GetMapping("/my")
    public ModelAndView myOrders(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return new ModelAndView("redirect:/login");

        ModelAndView mav = new ModelAndView("index");
        List<TradeOrder> list = tradeOrderService.findAllByBuyerId(user.getId());

        mav.addObject("reserveRecordList", list);
        mav.addObject("mainPage", "page/MyReserveRecord");
        mav.addObject("title", "我的预订");
        return mav;
    }

    /**
     * 取消订单
     * 逻辑：将订单状态置为1（取消），并将商品状态重置为1（上架）
     */
    @RequestMapping("/cancel")
    public ModelAndView cancel(@RequestParam("id") Integer id, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return new ModelAndView("redirect:/login");

        TradeOrder order = tradeOrderService.findById(id);

        // 校验：订单存在 + 是当前用户的订单 + 状态是交易中(0)
        if (order != null && order.getBuyerId().equals(user.getId()) && order.getStatus() == 0) {

            order.setStatus(1); // 1-已取消
            tradeOrderService.save(order);

            // 重新上架商品
            IdleItem item = idleItemService.findById(order.getItemId());
            if (item != null) {
                item.setStatus(1); // 恢复为 1-上架中
                idleItemService.save(item);
            }
        }

        return new ModelAndView("redirect:/order/my");
    }
}