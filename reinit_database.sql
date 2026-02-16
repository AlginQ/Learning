-- 重新初始化数据库脚本
-- 包含正确的BCrypt密码哈希值

SET FOREIGN_KEY_CHECKS=0;

-- 删除现有数据
DELETE FROM study_progress;
DELETE FROM lesson;
DELETE FROM chapter;
DELETE FROM course;
DELETE FROM category;
DELETE FROM user;

-- 重新插入基础数据
INSERT INTO `category` (`id`, `name`, `icon`, `sort_order`) VALUES
 (1, '前端开发', 'https://example.com/frontend.svg', 1),
 (2, '后端开发', 'https://example.com/backend.svg', 2),
 (3, '移动开发', 'https://example.com/mobile.svg', 3),
(4, '数据库', 'https://example.com/database.svg', 4);

INSERT INTO `course` (`id`, `category_id`, `teacher_id`, `title`, `introduction`, `cover_image`, `price`, `discount_price`, `lesson_count`, `student_count`)
VALUES (1, 1, 1, 'Vue 3从入门到实战', '全面学习Vue 3最新特性，掌握现代前端开发技能', 'https://example.com/vue3.jpg', 0, 0, 20, 0),
       (2, 1, 2, 'React Hooks完全指南', '深入学习React Hooks，提升函数式组件开发能力', 'https://example.com/react.jpg', 0, 0, 15, 0),
       (3, 2, 3, 'Spring Boot企业级开发', '掌握Spring Boot核心技能，构建企业级应用', 'https://example.com/springboot.jpg', 0, 0, 25, 0);

INSERT INTO `chapter` (`id`, `course_id`,`title`,`chapter_number`,`description`,`create_time`,`update_time`)
VALUES
    (1, 1, 'Vue 3基础', 1, 'Vue 3基础章节', NOW(), NOW()),
    (2, 1, '组件开发', 2, 'Vue 3组件开发章节', NOW(), NOW()),
    (3, 1, '状态管理', 3, 'Vue 3状态管理章节', NOW(), NOW()),
    (4, 2, 'Hooks基础', 1, 'React Hooks基础章节', NOW(), NOW()),
    (5, 2, '自定义Hooks', 2, 'React 自定义Hooks章节', NOW(), NOW()),
    (6, 3, 'Spring Boot入门', 1, 'Spring Boot入门章节', NOW(), NOW()),
    (7, 3, '核心特性', 2, 'Spring Boot核心特性章节', NOW(), NOW());

INSERT INTO `chapter` (`id`,`course_id`,`title`,`chapter_number`,`description`,`create_time`,`update_time`)
VALUES
    (1, 1, 'Vue 3基础', 1, 'Vue 3基础章节', NOW(), NOW()),
    (2, 1, '组件开发', 2, 'Vue 3组件开发章节', NOW(), NOW()),
    (3, 1, '状态管理', 3, 'Vue 3状态管理章节', NOW(), NOW()),
    (4, 2, 'Hooks基础', 1, 'React Hooks基础章节', NOW(), NOW()),
    (5, 2, '自定义Hooks', 2, 'React 自定义Hooks章节', NOW(), NOW()),
    (6, 3, 'Spring Boot入门', 1, 'Spring Boot入门章节', NOW(), NOW()),
    (7, 3, '核心特性', 2, 'Spring Boot核心特性章节', NOW(), NOW());
-- 插入默认管理员用户 (密码: admin123)
-- 使用标准BCrypt加密，成本因子为10
INSERT IGNORE INTO `user` (`username`, `password`, `email`, `nickname`, `role`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb0bta/OauRxaOKSr.QhqyD2R5FKvMQjmHoLkm5Sy', 'admin@example.com', '管理员', 'ADMIN', 1);

-- 插入测试普通用户 (密码: user123)  
-- 使用标准BCrypt加密，成本因子为10
INSERT IGNORE INTO `user` (`username`, `password`, `email`, `nickname`, `role`, `status`) VALUES
('testuser', '$2a$10$r3.l0wUOsEWAmfnJdqHb2e.nds75HiRQDQiM7VcxcrVQMvDyB9gsO', 'user@example.com', '测试用户', 'USER', 1);

COMMIT;

-- 开启外键约束检查
SET FOREIGN_KEY_CHECKS=1;

-- 验证管理员账户
SELECT username,
       CASE
           WHEN PASSWORD = '$2a$10$N.zmdr9k7uOCQb0bta/OauRxaOKSr.QhqyD2R5FKvMQjmHoLkm5Sy' THEN '哈希匹配'
           ELSE '哈希不匹配'
           END AS password_status
FROM USER WHERE username = 'admin';

-- 验证测试用户账户
SELECT username,
       CASE
           WHEN PASSWORD = '$2a$10$r3.l0wUOsEWAmfnJdqHb2e.nds75HiRQDQiM7VcxcrVQMvDyB9gsO' THEN '哈希匹配'
           ELSE '哈希不匹配'
           END AS password_status
FROM USER WHERE username = 'testuser';