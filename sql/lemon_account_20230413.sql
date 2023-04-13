/*
 Navicat Premium Data Transfer

 Source Server         : [01] 127.0.0.1 localhost 8.0 root
 Source Server Type    : MySQL
 Source Server Version : 80027
 Source Host           : localhost:3306
 Source Schema         : lemon-account-master

 Target Server Type    : MySQL
 Target Server Version : 80027
 File Encoding         : 65001

 Date: 13/04/2023 17:01:05
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for lam_account
-- ----------------------------
DROP TABLE IF EXISTS `lam_account`;
CREATE TABLE `lam_account`  (
  `account_id` bigint NOT NULL AUTO_INCREMENT COMMENT '账号id',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父账号id',
  `ancestors` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '祖级列表',
  `account_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '账户名称',
  `account_password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '账户密码',
  `account_key_iv` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '账户key值偏移量iv',
  `account_info` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '账户说明',
  `account_domain` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '账户域名',
  `del_flag` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`account_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 101 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '柠檬账号大师 - 账号表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for lam_aes_key
-- ----------------------------
DROP TABLE IF EXISTS `lam_aes_key`;
CREATE TABLE `lam_aes_key`  (
  `key_id` bigint NOT NULL AUTO_INCREMENT COMMENT '密钥ID',
  `aes_key` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'AES密钥',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`key_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 101 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '柠檬账号大师 - AES密钥表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of lam_aes_key
-- ----------------------------
INSERT INTO `lam_aes_key` VALUES (1, 'ApjfT/KhcswJvxeWbZ20sZwqekz6yYLB5Okrm/zEgBlj+4HK9kZYML+aoEDJpgmh', '2023-04-07 09:01:49');
INSERT INTO `lam_aes_key` VALUES (2, 'ApjfT/KhcswJvxeWbZ20sZwqekz6yYLB5Okrm/zEgBlj+4HK9kZYML+aoEDJpgmh', '2023-04-07 09:01:49');

-- ----------------------------
-- Table structure for lam_user_account
-- ----------------------------
DROP TABLE IF EXISTS `lam_user_account`;
CREATE TABLE `lam_user_account`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `account_id` bigint NOT NULL COMMENT '柠檬账号ID',
  PRIMARY KEY (`user_id`, `account_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '柠檬账号大师 - 用户和账号关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for lam_user_aes_key
-- ----------------------------
DROP TABLE IF EXISTS `lam_user_aes_key`;
CREATE TABLE `lam_user_aes_key`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `key_id` bigint NOT NULL COMMENT '密钥ID',
  PRIMARY KEY (`user_id`, `key_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '柠檬账号大师 - 用户和AES密钥关联表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
