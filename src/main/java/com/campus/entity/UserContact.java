package com.campus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_contacts")
public class UserContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", length = 50) // 例如：微信、手机号
    private String name;

    @Column(name = "content", length = 100) // 具体号码
    private String content;

    @Column(name = "user_id", nullable = false)
    private Integer userId;
}