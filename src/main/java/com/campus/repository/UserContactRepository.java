package com.campus.repository;

import com.campus.entity.UserContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserContactRepository extends JpaRepository<UserContact, Integer>, JpaSpecificationExecutor<UserContact> {

    /**
     * 查找指定用户的所有联系方式
     */
    List<UserContact> findByUserId(Integer userId);

    /**
     * 根据名称和用户ID查找 (用于防止重复添加同名联系方式)
     */
    UserContact findByNameAndUserId(String name, Integer userId);
}