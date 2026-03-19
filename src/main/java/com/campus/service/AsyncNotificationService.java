package com.campus.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 处理多线程异步任务的服务
 */
@Service
public class AsyncNotificationService {

    /**
     * 发送通知
     * @param sellerName 卖家名字
     * @param itemName 商品名字
     */
    //
    @Async("taskExecutor")
    public void sendOrderNotification(String sellerName, String itemName) {
        try {
            // 获取当前线程的名字，证明是新线程在跑
            String threadName = Thread.currentThread().getName();

            System.out.println("【" + threadName + "】正在连接邮件服务器...");
            System.out.println("【" + threadName + "】准备给卖家 [" + sellerName + "] 发送关于 [" + itemName + "] 的预定通知");
            Thread.sleep(2000);

            System.out.println("【" + threadName + "】通知发送成功！");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}