CREATE DATABASE IF NOT EXISTS learning_game
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE learning_game;

CREATE TABLE IF NOT EXISTS users (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL,
  password VARCHAR(128) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS categories (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  name VARCHAR(64) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_categories_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS questions (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  question_text VARCHAR(500) NOT NULL,
  option_a VARCHAR(255) NOT NULL,
  option_b VARCHAR(255) NOT NULL,
  option_c VARCHAR(255) NOT NULL,
  option_d VARCHAR(255) NOT NULL,
  correct_option ENUM('A', 'B', 'C', 'D') NOT NULL,
  category VARCHAR(64) NULL,
  category_id BIGINT UNSIGNED NULL,
  difficulty TINYINT UNSIGNED NOT NULL DEFAULT 1,
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  INDEX idx_questions_active (is_active),
  INDEX idx_questions_category (category),
  INDEX idx_questions_category_id (category_id),
  INDEX idx_questions_difficulty (difficulty),
  CONSTRAINT fk_questions_category_id FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO categories (name) VALUES ('常识'), ('技术');

INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option, category, difficulty)
VALUES
('太阳系最大的行星是？', '地球', '火星', '木星', '土星', 'C', '常识', 1),
('HTTP 默认端口是？', '80', '443', '8080', '3306', 'A', '技术', 1),
('Vue 主要用于？', '前端界面开发', '关系型数据库', '容器编排', '操作系统开发', 'A', '技术', 1),
('水的化学式是？', 'CO2', 'H2O', 'O2', 'NaCl', 'B', '常识', 1),
('Git 主要用于？', '图像处理', '版本控制', '邮件发送', '数据库备份', 'B', '技术', 1);

ALTER TABLE users ADD COLUMN nick_name VARCHAR(50) DEFAULT NULL;