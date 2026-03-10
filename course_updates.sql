-- 1. 为course表添加rating字段
USE learning_system;
ALTER TABLE `course` ADD COLUMN `rating` DECIMAL(3,1) DEFAULT 0.0 COMMENT '评分';

-- 2. 更新所有课程的封面图片URL为真实图片
UPDATE `course` SET `cover_image` = 'https://picsum.photos/400/225?random=1' WHERE id = 1;
UPDATE `course` SET `cover_image` = 'https://picsum.photos/400/225?random=2' WHERE id = 2;
UPDATE `course` SET `cover_image` = 'https://picsum.photos/400/225?random=3' WHERE id = 3;
UPDATE `course` SET `cover_image` = 'https://picsum.photos/400/225?random=4' WHERE id = 4;
UPDATE `course` SET `cover_image` = 'https://picsum.photos/400/225?random=5' WHERE id = 5;
UPDATE `course` SET `cover_image` = 'https://picsum.photos/400/225?random=6' WHERE id = 6;
UPDATE `course` SET `cover_image` = 'https://picsum.photos/400/225?random=7' WHERE id = 7;
UPDATE `course` SET `cover_image` = 'https://picsum.photos/400/225?random=8' WHERE id = 8;
UPDATE `course` SET `cover_image` = 'https://picsum.photos/400/225?random=9' WHERE id = 9;
UPDATE `course` SET `cover_image` = 'https://picsum.photos/400/225?random=10' WHERE id = 10;
UPDATE `course` SET `cover_image` = 'https://picsum.photos/400/225?random=11' WHERE id = 11;
UPDATE `course` SET `cover_image` = 'https://picsum.photos/400/225?random=12' WHERE id = 12;
UPDATE `course` SET `cover_image` = 'https://picsum.photos/400/225?random=13' WHERE id = 13;
UPDATE `course` SET `cover_image` = 'https://picsum.photos/400/225?random=14' WHERE id = 14;
UPDATE `course` SET `cover_image` = 'https://picsum.photos/400/225?random=15' WHERE id = 15;
UPDATE `course` SET `cover_image` = 'https://picsum.photos/400/225?random=16' WHERE id = 16;
UPDATE `course` SET `cover_image` = 'https://picsum.photos/400/225?random=17' WHERE id = 17;
UPDATE `course` SET `cover_image` = 'https://picsum.photos/400/225?random=18' WHERE id = 18;
UPDATE `course` SET `cover_image` = 'https://picsum.photos/400/225?random=19' WHERE id = 19;
UPDATE `course` SET `cover_image` = 'https://picsum.photos/400/225?random=20' WHERE id = 20;
UPDATE `course` SET `cover_image` = 'https://picsum.photos/400/225?random=21' WHERE id = 21;
UPDATE `course` SET `cover_image` = 'https://picsum.photos/400/225?random=22' WHERE id = 22;
UPDATE `course` SET `cover_image` = 'https://picsum.photos/400/225?random=23' WHERE id = 23;

-- 3. 为所有课程添加评分值
UPDATE `course` SET `rating` = 4.8 WHERE id = 1;
UPDATE `course` SET `rating` = 4.7 WHERE id = 2;
UPDATE `course` SET `rating` = 4.6 WHERE id = 3;
UPDATE `course` SET `rating` = 4.7 WHERE id = 4;
UPDATE `course` SET `rating` = 4.8 WHERE id = 5;
UPDATE `course` SET `rating` = 4.6 WHERE id = 6;
UPDATE `course` SET `rating` = 4.7 WHERE id = 7;
UPDATE `course` SET `rating` = 4.8 WHERE id = 8;
UPDATE `course` SET `rating` = 4.8 WHERE id = 9;
UPDATE `course` SET `rating` = 4.7 WHERE id = 10;
UPDATE `course` SET `rating` = 4.6 WHERE id = 11;
UPDATE `course` SET `rating` = 4.7 WHERE id = 12;
UPDATE `course` SET `rating` = 4.8 WHERE id = 13;
UPDATE `course` SET `rating` = 4.7 WHERE id = 14;
UPDATE `course` SET `rating` = 4.6 WHERE id = 15;
UPDATE `course` SET `rating` = 4.7 WHERE id = 16;
UPDATE `course` SET `rating` = 4.6 WHERE id = 17;
UPDATE `course` SET `rating` = 4.8 WHERE id = 18;
UPDATE `course` SET `rating` = 4.9 WHERE id = 19;
UPDATE `course` SET `rating` = 4.7 WHERE id = 20;
UPDATE `course` SET `rating` = 4.6 WHERE id = 21;
UPDATE `course` SET `rating` = 4.8 WHERE id = 22;
UPDATE `course` SET `rating` = 4.7 WHERE id = 23;

-- 4. 验证更新结果
SELECT id, title, cover_image, rating FROM course LIMIT 10;