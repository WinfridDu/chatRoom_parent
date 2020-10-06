/*
 Navicat MySQL Data Transfer

 Source Server         : ubuntu
 Source Server Type    : MySQL
 Source Server Version : 50731
 Source Host           : 192.168.159.130:3306
 Source Schema         : chat_room

 Target Server Type    : MySQL
 Target Server Version : 50731
 File Encoding         : 65001

 Date: 06/10/2020 17:01:02
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(20) NOT NULL,
  `password` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `nickname` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `head` tinyint(1) NULL DEFAULT NULL,
  `sex` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (123, '123456', '张三', 0, 'M');
INSERT INTO `user` VALUES (456, '123456', '李四', 3, 'F');
INSERT INTO `user` VALUES (509757, '123456', '王五', 3, 'F');
INSERT INTO `user` VALUES (717217, '123456', '王五', 1, 'F');

SET FOREIGN_KEY_CHECKS = 1;
