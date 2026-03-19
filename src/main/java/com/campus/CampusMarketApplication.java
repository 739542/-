package com.campus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync; // ✅ 1. 新增导入

/**
 * 校园二手交易平台启动类
 */
@ServletComponentScan
@SpringBootApplication
@EnableAsync //Spring异步线程池
public class CampusMarketApplication {
    public static void main(String[] args) {
        SpringApplication.run(CampusMarketApplication.class, args);
    }
}