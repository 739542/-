package com.campus.service;

import com.campus.entity.IdleItem;
import com.campus.entity.TradeOrder;
import com.campus.repository.IdleItemRepository;
import com.campus.repository.TradeOrderRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class TradeOrderService {

    private final TradeOrderRepository tradeOrderRepository;
    private final IdleItemRepository idleItemRepository;
    // 异步服务变量
    private final AsyncNotificationService asyncNotificationService;

    public TradeOrderService(TradeOrderRepository tradeOrderRepository,
                             IdleItemRepository idleItemRepository,
                             AsyncNotificationService asyncNotificationService) {
        this.tradeOrderRepository = tradeOrderRepository;
        this.idleItemRepository = idleItemRepository;
        this.asyncNotificationService = asyncNotificationService;
    }

    public List<TradeOrder> findAllByBuyerId(Integer buyerId) {
        List<TradeOrder> orderList = tradeOrderRepository.findAllByBuyerId(buyerId);

        for (TradeOrder order : orderList) {
            if (order.getItemId() != null) {
                IdleItem item = idleItemRepository.findById(order.getItemId()).orElse(null);
                order.setIdleItem(item);
                if (item != null) {
                    order.setItemName(item.getName());
                }
            }
        }
        return orderList;
    }

    public Page<TradeOrder> list(int page, int size, TradeOrder searchOrder) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createTime").descending());

        Specification<TradeOrder> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (searchOrder.getBuyerId() != null) {
                predicates.add(cb.equal(root.get("buyerId"), searchOrder.getBuyerId()));
            }
            if (searchOrder.getItemId() != null) {
                predicates.add(cb.equal(root.get("itemId"), searchOrder.getItemId()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return tradeOrderRepository.findAll(spec, pageable);
    }

    public TradeOrder findById(Integer id) {
        return tradeOrderRepository.findById(id).orElse(null);
    }

    /**
     * 根据物品ID查找有效订单
     */
    public TradeOrder findByItemId(Integer itemId) {
        List<TradeOrder> orders = tradeOrderRepository.findByItemId(itemId);
        if (orders == null || orders.isEmpty()) {
            return null;
        }
        for (TradeOrder order : orders) {
            if (order.getStatus() != 1) {
                return order;
            }
        }
        return orders.get(orders.size() - 1);
    }

    /**
     * 在保存订单时，触发多线程异步通知
     */
    @Transactional
    public void save(TradeOrder order) {
        // 先保存数据
        tradeOrderRepository.save(order);

        // 如果订单状态不是取消（状态1是取消），且有关联物品，才发通知
        if (order.getStatus() != 1 && order.getItemId() != null) {
            IdleItem item = idleItemRepository.findById(order.getItemId()).orElse(null);
            String itemName = (item != null) ? item.getName() : "未知商品";
            String sellerInfo = "卖家(ID:" + (item != null ? item.getUserId() : "未知") + ")";

            // 调用异步线程池
            // 这行代码会立刻返回，具体的发邮件模拟操作会交给后台线程去跑
            asyncNotificationService.sendOrderNotification(sellerInfo, itemName);
        }
    }

    @Transactional
    public void delete(Integer id) {
        tradeOrderRepository.deleteById(id);
    }

    public long getTodayCancelCount(Integer userId) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date start = calendar.getTime();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date end = calendar.getTime();

        return tradeOrderRepository.countByBuyerIdAndStatusAndCreateTimeBetween(userId, 1, start, end);
    }
}