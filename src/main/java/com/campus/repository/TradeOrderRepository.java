package com.campus.repository;

import com.campus.entity.TradeOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TradeOrderRepository extends JpaRepository<TradeOrder, Integer>, JpaSpecificationExecutor<TradeOrder> {

    /**
     * 根据物品ID查找订单列表
     * (修改为返回 List，防止多条订单导致报错)
     */
    List<TradeOrder> findByItemId(Integer itemId);

    // 查找所有订单
    List<TradeOrder> findAllByBuyerId(Integer buyerId);

    /**
     * 根据物品ID和状态查找订单
     */
    TradeOrder findByItemIdAndStatus(Integer itemId, Integer status);

    /**
     * 统计指定用户、指定状态、指定时间范围内的订单数量
     */
    long countByBuyerIdAndStatusAndCreateTimeBetween(Integer buyerId, Integer status, Date startTime, Date endTime);
}