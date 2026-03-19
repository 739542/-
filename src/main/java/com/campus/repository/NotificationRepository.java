package com.campus.repository;

import com.campus.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer>, JpaSpecificationExecutor<Notification> {

    long countByUserIdAndIsRead(Integer userId, Integer isRead);

    /**
     * 查询两个用户关于某个商品的聊天记录
     * 逻辑：(接收者=A AND 发送者=B AND 商品=X) OR (接收者=B AND 发送者=A AND 商品=X)
     * 按时间升序排列
     */
    @Query(value = "SELECT * FROM notifications WHERE item_id = ?3 AND ((user_id = ?1 AND sender_id = ?2) OR (user_id = ?2 AND sender_id = ?1)) ORDER BY create_time ASC", nativeQuery = true)
    List<Notification> findChatHistory(Integer userId1, Integer userId2, Integer itemId);
}