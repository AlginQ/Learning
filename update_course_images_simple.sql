-- 更新course表的封面图片URL为真实图片
USE learning_system;

-- 更新前3个课程的封面图片
UPDATE `course` SET `cover_image` = 'https://picsum.photos/400/225?random=1' WHERE id = 1;
UPDATE `course` SET `cover_image` = 'https://picsum.photos/400/225?random=2' WHERE id = 2;
UPDATE `course` SET `cover_image` = 'https://picsum.photos/400/225?random=3' WHERE id = 3;

-- 验证更新结果
SELECT id, title, cover_image FROM course LIMIT 5;