package com.campus.repository;

import com.campus.entity.SiteFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteFeedbackRepository extends JpaRepository<SiteFeedback, Integer>, JpaSpecificationExecutor<SiteFeedback> {
    // 没必要再添加功能了
}