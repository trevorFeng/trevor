/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50087
Source Host           : localhost:3306
Source Database       : trevor

Target Server Type    : MYSQL
Target Server Version : 50087
File Encoding         : 65001

Date: 2019-03-11 00:03:47
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for card_consum_record
-- ----------------------------
DROP TABLE IF EXISTS `card_consum_record`;
CREATE TABLE `card_consum_record` (
  `id` int(11) NOT NULL auto_increment COMMENT '主键id',
  `room_record_id` int(11) NOT NULL COMMENT '开房id',
  `room_auth` int(11) NOT NULL COMMENT '开房人的id',
  `consum_num` int(11) NOT NULL COMMENT '消费房卡数量',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='房卡消费记录';
