/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50087
Source Host           : localhost:3306
Source Database       : trevor

Target Server Type    : MYSQL
Target Server Version : 50087
File Encoding         : 65001

Date: 2019-03-13 23:38:10
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for card_consum_record
-- ----------------------------
DROP TABLE IF EXISTS `card_consum_record`;
CREATE TABLE `card_consum_record` (
  `id` int(11) unsigned NOT NULL auto_increment COMMENT '主键id',
  `room_record_id` int(11) unsigned NOT NULL COMMENT '开房id',
  `room_auth` int(11) unsigned NOT NULL COMMENT '开房人的id',
  `consum_num` int(11) unsigned NOT NULL COMMENT '消费房卡数量',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='房卡消费记录';

-- ----------------------------
-- Table structure for card_trans
-- ----------------------------
DROP TABLE IF EXISTS `card_trans`;
CREATE TABLE `card_trans` (
  `id` int(11) unsigned NOT NULL auto_increment COMMENT '主键',
  `card_num` int(11) unsigned NOT NULL COMMENT '交易的房卡数量',
  `turn_out_user_name` varchar(20) character set utf8 collate utf8_bin NOT NULL COMMENT '转出时登陆玩家名字',
  `turn_out_user_id` int(11) unsigned NOT NULL COMMENT '转出玩家id',
  `trans_num` varchar(50) NOT NULL COMMENT '全局唯一的交易号',
  `turn_out_time` bigint(20) unsigned NOT NULL COMMENT '转出时间',
  `turn_in_user_name` varchar(20) character set utf8 collate utf8_bin NOT NULL COMMENT '转入时登陆玩家名字',
  `turn_in_user_id` int(11) unsigned NOT NULL COMMENT '转入玩家id',
  `turn_in_time` bigint(20) unsigned NOT NULL COMMENT '转入时间',
  `version` tinyint(4) unsigned NOT NULL COMMENT '初始值为0，每次修改版本号加1,最大值为1',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='房卡交易记录';

-- ----------------------------
-- Table structure for config
-- ----------------------------
DROP TABLE IF EXISTS `config`;
CREATE TABLE `config` (
  `id` int(11) unsigned NOT NULL auto_increment COMMENT '主键',
  `config_name` varchar(30) character set utf8 collate utf8_bin NOT NULL COMMENT '配置名字',
  `config_value` varchar(50) character set utf8 collate utf8_bin NOT NULL COMMENT '配置名字',
  `config_name1` varchar(30) character set utf8 collate utf8_bin default NULL,
  `config_value1` varchar(50) character set utf8 collate utf8_bin default NULL,
  `config_name2` varchar(30) character set utf8 collate utf8_bin default NULL,
  `config_value2` varchar(50) character set utf8 collate utf8_bin default NULL,
  `config_name3` varchar(30) character set utf8 collate utf8_bin default NULL,
  `config_value3` varchar(50) character set utf8 collate utf8_bin default NULL,
  `config_name4` varchar(30) character set utf8 collate utf8_bin default NULL,
  `config_value4` varchar(50) character set utf8 collate utf8_bin default NULL,
  `active` tinyint(4) unsigned NOT NULL COMMENT '1代表可用，0代表不可用',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for friends_manage
-- ----------------------------
DROP TABLE IF EXISTS `friends_manage`;
CREATE TABLE `friends_manage` (
  `id` int(11) unsigned NOT NULL auto_increment COMMENT '主键',
  `user_id` int(11) unsigned NOT NULL COMMENT '玩家id',
  `manage_friend_id` int(11) unsigned NOT NULL COMMENT '关联的好友id',
  `allow_flag` tinyint(4) unsigned NOT NULL COMMENT '1为通过，0为未通过',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for game_situation
-- ----------------------------
DROP TABLE IF EXISTS `game_situation`;
CREATE TABLE `game_situation` (
  `id` int(11) unsigned NOT NULL auto_increment COMMENT '主键',
  `room_record_id` int(11) unsigned NOT NULL COMMENT '开房的id',
  `game_situation` text character set utf8 collate utf8_bin NOT NULL COMMENT '这间房间的对局情况',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for personal_card
-- ----------------------------
DROP TABLE IF EXISTS `personal_card`;
CREATE TABLE `personal_card` (
  `id` int(11) unsigned NOT NULL auto_increment COMMENT '主键',
  `user_id` int(11) unsigned NOT NULL COMMENT '用户id',
  `room_card_num` int(11) unsigned NOT NULL COMMENT '拥有房卡数量',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for personal_confrontation
-- ----------------------------
DROP TABLE IF EXISTS `personal_confrontation`;
CREATE TABLE `personal_confrontation` (
  `id` int(11) unsigned NOT NULL auto_increment COMMENT '主键',
  `user_id` int(11) unsigned NOT NULL COMMENT '玩家id',
  `room_record_id` int(11) unsigned NOT NULL COMMENT '开房的id',
  `end_time` bigint(20) unsigned NOT NULL COMMENT '结束时间',
  `integral_condition` int(11) unsigned NOT NULL COMMENT '积分情况，正数表示赢',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for recharge_record
-- ----------------------------
DROP TABLE IF EXISTS `recharge_record`;
CREATE TABLE `recharge_record` (
  `id` int(11) unsigned NOT NULL auto_increment COMMENT '主键',
  `user_id` int(11) unsigned NOT NULL COMMENT '充值的玩家id',
  `recharge_card` int(11) unsigned NOT NULL COMMENT '充值的数量',
  `unit_price` decimal(10,4) unsigned NOT NULL COMMENT '单价',
  `total_price` decimal(10,4) unsigned NOT NULL COMMENT '本次充值的总价',
  `time` bigint(20) unsigned NOT NULL COMMENT '充值时间',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for room_record
-- ----------------------------
DROP TABLE IF EXISTS `room_record`;
CREATE TABLE `room_record` (
  `id` int(11) unsigned NOT NULL auto_increment COMMENT '主键',
  `get_room_time` bigint(20) NOT NULL COMMENT '开房时间',
  `room_auth` int(11) unsigned NOT NULL COMMENT '开房人的id',
  `room_state` tinyint(4) NOT NULL COMMENT '房间状态，1为可用，2为已完成（打完牌后），3-已过期（半小时之内没人打牌）',
  `room_type` tinyint(4) unsigned NOT NULL COMMENT '房间类型，参考config表',
  `room_config` varchar(255) character set utf8 collate utf8_bin NOT NULL COMMENT '房间配置信息',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL auto_increment COMMENT '主键',
  `name` varchar(40) character set utf8 collate utf8_bin default NULL COMMENT '名字',
  `id_Card` varchar(20) character set utf8 collate utf8_bin default NULL COMMENT '身份证号码',
  `weixin_name` varchar(40) character set utf8 collate utf8_bin default NULL COMMENT '微信名字',
  `weixin_id` varchar(20) character set utf8 collate utf8_bin default NULL COMMENT '微信号',
  `xianliao_name` varchar(40) character set utf8 collate utf8_bin default NULL COMMENT '闲聊名字',
  `xianliao_id` varchar(20) character set utf8 collate utf8_bin default NULL COMMENT '闲聊名字',
  `phone_number` varchar(11) character set utf8 collate utf8_bin default NULL COMMENT '电话号码',
  `weixin_picture` blob COMMENT '微信头像',
  `xianliao_picture` blob COMMENT '闲聊头像',
  `friend_manage_flag` tinyint(4) unsigned NOT NULL COMMENT '是否开启好友管理，1为是，0为否',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user_proposals
-- ----------------------------
DROP TABLE IF EXISTS `user_proposals`;
CREATE TABLE `user_proposals` (
  `id` int(11) unsigned NOT NULL auto_increment COMMENT '主键',
  `user_id` int(11) unsigned NOT NULL COMMENT '提异议的用户id',
  `message` varchar(100) character set utf8 collate utf8_bin NOT NULL COMMENT '提议或异常信息',
  `file_urls` varchar(255) character set utf8 collate utf8_bin default NULL COMMENT '照片地址,是jspn字符串',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户提议，异常举报';
