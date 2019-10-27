SET sql_mode = '';
CREATE TABLE `grocers`.`user` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated_at` DATETIME NOT NULL DEFAULT '0000-00-00 00:00:00',
  `telephone` VARCHAR(40) NOT NULL DEFAULT '',
  `password` VARCHAR(200) NOT NULL DEFAULT '',
  `nick_name` VARCHAR(40) NOT NULL DEFAULT '',
  `gender` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `telephone_unique_index` USING BTREE (`telephone`));

SET sql_mode = '';
CREATE TABLE `grocers`.`seller` (
  `id` INT(0) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(80) NOT NULL DEFAULT '',
  `created_at` DATETIME(0) NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated_at` DATETIME(0) NOT NULL DEFAULT '0000-00-00 00:00:00',
  `remark_score` DECIMAL(2, 1) NOT NULL DEFAULT 0,
  `disabled_flag` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`));

SET sql_mode = '';
CREATE TABLE `grocers`.`category` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated_at` DATETIME NOT NULL DEFAULT '0000-00-00 00:00:00',
  `name` VARCHAR(20) NOT NULL DEFAULT '',
  `icon_url` VARCHAR(200) NOT NULL DEFAULT '',
  `sort` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_unique_index` USING BTREE (`name`));

SET sql_mode = '';
CREATE TABLE `grocers`.`shop` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated_at` DATETIME NOT NULL DEFAULT '0000-00-00 00:00:00',
  `name` VARCHAR(80) NOT NULL DEFAULT '',
  `remark_score` DECIMAL(2, 1) NOT NULL DEFAULT 0,
  `price_per_man` INT NOT NULL DEFAULT 0,
  `latitude` DECIMAL(10, 6) NOT NULL DEFAULT 0,
  `longitude` DECIMAL(10, 6) NOT NULL DEFAULT 0,
  `category_id` INT NOT NULL DEFAULT 0,
  `tags` VARCHAR(2000) NOT NULL DEFAULT '',
  `start_time` VARCHAR(200) NOT NULL DEFAULT '',
  `end_time` VARCHAR(200) NOT NULL DEFAULT '',
  `address` VARCHAR(200) NOT NULL DEFAULT '',
  `seller_id` INT NOT NULL DEFAULT 0,
  `icon_url` VARCHAR(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`));
