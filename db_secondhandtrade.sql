/*
SQLyog Ultimate v12.08 (64 bit)
MySQL - 8.0+ : Database - campus_market
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;
/*!40101 SET SQL_MODE=''*/;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- 创建新数据库
CREATE DATABASE /*!32312 IF NOT EXISTS*/`campus_market` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `campus_market`;

/* 1. 用户表 (原 t_user -> users) */
DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
                         `id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                         `username` varchar(50) NOT NULL COMMENT '用户名',
                         `password` varchar(50) NOT NULL COMMENT '密码',
                         `nick_name` varchar(50) DEFAULT NULL COMMENT '昵称',
                         `type` int NOT NULL DEFAULT '2' COMMENT '角色: 1-管理员, 2-普通用户',
                         `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
                         `status` int NOT NULL DEFAULT '1' COMMENT '状态: 1-正常, 0-封禁',
                         `image_name` varchar(50) DEFAULT NULL COMMENT '头像文件名',
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

/* 迁移数据: t_user -> users */
INSERT INTO `users`(`id`,`username`,`password`,`nick_name`,`type`,`email`,`status`,`image_name`) VALUES
                                                                                                     (1,'admin','admin','管理员',1,'admin@campus.com',1,NULL),
                                                                                                     (2,'2','2','tom',2,'123@qq.com',1,'202201121805431641981943150.jpg'),
                                                                                                     (3,'3','3','jack',2,'1231@qq.com',0,'202201130053431642006423417.jpg'),
                                                                                                     (56,'1','1','哈哈',2,'122319@qq.com',1,'202204082218211649427501467.jpg'),
                                                                                                     (57,'4','4','4',2,'1203007469@qq.com',1,'202204182049341650286174535.jpg');


/* 2. 物品分类表 (原 t_goods_type -> categories) */
DROP TABLE IF EXISTS `categories`;

CREATE TABLE `categories` (
                              `id` int NOT NULL AUTO_INCREMENT COMMENT '分类ID',
                              `name` varchar(50) NOT NULL COMMENT '分类名称',
                              `sort_order` int DEFAULT NULL COMMENT '排序权重',
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COMMENT='物品分类表';

/* 迁移数据: t_goods_type -> categories */
INSERT INTO `categories`(`id`,`name`,`sort_order`) VALUES
                                                       (1,'电子产品',1),
                                                       (2,'服饰',2),
                                                       (3,'书籍',3),
                                                       (4,'食品',4),
                                                       (5,'生活用品',5),
                                                       (6,'运动器械',6),
                                                       (7,'代步工具',7),
                                                       (8,'求购专区',8);


/* 3. 闲置物品表 (原 t_goods -> idle_items) */
/* 3. 闲置物品表 (原 t_goods -> idle_items) */
DROP TABLE IF EXISTS `idle_items`;

CREATE TABLE `idle_items` (
                              `id` int NOT NULL AUTO_INCREMENT COMMENT '物品ID',
                              `name` varchar(100) NOT NULL COMMENT '物品名称',
                              `description` text COMMENT '物品描述(HTML)',
                              `price` decimal(10,2) NOT NULL COMMENT '现价',
                              `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价',
                              `status` int NOT NULL DEFAULT '0' COMMENT '状态: 0-未审核, 1-上架, 2-驳回, 3-下架, 4-已预订, 5-已售出',
                              `reject_reason` varchar(500) DEFAULT NULL COMMENT '驳回原因',
                              `category_id` int NOT NULL COMMENT '分类ID',
                              `user_id` int NOT NULL COMMENT '卖家ID',
                              `release_time` datetime DEFAULT NULL COMMENT '发布时间',
                              `is_recommended` int DEFAULT '0' COMMENT '是否推荐: 0-否, 1-是',
                              `recommend_start_time` datetime DEFAULT NULL COMMENT '推荐开始时间',
                              `recommend_days` int DEFAULT NULL COMMENT '推荐天数',
                              `view_count` int DEFAULT '0' COMMENT '浏览量',
                              `cover_image` varchar(255) DEFAULT NULL COMMENT '商品封面图片文件名', -- 直接嵌入封面图片字段
                              PRIMARY KEY (`id`),
                              KEY `idx_user_id` (`user_id`),
                              KEY `idx_category_id` (`category_id`),
                              CONSTRAINT `fk_items_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE CASCADE,
                              CONSTRAINT `fk_items_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COMMENT='闲置物品表';

USE `campus_market`;

UPDATE `idle_items`
SET `cover_image` = SUBSTRING_INDEX(SUBSTRING_INDEX(`description`, '/articleImage/', -1), '"', 1)
WHERE `id` > 0  -- 加入主键列条件，触发安全模式放行
  AND `description` LIKE '%/articleImage/%'
  AND `cover_image` IS NULL;

UPDATE `idle_items`
SET `cover_image` = 'default_item.jpg'
WHERE `id` > 0  -- 同理加入主键列
  AND `cover_image` IS NULL;
/* 迁移数据: t_goods -> idle_items */
/* 注意：价格字段类型调整为 decimal(10,2) */
INSERT INTO `idle_items`(`id`,`name`,`description`,`price`,`original_price`,`status`,`reject_reason`,`category_id`,`user_id`,`release_time`,`is_recommended`,`recommend_start_time`,`recommend_days`,`view_count`) VALUES
                                                                                                                                                                                                                       (21,'北美电器（ACA）电风扇','<p><img src=\"/static/images/articleImage/202201160115131642266913331.jpg\" style=\"width:100%\" /></p>',333.00,0.00,1,NULL,5,56,'2022-01-16 01:15:23',0,NULL,NULL,3),
                                                                                                                                                                                                                       (22,'百草味果干大礼包','<p><img src=\"/static/images/articleImage/202201160117501642267070232.jpg\" style=\"width:100%\" /></p>',99.00,0.00,0,NULL,4,56,'2022-01-16 01:17:57',0,NULL,NULL,28),
                                                                                                                                                                                                                       (23,'旺旺旺仔小馒头','<p><img src=\"/static/images/articleImage/202201160119351642267175196.jpg\" style=\"width:100%\" /></p>',15.00,0.00,0,NULL,4,56,'2022-01-16 01:19:40',1,NULL,NULL,55),
                                                                                                                                                                                                                       (24,'加绒加厚保暖上衣','<p><img src=\"/static/images/articleImage/202201160121371642267297550.jpg\" style=\"width:100%\" /></p>',30.00,33.00,1,NULL,2,56,'2022-01-16 01:21:42',1,NULL,NULL,60),
                                                                                                                                                                                                                       (25,'唐狮2022年新款打底衫','<p><img src=\"/static/images/articleImage/202201160122491642267369440.jpg\" style=\"width:100%\" /></p>',60.00,0.00,1,NULL,2,56,'2022-01-16 01:22:54',0,NULL,NULL,18),
                                                                                                                                                                                                                       (26,'优衣库男装摇粒绒','<p><img src=\"/static/images/articleImage/202201160124071642267447440.jpg\" style=\"width:100%\" /></p>',70.00,0.00,1,NULL,2,56,'2022-01-16 01:24:12',1,NULL,NULL,82),
                                                                                                                                                                                                                       (27,'畅享20Plus 8+128G','<p><img src=\"/static/images/articleImage/202201160125321642267532402.jpg\" style=\"width:100%\" /></p>',999.00,0.00,4,NULL,1,56,'2022-01-16 01:25:46',1,NULL,NULL,227),
                                                                                                                                                                                                                       (28,'三星A52千元5G手机','<p><img src=\"/static/images/articleImage/202201160126481642267608246.jpg\" style=\"width:100%\" /></p>',1888.00,0.00,5,NULL,1,56,'2022-01-16 01:26:59',0,'2022-01-19 15:20:55',1,102),
                                                                                                                                                                                                                       (29,'超薄办公一体机电脑','<p><img src=\"/static/images/articleImage/202201160128131642267693062.jpg\" style=\"width:100%\" /></p>',1222.00,0.00,1,NULL,1,56,'2022-01-16 01:28:17',0,NULL,NULL,22),
                                                                                                                                                                                                                       (30,'22寸工业工控平板','<p><img src=\"/static/images/articleImage/202201160130111642267811613.jpg\" style=\"width:100%\" /></p>',999.00,0.00,1,NULL,1,56,'2022-01-16 01:30:16',0,'2022-01-19 23:43:53',2,25),
                                                                                                                                                                                                                       (31,'求购一个机械键盘','<p>200元以内</p>',88.00,0.00,3,NULL,8,56,'2022-01-16 02:05:02',0,'2022-01-18 16:25:40',10,55),
                                                                                                                                                                                                                       (32,'测试商品22222','<p>详情内容...</p>',200.00,0.00,3,'信息不全',8,56,'2022-01-16 02:21:17',0,NULL,NULL,21),
                                                                                                                                                                                                                       (33,'测试商品2','<p>详情内容...</p>',20.00,0.00,2,'价格异常',2,56,'2022-01-16 21:39:20',0,'2022-01-18 16:24:02',10,28),
                                                                                                                                                                                                                       (40,'测试商品1','<p>详情...</p>',1.00,0.00,0,'待审核',3,56,'2022-04-17 22:09:12',0,NULL,NULL,3);


/* 4. 交易订单表 (原 t_reserve_record -> trade_orders) */
DROP TABLE IF EXISTS `trade_orders`;

CREATE TABLE `trade_orders` (
                                `id` int NOT NULL AUTO_INCREMENT COMMENT '订单ID',
                                `item_id` int NOT NULL COMMENT '关联物品ID',
                                `buyer_id` int NOT NULL COMMENT '买家ID',
                                `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                `status` int NOT NULL DEFAULT '0' COMMENT '状态: 0-进行中, 1-已取消, 2-已完成',
                                PRIMARY KEY (`id`),
                                KEY `idx_item_id` (`item_id`),
                                KEY `idx_buyer_id` (`buyer_id`),
                                CONSTRAINT `fk_orders_item` FOREIGN KEY (`item_id`) REFERENCES `idle_items` (`id`) ON DELETE CASCADE,
                                CONSTRAINT `fk_orders_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COMMENT='交易订单表';

/* 迁移数据: t_reserve_record -> trade_orders */
INSERT INTO `trade_orders`(`id`,`item_id`,`buyer_id`,`create_time`,`status`) VALUES
                                                                                 (33,27,2,'2022-04-19 00:31:28',1),
                                                                                 (34,28,2,'2022-04-19 00:32:47',2),
                                                                                 (35,29,2,'2022-04-19 00:33:37',1),
                                                                                 (36,27,2,'2022-04-19 00:41:56',0);


/* 5. 站内反馈表 (原 t_contact -> site_feedbacks) */
DROP TABLE IF EXISTS `site_feedbacks`;

CREATE TABLE `site_feedbacks` (
                                  `id` int NOT NULL AUTO_INCREMENT COMMENT '反馈ID',
                                  `content` varchar(1000) NOT NULL COMMENT '留言内容',
                                  `reply` varchar(1000) DEFAULT NULL COMMENT '管理员回复',
                                  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                  `user_id` int NOT NULL COMMENT '留言用户ID',
                                  `admin_id` int DEFAULT NULL COMMENT '回复管理员ID',
                                  PRIMARY KEY (`id`),
                                  KEY `idx_user_id` (`user_id`),
                                  CONSTRAINT `fk_feedbacks_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COMMENT='站内反馈表';

/* 迁移数据: t_contact -> site_feedbacks */
INSERT INTO `site_feedbacks`(`id`,`content`,`reply`,`create_time`,`user_id`,`admin_id`) VALUES
                                                                                            (8,'系统建议：希望增加深色模式','收到，已记录','2022-01-12 14:39:53',56,1),
                                                                                            (18,'测试留言1','回复测试','2022-01-12 16:12:30',56,1),
                                                                                            (19,'测试留言2','收到','2022-01-12 16:12:34',56,1),
                                                                                            (21,'求购功能怎么用？','请在发布时选择求购分类','2022-01-12 18:00:57',2,1),
                                                                                            (22,'界面有点卡顿',NULL,'2022-03-24 13:34:36',2,NULL);


/* 6. 系统通知表 (原 t_message -> notifications) */
DROP TABLE IF EXISTS `notifications`;

CREATE TABLE `notifications` (
                                 `id` int NOT NULL AUTO_INCREMENT COMMENT '通知ID',
                                 `user_id` int NOT NULL COMMENT '接收用户ID',
                                 `content` varchar(500) NOT NULL COMMENT '通知内容',
                                 `create_time` datetime DEFAULT NULL COMMENT '发送时间',
                                 `is_read` int NOT NULL DEFAULT '0' COMMENT '状态: 0-未读, 1-已读',
                                 PRIMARY KEY (`id`),
                                 KEY `idx_user_id` (`user_id`),
                                 CONSTRAINT `fk_notifications_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COMMENT='系统通知表';

/* 迁移数据: t_message -> notifications */
INSERT INTO `notifications`(`id`,`user_id`,`content`,`create_time`,`is_read`) VALUES
                                                                                  (6,56,'你的商品（优衣库...）已被预定，请联系买家当面交易哦！！','2022-01-18 14:57:32',0),
                                                                                  (7,56,'你的商品（畅享20Plus...）已被预定','2022-01-18 17:11:39',0),
                                                                                  (18,2,'你预订的商品（三星A52...）已被卖家取消预订！！','2022-01-19 00:03:45',0),
                                                                                  (37,56,'你的商品（1）已被卖家取消预订！！','2022-04-19 11:32:30',0);


/* 7. 用户联系方式表 (原 t_contact_information -> user_contacts) */
DROP TABLE IF EXISTS `user_contacts`;

CREATE TABLE `user_contacts` (
                                 `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                 `name` varchar(50) NOT NULL COMMENT '联系方式名称(微信/QQ)',
                                 `content` varchar(100) NOT NULL COMMENT '号码',
                                 `user_id` int NOT NULL COMMENT '所属用户ID',
                                 PRIMARY KEY (`id`),
                                 KEY `idx_user_id` (`user_id`),
                                 CONSTRAINT `fk_contacts_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COMMENT='用户联系方式表';

/* 迁移数据: t_contact_information -> user_contacts */
INSERT INTO `user_contacts`(`id`,`name`,`content`,`user_id`) VALUES
                                                                 (1,'QQ','1203007466',56),
                                                                 (2,'微信','ledao303',56),
                                                                 (5,'手机','13667832012',56),
                                                                 (21,'微信','123434243',3),
                                                                 (22,'微信','2a231',2);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;