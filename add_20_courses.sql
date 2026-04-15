-- 增加20条course表数据（符合现有表结构）
NSERT INTO `course` (`id`, `category_id`, `teacher_id`, `title`, `introduction`, `cover_image`, `price`, `discount_price`, `lesson_count`, `student_count`, `rating`)
VALUES
-- 前端开发课程
(4, 1, 1, 'JavaScript高级编程', '深入学习JavaScript高级特性，包括闭包、原型链、异步编程等', 'https://example.com/js-advanced.jpg', 129, 69, 22, 4567),
(5, 1, 2, 'TypeScript入门到精通', '学习TypeScript类型系统和最佳实践，提升代码质量', 'https://example.com/typescript.jpg', 149, 79, 20, 2345),
(6, 1, 1, 'HTML5与CSS3实战', '学习HTML5与CSS3高级特性，构建现代化网页', 'https://example.com/html5-css3.jpg', 99, 49, 15, 4567),
(7, 1, 2, '前端性能优化', '学习前端性能优化技巧，提升网站加载速度', 'https://example.com/frontend-optimization.jpg', 179, 89, 18, 1234),
(8, 1, 1, 'Vue 3组件库开发', '学习如何开发自己的Vue 3组件库', 'https://example.com/vue3-components.jpg', 199, 99, 25, 1890),

-- 后端开发课程
(9, 2, 1, 'Spring Cloud微服务架构', '学习Spring Cloud微服务架构设计与实现', 'https://example.com/spring-cloud.jpg', 299, 149, 35, 2345),
(10, 2, 2, 'Node.js后端开发', '学习Node.js后端开发技术，构建高性能服务', 'https://example.com/nodejs-backend.jpg', 169, 89, 24, 2789),
(11, 2, 1, 'Python后端开发', '学习Python后端开发框架，如Django和Flask', 'https://example.com/python-backend.jpg', 159, 79, 22, 3456),
(12, 2, 2, 'Go语言实战', '学习Go语言的核心特性和实战应用', 'https://example.com/go-language.jpg', 189, 99, 20, 1987),
(13, 2, 1, '微服务架构设计', '学习微服务架构的设计原则和最佳实践', 'https://example.com/microservices.jpg', 249, 129, 28, 1654),

-- 移动开发课程
(14, 3, 1, 'Flutter跨平台开发', '学习Flutter跨平台应用开发，一次编写多端运行', 'https://example.com/flutter.jpg', 249, 129, 30, 2789),
(15, 3, 2, 'React Native开发', '学习React Native移动应用开发', 'https://example.com/react-native.jpg', 229, 119, 28, 2345),
(16, 3, 1, 'iOS开发基础', '学习iOS应用开发的基础知识和技能', 'https://example.com/ios-dev.jpg', 199, 99, 25, 1890),
(17, 3, 2, 'Android开发入门', '学习Android应用开发的入门知识', 'https://example.com/android-dev.jpg', 189, 99, 22, 2134),
(18, 3, 1, '移动应用UI设计', '学习移动应用的UI设计原则和实践', 'https://example.com/mobile-ui.jpg', 159, 79, 18, 1654),

-- 数据库课程
(19, 4, 2, 'MySQL数据库优化', '学习MySQL数据库性能优化技巧', 'https://example.com/mysql-optimization.jpg', 149, 79, 18, 1987),
(20, 4, 1, 'PostgreSQL高级特性', '学习PostgreSQL数据库的高级特性和应用', 'https://example.com/postgresql.jpg', 169, 89, 20, 1234),
(21, 4, 2, 'MongoDB实战', '学习MongoDB非关系型数据库的应用', 'https://example.com/mongodb.jpg', 159, 79, 19, 1567),
(22, 4, 1, '数据库设计与建模', '学习数据库设计的原则和建模技巧', 'https://example.com/db-design.jpg', 179, 89, 22, 1345),
(23, 4, 2, 'Redis缓存技术', '学习Redis缓存技术的应用和最佳实践', 'https://example.com/redis.jpg', 149, 79, 16, 1789);

-- 插入教师数据（如果不存在）
INSERT IGNORE INTO `teacher` (`id`, `name`, `avatar`, `introduction`)
VALUES
(1, '张老师', 'https://example.com/teacher1.jpg', '资深前端开发工程师，拥有10年开发经验'),
(2, '李老师', 'https://example.com/teacher2.jpg', 'Java专家，Spring框架认证讲师'),
(3, '王老师', 'https://example.com/teacher3.jpg', '数据科学专家，数据库优化大师');