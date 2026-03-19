package com.campus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "trade_orders")
public class TradeOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "item_id", nullable = false)
    private Integer itemId;

    @Column(name = "buyer_id", nullable = false)
    private Integer buyerId;

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    /**
     * 状态: 0-交易中, 1-已取消, 2-完成
     */
    @Column(nullable = false)
    private Integer status = 0;

    // --- 辅助字段，不进入数据库 ---
    @Transient
    private IdleItem idleItem;

    @Transient
    private String itemName;

    @Transient
    private String buyerName;

}