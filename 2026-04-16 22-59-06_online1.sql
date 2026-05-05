/*
SQLyog Job Agent Version 10.0 Beta1 Copyright(c) Webyog Inc. All Rights Reserved.


MySQL - 5.7.38 : Database - online_study
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`online_study` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;

USE `online_study`;

/*Table structure for table `category` */

DROP TABLE IF EXISTS `category`;

CREATE TABLE `category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `icon` varchar(50) DEFAULT NULL COMMENT '分类图标',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COMMENT='课程分类表';

/*Data for the table `category` */

insert  into `category` values (5,'编程语言','code',1,'2026-04-15 21:05:29','2026-04-15 21:05:29'),(6,'前端开发','desktop',2,'2026-04-15 21:05:29','2026-04-15 21:05:29'),(7,'后端开发','server',3,'2026-04-15 21:05:29','2026-04-15 21:05:29'),(8,'移动开发','mobile',4,'2026-04-15 21:05:29','2026-04-15 21:05:29'),(9,'数据库','database',5,'2026-04-15 21:05:29','2026-04-15 21:05:29'),(10,'人工智能','ai',6,'2026-04-15 21:05:29','2026-04-15 21:05:29'),(11,'运维技术','setting',7,'2026-04-15 21:05:29','2026-04-15 21:05:29');

/*Table structure for table `chapter` */

DROP TABLE IF EXISTS `chapter`;

CREATE TABLE `chapter` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '章节ID',
  `course_id` bigint(20) NOT NULL COMMENT '课程ID',
  `title` varchar(200) NOT NULL COMMENT '章节标题',
  `chapter_number` int(11) NOT NULL COMMENT '章节序号',
  `description` text COMMENT '章节描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_chapter_number` (`chapter_number`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COMMENT='课程章节表';

/*Data for the table `chapter` */

insert  into `chapter` values (8,1,'Java基础语法',1,'学习Java的基本语法和编程概念','2026-04-15 21:05:29','2026-04-15 21:05:29'),(9,1,'面向对象编程',2,'深入理解面向对象的核心思想','2026-04-15 21:05:29','2026-04-15 21:05:29'),(10,1,'集合框架',3,'掌握Java集合类的使用','2026-04-15 21:05:29','2026-04-15 21:05:29'),(11,2,'Vue基础',1,'Vue 3基础概念和语法','2026-04-15 21:05:29','2026-04-15 21:05:29'),(12,2,'组件化开发',2,'组件的设计和复用','2026-04-15 21:05:29','2026-04-15 21:05:29'),(13,2,'状态管理',3,'Pinia状态管理详解','2026-04-15 21:05:29','2026-04-15 21:05:29'),(14,3,'Spring Boot入门',1,'Spring Boot基础配置和使用','2026-04-15 21:05:29','2026-04-15 21:05:29'),(15,3,'数据访问',2,'MyBatis/JPA数据操作','2026-04-15 21:05:29','2026-04-15 21:05:29'),(16,3,'安全控制',3,'Spring Security权限管理','2026-04-15 21:05:29','2026-04-15 21:05:29');

/*Table structure for table `course` */

DROP TABLE IF EXISTS `course`;

CREATE TABLE `course` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `title` varchar(200) NOT NULL COMMENT '课程标题',
  `description` varchar(500) DEFAULT NULL COMMENT '课程简介',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '封面图片URL',
  `introduction` text COMMENT '课程详细介绍',
  `requirements` text COMMENT '学习要求',
  `target_audience` text COMMENT '目标受众',
  `price` decimal(10,2) DEFAULT '0.00' COMMENT '原价',
  `discount_price` decimal(10,2) DEFAULT '0.00' COMMENT '折扣价',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `teacher_id` bigint(20) NOT NULL COMMENT '讲师ID',
  `lesson_count` int(11) DEFAULT '0' COMMENT '课时总数',
  `student_count` int(11) DEFAULT '0' COMMENT '学习人数',
  `rating` decimal(3,2) DEFAULT '0.00' COMMENT '评分',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态 0-下架 1-上架',
  `recommend` tinyint(4) DEFAULT '0' COMMENT '是否推荐 0-否 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

/*Data for the table `course` */

insert  into `course` values (25,'Vue 3从入门到实战','掌握现代前端框架Vue 3的完整开发流程','https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=400','深入学习Vue 3 Composition API、响应式原理、组件通信等核心技术，通过实战项目巩固所学知识。','HTML/CSS基础,JavaScript基础','前端开发者,想学习Vue的同学','129.00','69.00',2,2,40,856,'4.90',1,0,'2026-04-15 21:05:29','2026-04-15 21:05:29'),(26,'Spring Boot企业级开发','构建现代化的企业级Java应用','https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=400','全面掌握Spring Boot核心特性，包括自动配置、数据访问、安全控制、缓存机制等企业级开发技能。','Java基础,Spring框架基础','Java开发者,架构师','199.00','99.00',3,1,60,567,'4.70',1,0,'2026-04-15 21:05:29','2026-04-15 21:05:29'),(27,'Java零基础入门到精通','从零学习Java编程技术','https://picsum.photos/600/300?random=1','Java基础语法、面向对象、集合、多线程全套教程','无需基础，愿意动手练习','零基础学生、转行人员','99.00','49.00',1,2,55,1320,'4.80',1,1,'2026-04-16 19:03:22','2026-04-16 19:03:22'),(28,'Vue3全家桶实战开发','前端主流框架Vue3完整教程','https://picsum.photos/600/300?random=2','CompositionAPI、Pinia、路由、组件化全套讲解','掌握HTML、CSS、JS基础','前端开发、转行前端人员','129.00','69.00',2,2,48,956,'4.90',1,1,'2026-04-16 19:03:22','2026-04-16 19:03:22'),(29,'SpringBoot微服务实战','Java后端主流框架开发','https://picsum.photos/600/300?random=3','自动配置、接口开发、数据库操作、权限控制','Java基础、Spring基础','Java开发、后端工程师','199.00','99.00',3,2,65,742,'4.70',1,0,'2026-04-16 19:03:22','2026-04-16 19:03:22'),(30,'MySQL数据库优化实战','SQL优化、索引、事务、锁机制','https://picsum.photos/600/300?random=4','数据库性能调优、慢查询分析、索引设计','会基本SQL语句','后端开发、DBA、测试人员','159.00','79.00',4,2,36,631,'4.60',1,1,'2026-04-16 19:03:22','2026-04-16 19:03:22'),(31,'Python数据分析全攻略','数据处理、可视化、分析实战','https://picsum.photos/600/300?random=5','Pandas、Numpy、Matplotlib全套教学','无需编程基础','数据爱好者、转行数据分析','139.00','59.00',5,2,42,845,'4.80',1,0,'2026-04-16 19:03:22','2026-04-16 19:03:22'),(32,'微信小程序开发实战','从零开发上线商业小程序','https://picsum.photos/600/300?random=6','页面开发、接口调用、支付、上线发布','有前端基础更佳','全栈开发、创业者、学生','169.00','89.00',6,2,39,521,'4.70',1,1,'2026-04-16 19:03:22','2026-04-16 19:03:22'),(33,'React框架实战教程','企业级前端React开发','https://picsum.photos/600/300?random=7','Hooks、Redux、路由、组件封装','JS基础扎实','前端工程师、进阶学习者','149.00','79.00',2,2,45,712,'4.60',1,0,'2026-04-16 19:03:22','2026-04-16 19:03:22'),(34,'SpringCloud微服务架构','分布式、高可用微服务体系','https://picsum.photos/600/300?random=8','网关、注册中心、配置中心、链路追踪','SpringBoot基础','架构师、高级后端开发','299.00','149.00',3,2,72,436,'4.90',1,1,'2026-04-16 19:03:22','2026-04-16 19:03:22'),(35,'HTML+CSS零基础入门','网页制作零基础教程','https://picsum.photos/600/300?random=9','网页布局、样式、响应式开发','零基础可学','网页设计、前端入门','59.00','29.00',2,2,28,1532,'4.80',1,1,'2026-04-16 19:03:22','2026-04-16 19:03:22'),(36,'Redis缓存数据库实战','Redis高性能缓存技术','https://picsum.photos/600/300?random=10','数据缓存、分布式锁、消息队列、高级特性','有数据库基础','后端开发、架构师','169.00','89.00',4,2,33,645,'4.70',1,0,'2026-04-16 19:03:22','2026-04-16 19:03:22');

/*Table structure for table `course_review` */

DROP TABLE IF EXISTS `course_review`;

CREATE TABLE `course_review` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `course_id` bigint(20) NOT NULL COMMENT '课程ID',
  `rating` int(11) NOT NULL COMMENT '评分 1-5星',
  `content` text COMMENT '评价内容',
  `like_count` int(11) DEFAULT '0' COMMENT '点赞数',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态 0-隐藏 1-显示',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_course` (`user_id`,`course_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_rating` (`rating`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程评价表';

/*Data for the table `course_review` */

/*Table structure for table `lesson` */

DROP TABLE IF EXISTS `lesson`;

CREATE TABLE `lesson` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '课时ID',
  `chapter_id` bigint(20) NOT NULL COMMENT '章节ID',
  `course_id` bigint(20) NOT NULL COMMENT '课程ID',
  `title` varchar(200) NOT NULL COMMENT '课时标题',
  `description` text COMMENT '课时描述',
  `video_url` varchar(255) DEFAULT NULL COMMENT '视频URL',
  `duration` int(11) DEFAULT '0' COMMENT '视频时长（秒）',
  `lesson_number` int(11) NOT NULL COMMENT '课时序号',
  `is_free` tinyint(4) DEFAULT '0' COMMENT '是否免费 0-收费 1-免费',
  `content` longtext COMMENT '课时内容（富文本）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_chapter_id` (`chapter_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_lesson_number` (`lesson_number`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COMMENT='课时表';

/*Data for the table `lesson` */

insert  into `lesson` values (9,1,1,'Java环境搭建','安装JDK和配置开发环境','https://example.com/video1.mp4',1800,1,1,NULL,'2026-04-15 21:05:29','2026-04-15 21:05:29'),(10,1,1,'第一个Java程序','编写和运行Hello World程序','https://example.com/video2.mp4',1500,2,1,NULL,'2026-04-15 21:05:29','2026-04-15 21:05:29'),(11,1,1,'变量和数据类型','学习Java的基本数据类型','https://example.com/video3.mp4',2100,3,0,NULL,'2026-04-15 21:05:29','2026-04-15 21:05:29'),(12,2,1,'类和对象','理解面向对象的基本概念','https://example.com/video4.mp4',2400,1,0,NULL,'2026-04-15 21:05:29','2026-04-15 21:05:29'),(13,2,1,'继承和多态','掌握继承和多态机制','https://example.com/video5.mp4',2700,2,0,NULL,'2026-04-15 21:05:29','2026-04-15 21:05:29'),(14,4,2,'Vue 3简介','Vue 3的新特性和优势','https://example.com/video6.mp4',1600,1,1,NULL,'2026-04-15 21:05:29','2026-04-15 21:05:29'),(15,4,2,'Composition API','学习组合式API的使用','https://example.com/video7.mp4',2200,2,0,NULL,'2026-04-15 21:05:29','2026-04-15 21:05:29'),(16,7,3,'Spring Boot起步','创建第一个Spring Boot项目','https://example.com/video8.mp4',2000,1,1,NULL,'2026-04-15 21:05:29','2026-04-15 21:05:29'),(17,7,3,'自动配置原理','理解Spring Boot自动配置机制','https://example.com/video9.mp4',2500,2,0,NULL,'2026-04-15 21:05:29','2026-04-15 21:05:29');

/*Table structure for table `note` */

DROP TABLE IF EXISTS `note`;

CREATE TABLE `note` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '笔记ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `course_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '课程ID',
  `lesson_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '课时ID',
  `title` varchar(255) NOT NULL COMMENT '笔记标题',
  `content` text NOT NULL COMMENT '笔记内容',
  `type` int(11) NOT NULL DEFAULT '0' COMMENT '笔记类型：0-课程笔记，1-通用笔记',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_lesson_id` (`lesson_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COMMENT='笔记表';

/*Data for the table `note` */

insert  into `note` values (10,1,1,1,'测试看看','达娃大达瓦低洼地',0,'2026-04-16 21:32:18','2026-04-16 21:32:18'),(11,1,1,1,'大无畏达瓦','大大大大大大',0,'2026-04-16 21:50:17','2026-04-16 21:50:17'),(12,1,1,1,'吴大维','挖都挖对',0,'2026-04-16 22:08:01','2026-04-16 22:08:01'),(14,1,1,1,'低洼低洼顶顶顶顶顶','对大大大大大大',0,'2026-04-16 22:12:19','2026-04-16 22:12:19'),(15,19,25,9,'测试','测试能不看见',0,'2026-04-16 22:25:26','2026-04-16 22:25:34');

/*Table structure for table `study_progress` */

DROP TABLE IF EXISTS `study_progress`;

CREATE TABLE `study_progress` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '进度ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `lesson_id` bigint(20) NOT NULL COMMENT '课时ID',
  `progress` int(11) DEFAULT '0' COMMENT '学习进度百分比 0-100',
  `watch_duration` int(11) DEFAULT '0' COMMENT '已观看时长（秒）',
  `is_completed` tinyint(4) DEFAULT '0' COMMENT '是否完成 0-未完成 1-已完成',
  `last_study_time` datetime DEFAULT NULL COMMENT '最后学习时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_lesson` (`user_id`,`lesson_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_lesson_id` (`lesson_id`),
  KEY `idx_last_study_time` (`last_study_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习进度表';

/*Data for the table `study_progress` */

/*Table structure for table `study_record` */

DROP TABLE IF EXISTS `study_record`;

CREATE TABLE `study_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `course_id` bigint(20) NOT NULL COMMENT '课程ID',
  `lesson_id` bigint(20) NOT NULL COMMENT '课时ID',
  `duration` int(11) DEFAULT '0' COMMENT '学习时长（分钟）',
  `progress` int(11) DEFAULT '0' COMMENT '学习进度百分比',
  `study_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '学习时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_study_time` (`study_time`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COMMENT='学习记录表';

/*Data for the table `study_record` */

insert  into `study_record` values (1,2,1,1,45,100,'2024-01-15 14:30:00','2026-02-19 17:26:38','2026-02-19 17:26:38'),(2,2,1,2,38,100,'2024-01-15 10:15:00','2026-02-19 17:26:38','2026-02-19 17:26:38'),(3,2,2,3,25,75,'2024-01-14 16:20:00','2026-02-19 17:26:38','2026-02-19 17:26:38'),(4,2,2,4,42,30,'2024-01-14 14:00:00','2026-02-19 17:26:38','2026-02-19 17:26:38'),(5,2,3,5,55,100,'2024-01-13 19:30:00','2026-02-19 17:26:38','2026-02-19 17:26:38'),(6,2,4,6,35,45,'2024-01-13 15:45:00','2026-02-19 17:26:38','2026-02-19 17:26:38');

/*Table structure for table `teacher` */

DROP TABLE IF EXISTS `teacher`;

CREATE TABLE `teacher` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '讲师ID',
  `user_id` bigint(20) DEFAULT NULL COMMENT '关联用户ID',
  `name` varchar(50) NOT NULL COMMENT '讲师姓名',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `title` varchar(100) DEFAULT NULL COMMENT '职称',
  `introduction` text COMMENT '讲师介绍',
  `specialty` varchar(255) DEFAULT NULL COMMENT '专长领域',
  `experience` text COMMENT '教学经验',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COMMENT='讲师表';

/*Data for the table `teacher` */

insert  into `teacher` values (1,NULL,'张老师',NULL,'高级软件工程师','拥有10年Java开发经验，专注于后端架构设计，曾就职于知名互联网公司',NULL,NULL,'2026-02-15 18:49:28','2026-02-15 18:49:28'),(2,NULL,'李老师',NULL,'前端技术专家','资深前端工程师，擅长Vue/React框架，有丰富的前端项目实战经验',NULL,NULL,'2026-02-15 18:49:28','2026-02-15 18:49:28'),(3,NULL,'王老师',NULL,'全栈开发工程师','精通前后端开发，对微服务架构有深入研究',NULL,NULL,'2026-02-15 18:49:28','2026-02-15 18:49:28'),(4,NULL,'张老师',NULL,'高级软件工程师','拥有10年Java开发经验，专注于后端架构设计，曾就职于知名互联网公司',NULL,NULL,'2026-04-15 21:05:29','2026-04-15 21:05:29'),(5,NULL,'李老师',NULL,'前端技术专家','资深前端工程师，擅长Vue/React框架，有丰富的前端项目实战经验',NULL,NULL,'2026-04-15 21:05:29','2026-04-15 21:05:29'),(6,NULL,'王老师',NULL,'全栈开发工程师','精通前后端开发，对微服务架构有深入研究',NULL,NULL,'2026-04-15 21:05:29','2026-04-15 21:05:29');

/*Table structure for table `teacher_apply` */

DROP TABLE IF EXISTS `teacher_apply`;

CREATE TABLE `teacher_apply` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `real_name` varchar(50) NOT NULL COMMENT '真实姓名',
  `major` varchar(100) NOT NULL COMMENT '专业',
  `qualification` text NOT NULL COMMENT '资质说明',
  `status` tinyint(4) DEFAULT '0' COMMENT '状态 0-待审核 1-通过 2-拒绝',
  `reviewer_id` bigint(20) DEFAULT NULL COMMENT '审核人ID',
  `review_time` datetime DEFAULT NULL COMMENT '审核时间',
  `review_remark` text COMMENT '审核备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COMMENT='教师申请表';

/*Data for the table `teacher_apply` */

insert  into `teacher_apply` values (1,1,'无敌','武道','天下无敌，举世无双。',1,19,'2026-04-15 22:02:22',NULL,'2026-04-15 21:45:39','2026-04-15 22:02:22'),(3,23,'战无','舞蹈','武大会的哈哈段湖底还u皇帝',1,19,'2026-04-15 22:12:50',NULL,'2026-04-15 22:12:28','2026-04-15 22:12:50'),(5,22,'测试用户','计算机科学','测试资质',1,19,'2026-04-15 22:22:22','','2026-04-15 22:20:28','2026-04-15 22:22:22'),(6,24,'林一','计算机','伟大的武侠时代吗斯柯达',1,19,'2026-04-15 22:33:49',NULL,'2026-04-15 22:33:33','2026-04-15 22:33:49'),(14,25,'ling儿','计算机','的哪家快递就安慰的话就我就',0,NULL,NULL,NULL,'2026-04-15 22:48:35','2026-04-15 22:48:35');

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '加密后的密码',
  `email` varchar(100) NOT NULL COMMENT '邮箱',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `gender` tinyint(4) DEFAULT '0' COMMENT '性别 0-未知 1-男 2-女',
  `birthday` datetime DEFAULT NULL COMMENT '生日',
  `role` varchar(20) DEFAULT 'USER' COMMENT '角色 USER-普通用户 ADMIN-管理员',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态 0-禁用 1-正常',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `title` varchar(50) DEFAULT NULL COMMENT '职称',
  `introduction` text COMMENT '个人简介',
  `specialty` varchar(100) DEFAULT NULL COMMENT '专业领域',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

/*Data for the table `user` */

insert  into `user` values (1,'default_user','123456','default@example.com',NULL,NULL,NULL,0,NULL,'STUDENT',1,NULL,'2026-04-16 21:02:07','2026-04-16 21:02:07',NULL,NULL,NULL,NULL),(19,'admin','$2a$10$c979aPAPQ75rXWRwtzLGzO9AbCSHVUTWLeEQICyB5G5coJ2qP6ZZO','2770163475q@gmail.com','管理员',NULL,NULL,0,NULL,'ADMIN',1,'2026-04-16 22:27:05','2026-04-15 21:12:51','2026-04-16 22:27:05',NULL,NULL,NULL,NULL),(20,'student','$2a$10$73fKJ4UarjvRsdlNdqnQheoZB/UWsUuOqbpO2A7uMtYnzMiT74kUe','123123123@gmail.com','测试学生',NULL,NULL,0,NULL,'TEACHER',1,'2026-04-16 22:24:22','2026-04-15 21:16:37','2026-04-16 22:24:22','测试用户20',NULL,'测试资质','计算机科学'),(21,'teacher','$2a$10$z6GJ/sl77x7yscAp4BFWG.agrALwLBJjA/o07AmIyMe1k/hhZdSc.','98214892847@gmail.com','测试教师',NULL,NULL,0,NULL,'TEACHER',1,'2026-04-16 22:12:30','2026-04-15 21:17:06','2026-04-16 22:12:30',NULL,NULL,NULL,NULL),(22,'s221','$2a$10$6.q4oa.deN6pxmKF16m.uuh7eTjrh4sGycQ9.4rzq4voBZQ0zi07C','2722333@gmail.com','wudi',NULL,NULL,0,NULL,'TEACHER',1,'2026-04-16 18:35:45','2026-04-15 21:32:41','2026-04-16 18:35:45','测试用户',NULL,'测试资质','计算机科学'),(24,'stu1','$2a$10$G4k82JPE8LSD1GtxnBFBH.0XKju8OIr.FXtbUuk47BZHcRCpFTJBC','212332131@gmail.com','嗡嗡嗡',NULL,NULL,0,NULL,'USER',1,'2026-04-16 22:25:44','2026-04-15 22:32:53','2026-04-16 22:25:44','林一',NULL,'伟大的武侠时代吗斯柯达','计算机'),(25,'stu2','$2a$10$rVrYFv4aCgXyfQAN5JcUXuTJRU61p78S6JlaXAgMFStGajrlA5w7e','12322223@gmail.com','123123',NULL,NULL,0,NULL,'USER',1,'2026-04-15 22:48:17','2026-04-15 22:46:10','2026-04-15 22:48:17','林二',NULL,'伟大我觉得我拿到看来你我们考虑','计算机'),(26,'123','$2a$10$Kqp/HpJyWbwtnIkUa32W6ef/MmApZwYOYlRVAtNox9KhlO9U1NLsW','2772135q@gmail.com','www',NULL,NULL,0,NULL,'USER',1,NULL,'2026-04-16 19:27:50','2026-04-16 19:27:50',NULL,NULL,NULL,NULL);

/*Table structure for table `user_favorite` */

DROP TABLE IF EXISTS `user_favorite`;

CREATE TABLE `user_favorite` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `course_id` bigint(20) NOT NULL COMMENT '课程ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_course` (`user_id`,`course_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

/*Data for the table `user_favorite` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
