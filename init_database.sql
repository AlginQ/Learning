-- 在线学习系统数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS online_study CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE online_study;

-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL UNIQUE COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '加密后的密码',
  `email` varchar(100) NOT NULL UNIQUE COMMENT '邮箱',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `gender` tinyint DEFAULT '0' COMMENT '性别 0-未知 1-男 2-女',
  `birthday` datetime DEFAULT NULL COMMENT '生日',
  `role` varchar(20) DEFAULT 'USER' COMMENT '角色 USER-普通用户 ADMIN-管理员',
  `status` tinyint DEFAULT '1' COMMENT '状态 0-禁用 1-正常',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 创建课程分类表
CREATE TABLE IF NOT EXISTS `category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) NOT NULL UNIQUE COMMENT '分类名称',
  `icon` varchar(50) DEFAULT NULL COMMENT '分类图标',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程分类表';

-- 创建讲师表
CREATE TABLE IF NOT EXISTS `teacher` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '讲师ID',
  `user_id` bigint DEFAULT NULL COMMENT '关联用户ID',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='讲师表';

-- 创建课程表
CREATE TABLE IF NOT EXISTS `course` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `title` varchar(200) NOT NULL COMMENT '课程标题',
  `description` varchar(500) DEFAULT NULL COMMENT '课程简介',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '封面图片URL',
  `introduction` text COMMENT '课程详细介绍',
  `requirements` text COMMENT '学习要求',
  `target_audience` text COMMENT '目标受众',
  `price` decimal(10,2) DEFAULT '0.00' COMMENT '原价',
  `discount_price` decimal(10,2) DEFAULT '0.00' COMMENT '折扣价',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `teacher_id` bigint NOT NULL COMMENT '讲师ID',
  `lesson_count` int DEFAULT '0' COMMENT '课时总数',
  `student_count` int DEFAULT '0' COMMENT '学习人数',
  `rating` decimal(3,2) DEFAULT '0.00' COMMENT '评分',
  `status` tinyint DEFAULT '1' COMMENT '状态 0-下架 1-上架',
  `recommend` tinyint DEFAULT '0' COMMENT '是否推荐 0-否 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 创建课程章节表
CREATE TABLE IF NOT EXISTS `chapter` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '章节ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `title` varchar(200) NOT NULL COMMENT '章节标题',
  `chapter_number` int NOT NULL COMMENT '章节序号',
  `description` text COMMENT '章节描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_chapter_number` (`chapter_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程章节表';

-- 创建课时表
CREATE TABLE IF NOT EXISTS `lesson` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课时ID',
  `chapter_id` bigint NOT NULL COMMENT '章节ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `title` varchar(200) NOT NULL COMMENT '课时标题',
  `description` text COMMENT '课时描述',
  `video_url` varchar(255) DEFAULT NULL COMMENT '视频URL',
  `duration` int DEFAULT '0' COMMENT '视频时长（秒）',
  `lesson_number` int NOT NULL COMMENT '课时序号',
  `is_free` tinyint DEFAULT '0' COMMENT '是否免费 0-收费 1-免费',
  `content` longtext COMMENT '课时内容（富文本）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_chapter_id` (`chapter_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_lesson_number` (`lesson_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课时表';

-- 创建学习进度表
CREATE TABLE IF NOT EXISTS `study_progress` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '进度ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `lesson_id` bigint NOT NULL COMMENT '课时ID',
  `progress` int DEFAULT '0' COMMENT '学习进度百分比 0-100',
  `watch_duration` int DEFAULT '0' COMMENT '已观看时长（秒）',
  `is_completed` tinyint DEFAULT '0' COMMENT '是否完成 0-未完成 1-已完成',
  `last_study_time` datetime DEFAULT NULL COMMENT '最后学习时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_lesson` (`user_id`,`lesson_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_lesson_id` (`lesson_id`),
  KEY `idx_last_study_time` (`last_study_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习进度表';

-- 插入默认分类数据
INSERT IGNORE INTO `category` (`name`, `icon`, `sort_order`) VALUES
('编程语言', 'code', 1),
('前端开发', 'desktop', 2),
('后端开发', 'server', 3),
('移动开发', 'mobile', 4),
('数据库', 'database', 5),
('人工智能', 'ai', 6),
('运维技术', 'setting', 7);

-- 插入默认讲师数据
INSERT IGNORE INTO `teacher` (`name`, `title`, `introduction`) VALUES
('张老师', '高级软件工程师', '拥有10年Java开发经验，专注于后端架构设计，曾就职于知名互联网公司'),
('李老师', '前端技术专家', '资深前端工程师，擅长Vue/React框架，有丰富的前端项目实战经验'),
('王老师', '全栈开发工程师', '精通前后端开发，对微服务架构有深入研究');

-- 插入测试课程数据
INSERT IGNORE INTO `course` (`title`, `description`, `cover_image`, `introduction`, `requirements`, `target_audience`, `price`, `discount_price`, `category_id`, `teacher_id`, `lesson_count`, `student_count`, `rating`) VALUES
('Java基础教程', '从零开始学习Java编程语言', 'https://images.unsplash.com/photo-1633356122102-3fe601e05bd2?w=400', '本课程适合零基础学员，从Java基础语法开始，循序渐进地学习面向对象编程、集合框架、多线程等核心知识。', '无编程基础,有学习热情', '初学者,转行人员', 99.00, 49.00, 1, 1, 50, 1234, 4.8),
('Vue 3从入门到实战', '掌握现代前端框架Vue 3的完整开发流程', 'https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=400', '深入学习Vue 3 Composition API、响应式原理、组件通信等核心技术，通过实战项目巩固所学知识。', 'HTML/CSS基础,JavaScript基础', '前端开发者,想学习Vue的同学', 129.00, 69.00, 2, 2, 40, 856, 4.9),
('Spring Boot企业级开发', '构建现代化的企业级Java应用', 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=400', '全面掌握Spring Boot核心特性，包括自动配置、数据访问、安全控制、缓存机制等企业级开发技能。', 'Java基础,Spring框架基础', 'Java开发者,架构师', 199.00, 99.00, 3, 1, 60, 567, 4.7);

-- 插入章节数据
INSERT IGNORE INTO `chapter` (`course_id`, `title`, `chapter_number`, `description`) VALUES
(1, 'Java基础语法', 1, '学习Java的基本语法和编程概念'),
(1, '面向对象编程', 2, '深入理解面向对象的核心思想'),
(1, '集合框架', 3, '掌握Java集合类的使用'),
(2, 'Vue基础', 1, 'Vue 3基础概念和语法'),
(2, '组件化开发', 2, '组件的设计和复用'),
(2, '状态管理', 3, 'Pinia状态管理详解'),
(3, 'Spring Boot入门', 1, 'Spring Boot基础配置和使用'),
(3, '数据访问', 2, 'MyBatis/JPA数据操作'),
(3, '安全控制', 3, 'Spring Security权限管理');

-- 插入课时数据
INSERT IGNORE INTO `lesson` (`chapter_id`, `course_id`, `title`, `description`, `video_url`, `duration`, `lesson_number`, `is_free`) VALUES
(1, 1, 'Java环境搭建', '安装JDK和配置开发环境', 'https://example.com/video1.mp4', 1800, 1, 1),
(1, 1, '第一个Java程序', '编写和运行Hello World程序', 'https://example.com/video2.mp4', 1500, 2, 1),
(1, 1, '变量和数据类型', '学习Java的基本数据类型', 'https://example.com/video3.mp4', 2100, 3, 0),
(2, 1, '类和对象', '理解面向对象的基本概念', 'https://example.com/video4.mp4', 2400, 1, 0),
(2, 1, '继承和多态', '掌握继承和多态机制', 'https://example.com/video5.mp4', 2700, 2, 0),
(4, 2, 'Vue 3简介', 'Vue 3的新特性和优势', 'https://example.com/video6.mp4', 1600, 1, 1),
(4, 2, 'Composition API', '学习组合式API的使用', 'https://example.com/video7.mp4', 2200, 2, 0),
(7, 3, 'Spring Boot起步', '创建第一个Spring Boot项目', 'https://example.com/video8.mp4', 2000, 1, 1),
(7, 3, '自动配置原理', '理解Spring Boot自动配置机制', 'https://example.com/video9.mp4', 2500, 2, 0);

-- 插入默认管理员用户 (密码: admin123)
-- 注意：这里的密码哈希是通过系统PasswordUtil生成的标准BCrypt哈希
INSERT IGNORE INTO `user` (`username`, `password`, `email`, `nickname`, `role`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb0bta/OauRxaOKSr.QhqyD2R5FKvMQjmHoLkm5Sy', 'admin@example.com', '管理员', 'ADMIN', 1);

-- 插入测试普通用户 (密码: user123)
-- 注意：这里的密码哈希是通过系统PasswordUtil生成的标准BCrypt哈希
INSERT IGNORE INTO `user` (`username`, `password`, `email`, `nickname`, `role`, `status`) VALUES
('testuser', '$2a$10$r3.l0wUOsEWAmfnJdqHb2e.nds75HiRQDQiM7VcxcrVQMvDyB9gsO', 'user@example.com', '测试用户', 'USER', 1);

-- 创建学习记录表
CREATE TABLE IF NOT EXISTS `study_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `lesson_id` bigint NOT NULL COMMENT '课时ID',
  `duration` int DEFAULT '0' COMMENT '学习时长（分钟）',
  `progress` int DEFAULT '0' COMMENT '学习进度百分比',
  `study_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '学习时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_study_time` (`study_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习记录表';

-- 插入示例学习记录
INSERT IGNORE INTO `study_record` (`user_id`, `course_id`, `lesson_id`, `duration`, `progress`, `study_time`) VALUES
(2, 1, 1, 45, 100, '2024-01-15 14:30:00'),
(2, 1, 2, 38, 100, '2024-01-15 10:15:00'),
(2, 2, 3, 25, 75, '2024-01-14 16:20:00'),
(2, 2, 4, 42, 30, '2024-01-14 14:00:00'),
(2, 3, 5, 55, 100, '2024-01-13 19:30:00'),
(2, 4, 6, 35, 45, '2024-01-13 15:45:00');

COMMIT;