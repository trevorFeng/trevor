/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50087
Source Host           : localhost:3306
Source Database       : trevor

Target Server Type    : MYSQL
Target Server Version : 50087
File Encoding         : 65001

Date: 2019-03-11 00:04:09
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for card_trans
-- ----------------------------
DROP TABLE IF EXISTS `card_trans`;
CREATE TABLE `card_trans` (
  `id` int(11) NOT NULL auto_increment COMMENT '主键',
  `card_num` int(11) NOT NULL COMMENT '交易的房卡数量',
  `turn_out_user_id` int(11) NOT NULL COMMENT '转出玩家id',
  `trans_num` varchar(50) NOT NULL COMMENT '全局唯一的交易号',
  `turn_out_time` bigint(20) NOT NULL COMMENT '转出时间',
  `turn_in_user_id` int(11) NOT NULL COMMENT '转入玩家id',
  `turn_in_time` bigint(20) NOT NULL COMMENT '转入时间',
  `version` int(10) unsigned NOT NULL COMMENT '初始值为0，每次修改版本号加1,最大值为1',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='房卡交易记录';
