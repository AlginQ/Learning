-- 教师申请表
CREATE TABLE `teacher_apply` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `real_name` varchar(50) NOT NULL COMMENT '真实姓名',
  `major` varchar(100) NOT NULL COMMENT '专业',
  `qualification` text NOT NULL COMMENT '资质说明',
  `status` tinyint DEFAULT '0' COMMENT '状态 0-待审核 1-通过 2-拒绝',
  `reviewer_id` bigint DEFAULT NULL COMMENT '审核人ID',
  `review_time` datetime DEFAULT NULL COMMENT '审核时间',
  `review_remark` text COMMENT '审核备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师申请表';

-- 为user表添加TEACHER角色的索引
CREATE INDEX idx_role ON user(role);

-- 插入测试数据
INSERT INTO `user` (`username`, `password`, `email`, `nickname`, `role`) VALUES
('teacher1', '$2a$10$rOzJb3.XXXXXXX', 'teacher1@example.com', '教师1', 'TEACHER');

-- 插入测试教师申请数据
INSERT INTO `teacher_apply` (`user_id`, `real_name`, `major`, `qualification`, `status`) VALUES
(1, '张三', '计算机科学与技术', '拥有5年教学经验，精通前端开发技术', 1);