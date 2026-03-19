package com.campus.config;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web Mvc 配置类
 * 用于配置静态资源映射，使前端可以访问本地磁盘的图片
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private AppProperties appProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射用户头像
        // 访问路径: /static/images/userImage/xxx.jpg
        // 映射到: file:D://campus_data//images//user//xxx.jpg
        registry.addResourceHandler("/static/images/userImage/**")
                .addResourceLocations("file:" + appProperties.getUserImageFilePath());

        // 映射轮播图
        registry.addResourceHandler("/static/images/carouselImage/**")
                .addResourceLocations("file:" + appProperties.getCarouselImageFilePath());

        // 映射物品/文章图片
        registry.addResourceHandler("/static/images/articleImage/**")
                .addResourceLocations("file:" + appProperties.getItemImageFilePath());

        // 映射原本的静态资源 (CSS, JS)
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}