package com.campus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "idle_items")
public class IdleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "original_price")
    private Double originalPrice;

    @Column(name = "cover_image")
    private String coverImage;

    /**
     * 状态: 0-未审核, 1-上架, 2-驳回, 3-下架, 4-已预订, 5-已售出
     */
    @Column(nullable = false)
    private Integer status;

    @Column(name = "reject_reason")
    private String rejectReason;

    // 关联字段
    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "release_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date releaseTime;

    // 推荐相关
    @Column(name = "is_recommended")
    private Integer isRecommended;

    @Column(name = "recommend_start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date recommendStartTime;

    @Column(name = "recommend_days")
    private Integer recommendDays;

    @Column(name = "view_count")
    private Integer viewCount;

    // --- 辅助字段 (非数据库映射) ---

    @Transient
    private String categoryName;

    @Transient
    private String sellerName; // 仅存名称

    @Transient
    private User seller;

    @Transient
    private List<UserContact> contactList;

    @Transient
    private String userName;
}