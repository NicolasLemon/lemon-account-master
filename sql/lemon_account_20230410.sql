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

 Date: 10/04/2023 16:47:03
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
) ENGINE = InnoDB AUTO_INCREMENT = 201 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '柠檬账号大师账号表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of lam_account
-- ----------------------------
INSERT INTO `lam_account` VALUES (1, 0, '', 'google@lemon', '', 'abcdef', '谷歌账户', NULL, '0', '', NULL, '', NULL);
INSERT INTO `lam_account` VALUES (2, 1, '0', 'aaa', 'bbb', 'ccc', 'aaasdas', NULL, '0', '', NULL, '', NULL);
INSERT INTO `lam_account` VALUES (3, 1, '0', '阿斯顿', '阿斯顿', 'sadas', 'asdas', NULL, '0', '', NULL, '', NULL);
INSERT INTO `lam_account` VALUES (4, 2, '0,2', 'sadasd', 'asdsad', 'sadsa', 'sadsa', NULL, '0', '', NULL, '', NULL);
INSERT INTO `lam_account` VALUES (5, 0, '', 'adsa', 'sadasd', 'sada', 'sadsa', NULL, '0', '', NULL, '', NULL);

-- ----------------------------
-- Table structure for lam_user_account
-- ----------------------------
DROP TABLE IF EXISTS `lam_user_account`;
CREATE TABLE `lam_user_account`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `account_id` bigint NOT NULL COMMENT '柠檬账号ID',
  PRIMARY KEY (`user_id`, `account_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户和柠檬账号关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of lam_user_account
-- ----------------------------
INSERT INTO `lam_user_account` VALUES (1, 1);
INSERT INTO `lam_user_account` VALUES (1, 2);
INSERT INTO `lam_user_account` VALUES (1, 3);
INSERT INTO `lam_user_account` VALUES (2, 4);
INSERT INTO `lam_user_account` VALUES (2, 5);

SET FOREIGN_KEY_CHECKS = 1;
