package com.campus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users") // 对应数据库表 users
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "nick_name", length = 50)
    private String nickName;

    /**
     * 用户角色: 1-管理员, 2-普通用户
     */
    @Column(nullable = false)
    private Integer type;

    @Column(length = 100)
    private String email;

    /**
     * 状态: 1-正常, 0-封禁
     */
    @Column(nullable = false)
    private Integer status = 1;

    @Column(name = "image_name")
    private String imageName;

    // 验证码不入库
    @Transient
    private String imageCode;
}