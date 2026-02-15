# 在线学习系统数据库设计

## 数据库名称
`online_study`

## 表结构设计

### 1. 用户表 (user)
存储用户基本信息和认证信息

```sql
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL UNIQUE COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '加密后的密码',
  `email` varchar(100) NOT NULL UNIQUE COMMENT '邮箱',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `gender` tinyint DEFAULT '0' COMMENT '性别 0-未知 1-男 2-女',
  `birthday` date DEFAULT NULL COMMENT '生日',
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
```

### 2. 课程分类表 (category)
存储课程分类信息

```sql
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) NOT NULL UNIQUE COMMENT '分类名称',
  `icon` varchar(50) DEFAULT NULL COMMENT '分类图标',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程分类表';
```

### 3. 讲师表 (teacher)
存储讲师信息

```sql
CREATE TABLE `teacher` (
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
```

### 4. 课程表 (course)
存储课程基本信息

```sql
CREATE TABLE `course` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `title` varchar(200) NOT NULL COMMENT '课程标题',
  `description` varchar(500) DEFAULT NULL COMMENT '课程简介',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '封面图片URL',
  `introduction` text COMMENT '课程详细介绍',
  `requirements` json DEFAULT NULL COMMENT '学习要求',
  `target_audience` json DEFAULT NULL COMMENT '目标受众',
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
```

### 5. 课程章节表 (chapter)
存储课程章节信息

```sql
CREATE TABLE `chapter` (
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
```

### 6. 课时表 (lesson)
存储具体课时内容

```sql
CREATE TABLE `lesson` (
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
```

### 7. 学习进度表 (study_progress)
记录用户学习进度

```sql
CREATE TABLE `study_progress` (
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
```

### 8. 用户课程收藏表 (user_favorite)
记录用户收藏的课程

```sql
CREATE TABLE `user_favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_course` (`user_id`,`course_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';
```

### 9. 课程评价表 (course_review)
存储课程评价信息

```sql
CREATE TABLE `course_review` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `rating` int NOT NULL COMMENT '评分 1-5星',
  `content` text COMMENT '评价内容',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `status` tinyint DEFAULT '1' COMMENT '状态 0-隐藏 1-显示',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_course` (`user_id`,`course_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_rating` (`rating`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程评价表';
```

## 初始化数据

### 插入默认分类
```sql
INSERT INTO `category` (`name`, `icon`, `sort_order`) VALUES
('编程语言', 'code', 1),
('前端开发', 'desktop', 2),
('后端开发', 'server', 3),
('移动开发', 'mobile', 4),
('数据库', 'database', 5),
('人工智能', 'ai', 6),
('运维技术', 'setting', 7);
```

### 插入默认讲师
```sql
INSERT INTO `teacher` (`name`, `title`, `introduction`) VALUES
('张老师', '高级软件工程师', '拥有10年Java开发经验，专注于后端架构设计'),
('李老师', '前端技术专家', '资深前端工程师，擅长Vue/React框架');
```

### 插入测试用户
```sql
-- 默认管理员用户 (密码: admin123)
INSERT INTO `user` (`username`, `password`, `email`, `nickname`, `role`) VALUES
('admin', '$2a$10$rOzJb3.XXXXXXX', 'admin@example.com', '管理员', 'ADMIN');

-- 测试普通用户 (密码: user123)
INSERT INTO `user` (`username`, `password`, `email`, `nickname`, `role`) VALUES
('testuser', '$2a$10$rOzJb3.XXXXXXX', 'user@example.com', '测试用户', 'USER');
```

## 索引优化建议

1. **用户表**: username, email 字段已建立唯一索引
2. **课程表**: category_id, teacher_id, status 字段建立索引
3. **学习进度表**: user_id + lesson_id 联合唯一索引
4. **收藏表**: user_id + course_id 联合唯一索引

## 注意事项

1. 所有涉及时间的字段均使用 datetime 类型
2. 密码字段需使用 BCrypt 加密存储
3. JSON 字段用于存储数组类型数据（如 requirements, target_audience）
4. 建立适当的外键约束保证数据一致性
5. 定期备份重要数据表