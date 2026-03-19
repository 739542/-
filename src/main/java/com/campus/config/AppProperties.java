package com.campus.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 应用全局配置类
 */
@Data
@Component
public class  AppProperties {

    /**
     * 发件人邮箱
     */
    @Value("${spring.mail.username:}")
    private String sendMailPerson;

    /**
     * 用户头像存储路径
     * 在application.yml中的file-paths.user-img
     */
    @Value("${file-paths.user-img:/opt/campus-market/images/userImage/}")
    private String userImageFilePath;

    /**
     * 轮播图存储路径
     */
    @Value("${file-paths.banner-img:/opt/campus-market/images/carouselImage/}")
    private String carouselImageFilePath;

    /**
     * 物品/文章图片存储路径
     */
    @Value("${file-paths.item-img:/opt/campus-market/images/articleImage/}")
    private String itemImageFilePath;
}
