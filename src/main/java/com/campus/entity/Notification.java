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
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId; // 接收者ID

    @Column(name = "sender_id")
    private Integer senderId; // 发送者ID

    @Column(name = "item_id")
    private Integer itemId; // 关联商品ID

    @Column(nullable = false, length = 1000)
    private String content; // 纯消息内容

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Column(name = "is_read")
    private Integer isRead = 0; // 0-未读, 1-已读

    // --- 辅助字段 ---

    @Transient
    private User sender; // 发送者对象

    @Transient
    private IdleItem idleItem; // 商品对象

    /**
     * 接收者用户名
     * 1. 用于后台 AdminNotificationController 接收搜索参数
     * 2. 用于后台列表展示
     */
    @Transient
    private String userName;
}