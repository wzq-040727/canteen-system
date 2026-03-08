-- 创建数据库
CREATE DATABASE IF NOT EXISTS canteen_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE canteen_db;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(50) COMMENT '真实姓名',
    `student_id` VARCHAR(20) COMMENT '学号',
    `phone` VARCHAR(20) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `role` TINYINT DEFAULT 0 COMMENT '角色：0-学生，1-食堂管理员，2-系统管理员',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_student_id` (`student_id`),
    UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 食堂表
CREATE TABLE IF NOT EXISTS `canteen` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '食堂ID',
    `name` VARCHAR(100) NOT NULL COMMENT '食堂名称',
    `location` VARCHAR(255) COMMENT '位置',
    `description` TEXT COMMENT '描述',
    `image` VARCHAR(255) COMMENT '图片URL',
    `opening_hours` VARCHAR(100) COMMENT '营业时间',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-关闭，1-营业',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='食堂表';

-- 食堂窗口表
CREATE TABLE IF NOT EXISTS `window` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '窗口ID',
    `canteen_id` BIGINT NOT NULL COMMENT '食堂ID',
    `name` VARCHAR(100) NOT NULL COMMENT '窗口名称',
    `description` TEXT COMMENT '描述',
    `cuisine_type` VARCHAR(50) COMMENT '菜系类型',
    `image` VARCHAR(255) COMMENT '图片URL',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-关闭，1-营业',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    KEY `idx_canteen_id` (`canteen_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='食堂窗口表';

-- 菜品表
CREATE TABLE IF NOT EXISTS `dish` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '菜品ID',
    `window_id` BIGINT NOT NULL COMMENT '窗口ID',
    `canteen_id` BIGINT NOT NULL COMMENT '食堂ID',
    `name` VARCHAR(100) NOT NULL COMMENT '菜品名称',
    `description` TEXT COMMENT '描述',
    `price` DECIMAL(10, 2) NOT NULL COMMENT '价格',
    `image` VARCHAR(255) COMMENT '图片URL',
    `category` VARCHAR(50) COMMENT '分类',
    `taste` VARCHAR(50) COMMENT '口味标签',
    `avg_rating` DECIMAL(3, 2) DEFAULT 0 COMMENT '平均评分',
    `rating_count` INT DEFAULT 0 COMMENT '评分人数',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-下架，1-上架',
    `is_recommend` TINYINT DEFAULT 0 COMMENT '是否推荐：0-否，1-是',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    KEY `idx_window_id` (`window_id`),
    KEY `idx_canteen_id` (`canteen_id`),
    KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品表';

-- 评论评分表
CREATE TABLE IF NOT EXISTS `review` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `dish_id` BIGINT NOT NULL COMMENT '菜品ID',
    `rating` TINYINT NOT NULL COMMENT '评分：1-5',
    `content` TEXT COMMENT '评论内容',
    `images` TEXT COMMENT '图片URL列表，JSON格式',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-隐藏，1-显示',
    `like_count` INT DEFAULT 0 COMMENT '点赞数',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    KEY `idx_user_id` (`user_id`),
    KEY `idx_dish_id` (`dish_id`),
    UNIQUE KEY `uk_user_dish` (`user_id`, `dish_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论评分表';

-- 用户行为记录表（用于推荐算法）
CREATE TABLE IF NOT EXISTS `user_behavior` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '行为ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `dish_id` BIGINT NOT NULL COMMENT '菜品ID',
    `behavior_type` TINYINT NOT NULL COMMENT '行为类型：1-浏览，2-收藏，3-评分，4-评论',
    `score` DECIMAL(3, 2) DEFAULT 0 COMMENT '行为分数',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY `idx_user_id` (`user_id`),
    KEY `idx_dish_id` (`dish_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为记录表';

-- 收藏表
CREATE TABLE IF NOT EXISTS `favorite` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `dish_id` BIGINT NOT NULL COMMENT '菜品ID',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    KEY `idx_user_id` (`user_id`),
    KEY `idx_dish_id` (`dish_id`),
    UNIQUE KEY `uk_user_dish` (`user_id`, `dish_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 推荐记录表
CREATE TABLE IF NOT EXISTS `recommendation` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '推荐ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `dish_id` BIGINT NOT NULL COMMENT '菜品ID',
    `score` DECIMAL(5, 4) COMMENT '推荐分数',
    `reason` VARCHAR(255) COMMENT '推荐理由',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推荐记录表';

-- 评论点赞表
CREATE TABLE IF NOT EXISTS `review_like` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '点赞ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `review_id` BIGINT NOT NULL COMMENT '评论ID',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_user_review` (`user_id`, `review_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论点赞表';

-- 插入初始管理员数据
INSERT INTO `user` (`username`, `password`, `real_name`, `role`, `status`) VALUES
('admin', '$2a$10$EqKcp1WFKVQISheBbxEY/ON8DC9tfZxZxQxEjYp2H4xHxP8xX8Hq', '系统管理员', 2, 1);

-- 插入示例食堂数据
INSERT INTO `canteen` (`name`, `location`, `description`, `opening_hours`, `status`) VALUES
('第一食堂', '校园东区', '提供早中晚餐，菜品种类丰富', '07:00-21:00', 1),
('第二食堂', '校园西区', '特色小吃为主，价格实惠', '07:00-21:00', 1),
('第三食堂', '校园南区', '清真食堂，特色菜品', '07:00-21:00', 1);

-- 插入示例窗口数据
INSERT INTO `window` (`canteen_id`, `name`, `cuisine_type`, `status`) VALUES
(1, '川菜窗口', '川菜', 1),
(1, '粤菜窗口', '粤菜', 1),
(1, '面食窗口', '面食', 1),
(2, '快餐窗口', '快餐', 1),
(2, '小吃窗口', '小吃', 1),
(3, '清真窗口', '清真', 1);

-- 插入示例菜品数据
INSERT INTO `dish` (`window_id`, `canteen_id`, `name`, `price`, `category`, `taste`, `status`) VALUES
(1, 1, '宫保鸡丁', 15.00, '热菜', '微辣', 1),
(1, 1, '麻婆豆腐', 12.00, '热菜', '中辣', 1),
(1, 1, '水煮肉片', 18.00, '热菜', '麻辣', 1),
(2, 1, '白切鸡', 16.00, '热菜', '清淡', 1),
(2, 1, '蒜蓉蒸排骨', 18.00, '热菜', '咸鲜', 1),
(3, 1, '牛肉面', 14.00, '面食', '咸鲜', 1),
(3, 1, '炸酱面', 12.00, '面食', '咸鲜', 1),
(4, 2, '红烧肉套餐', 16.00, '套餐', '咸鲜', 1),
(4, 2, '鸡腿套餐', 15.00, '套餐', '咸鲜', 1),
(5, 2, '煎饼果子', 6.00, '小吃', '咸鲜', 1),
(5, 2, '手抓饼', 8.00, '小吃', '咸鲜', 1),
(6, 3, '羊肉泡馍', 18.00, '特色', '咸鲜', 1),
(6, 3, '牛肉拉面', 15.00, '面食', '咸鲜', 1);

-- 插入测试用户
INSERT INTO `user` (`username`, `password`, `real_name`, `student_id`, `phone`, `role`, `status`) VALUES
('student1', '$2a$10$EqKcp1WFKVQISheBbxEY/ON8DC9tfZxZxQxEjYp2H4xHxP8xX8Hq', '张三', '2021001', '13800138001', 0, 1),
('student2', '$2a$10$EqKcp1WFKVQISheBbxEY/ON8DC9tfZxZxQxEjYp2H4xHxP8xX8Hq', '李四', '2021002', '13800138002', 0, 1),
('canteen_admin', '$2a$10$EqKcp1WFKVQISheBbxEY/ON8DC9tfZxZxQxEjYp2H4xHxP8xX8Hq', '食堂管理员', NULL, '13800138003', 1, 1);
