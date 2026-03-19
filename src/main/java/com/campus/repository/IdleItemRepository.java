package com.campus.repository;

import com.campus.entity.IdleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IdleItemRepository extends JpaRepository<IdleItem, Integer>, JpaSpecificationExecutor<IdleItem> {

    /**
     * 获取推荐时间过期的商品ID集合
     * 使用 Native Query (原生SQL) 处理日期计算，兼容性更好
     * 逻辑：查找 (推荐状态=1) 且 (推荐开始时间 + 推荐天数 <= 当前时间) 的记录
     */
    @Query(value = "SELECT id FROM idle_items WHERE is_recommended = 1 AND DATE_ADD(recommend_start_time, INTERVAL recommend_days DAY) <= NOW()", nativeQuery = true)
    List<Integer> findExpiredRecommendIds();
}