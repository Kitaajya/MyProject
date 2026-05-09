-- 创建数据库
CREATE DATABASE IF NOT EXISTS restaurant DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE restaurant;

-- ======================
-- 1. 用户系统实体
-- ======================
CREATE TABLE if not exists user_system (
                             user_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
                             username VARCHAR(50) NOT NULL COMMENT '用户名',
                             user_type ENUM('新用户','老用户','VIP','高级VIP','SVIP') NOT NULL COMMENT '用户类型'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户系统',auto_increment=20250001;

-- ======================
-- 2. 饭店实体
-- ======================
CREATE TABLE if not exists restaurant (
                            restaurant_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '饭店ID',
                            restaurant_name VARCHAR(100) NOT NULL COMMENT '饭店名称',
                            food_material VARCHAR(200) COMMENT '食材',
                            cost DECIMAL(12,2) DEFAULT 0.00 COMMENT '成本',
                            staff_salary DECIMAL(12,2) DEFAULT 0.00 COMMENT '员工工资',
                            upper_class_num INT DEFAULT 0 COMMENT '上层阶级数量',
                            staff_num INT DEFAULT 0 COMMENT '员工数量',
                            profit DECIMAL(15,2) DEFAULT 0.00 COMMENT '获利'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='饭店';

-- ======================
-- 3. 供应关系（ER图中间菱形）
-- ======================
CREATE TABLE if not exists supply_relation (
                                 id INT PRIMARY KEY AUTO_INCREMENT COMMENT '关系ID',
                                 user_id INT NOT NULL COMMENT '用户ID',
                                 restaurant_id INT NOT NULL COMMENT '饭店ID',

    -- 外键关联
                                 FOREIGN KEY (user_id) REFERENCES user_system(user_id) ON DELETE CASCADE,
                                 FOREIGN KEY (restaurant_id) REFERENCES restaurant(restaurant_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应关系';

INSERT INTO user_system(username, user_type) VALUES (

            '林安',
                    '新用户'
                               );