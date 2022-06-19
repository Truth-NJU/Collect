/*
 Navicat Premium Data Transfer

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 80023
 Source Host           : localhost:3306
 Source Schema         : collect

 Target Server Type    : MySQL
 Target Server Version : 80023
 File Encoding         : 65001

 Date: 26/05/2022 16:00:52
*/
create database collect;
use collect;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for attribute
-- ----------------------------
DROP TABLE IF EXISTS `attribute`;
CREATE TABLE `attribute` (
  `id` int NOT NULL AUTO_INCREMENT,
  `userId` int NOT NULL,
  `windows` int DEFAULT '0',
  `linux` int DEFAULT '0',
  `mac` int DEFAULT '0',
  `android` int DEFAULT '0',
  `ios` int DEFAULT '0',
  `harmony` int DEFAULT '0',
  `performance` int DEFAULT '0',
  `functional` int DEFAULT '0',
  `bug` int DEFAULT '0',
  `performanceAbility` double DEFAULT '0',
  `bugAbility` double DEFAULT '0',
  `functionalAbility` double DEFAULT '0',
  `credibility` double DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `user_id` (`userId`),
  CONSTRAINT `user_id` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=229 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for code
-- ----------------------------
DROP TABLE IF EXISTS `code`;
CREATE TABLE `code` (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `reportId` int DEFAULT NULL,
  `userId` int DEFAULT NULL,
  `mark` double DEFAULT NULL,
  `comment` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `reportId` (`reportId`),
  KEY `userId` (`userId`),
  CONSTRAINT `reportId` FOREIGN KEY (`reportId`) REFERENCES `report` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `userId` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for coworkers
-- ----------------------------
DROP TABLE IF EXISTS `coworkers`;
CREATE TABLE `coworkers` (
  `id` int NOT NULL AUTO_INCREMENT,
  `workId` int NOT NULL,
  `coworkerId` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for report
-- ----------------------------
DROP TABLE IF EXISTS `report`;
CREATE TABLE `report` (
  `id` int NOT NULL AUTO_INCREMENT,
  `userId` int NOT NULL,
  `taskId` int NOT NULL,
  `note` varchar(500) DEFAULT NULL,
  `steps` varchar(500) DEFAULT NULL,
  `device` varchar(500) DEFAULT NULL,
  `star` double DEFAULT '0',
  `starNum` int DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `report_taskId_index` (`taskId`),
  KEY `report_userId_index` (`userId`),
  CONSTRAINT `report_task_id_fk` FOREIGN KEY (`taskId`) REFERENCES `task` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `report_user_id_fk` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for report_cluster
-- ----------------------------
DROP TABLE IF EXISTS `report_cluster`;
CREATE TABLE `report_cluster` (
  `taskId` int NOT NULL AUTO_INCREMENT,
  `cluster_set` text,
  PRIMARY KEY (`taskId`),
  CONSTRAINT `report_cluster_task_id_fk` FOREIGN KEY (`taskId`) REFERENCES `task` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3169 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for report_image
-- ----------------------------
DROP TABLE IF EXISTS `report_image`;
CREATE TABLE `report_image` (
  `reportId` int NOT NULL,
  `image` varchar(100) NOT NULL,
  KEY `report_image_reportId_index` (`reportId`),
  CONSTRAINT `report_image_report_id_fk` FOREIGN KEY (`reportId`) REFERENCES `report` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for rule
-- ----------------------------
DROP TABLE IF EXISTS `rule`;
CREATE TABLE `rule` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `hint` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for task
-- ----------------------------
DROP TABLE IF EXISTS `task`;
CREATE TABLE `task` (
  `id` int NOT NULL AUTO_INCREMENT,
  `userId` int NOT NULL,
  `name` varchar(20) NOT NULL,
  `number` int NOT NULL,
  `remain` int NOT NULL,
  `tag` int DEFAULT NULL,
  `date` bigint NOT NULL,
  `aurl` varchar(100) DEFAULT NULL,
  `purl` varchar(100) DEFAULT NULL,
  `introduction` text,
  `level` int DEFAULT NULL,
  `device` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `task_date_index` (`date`),
  KEY `task_name_index` (`name`),
  KEY `task_tag_index` (`tag`),
  KEY `task_userId_index` (`userId`),
  CONSTRAINT `task_user_id_fk` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3169 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(30) NOT NULL,
  `passwd` varchar(255) NOT NULL,
  `salt` varchar(255) DEFAULT NULL,
  `identity` int NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `lastLogin` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_email_uindex` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=471 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for work
-- ----------------------------
DROP TABLE IF EXISTS `work`;
CREATE TABLE `work` (
  `id` int NOT NULL AUTO_INCREMENT,
  `userId` int NOT NULL,
  `taskId` int NOT NULL,
  `finish` int DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `work_taskId_index` (`taskId`),
  KEY `work_userId_index` (`userId`,`taskId`) USING BTREE,
  CONSTRAINT `work_task_id_fk` FOREIGN KEY (`taskId`) REFERENCES `task` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `work_user_id_fk` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7443 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;
