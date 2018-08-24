/*
Navicat MySQL Data Transfer

Source Server         : electric
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : electric

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2018-08-24 14:57:07
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_id_assignment
-- ----------------------------
DROP TABLE IF EXISTS `t_test_idassignment`;
CREATE TABLE `t_test_idassignment` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `CLIENT_ID` varchar(5) NOT NULL COMMENT '租户键，引用自t_client表',
  `SERVER_ID` varchar(16) NOT NULL,
  `ENTITY_ID` varchar(128) NOT NULL,
  `PREFIX_VALUE` bigint(20) NOT NULL,
  `PREFIX_VALUE_LEN` int(8) DEFAULT '4',
  `ID_VALUE_TYPE` varchar(8) DEFAULT 'string',
  `ID_INIT_VALUE` bigint(20) DEFAULT '1',
  `ID_VALUE` bigint(20) DEFAULT '1',
  `ID_STEP_LEN` int(8) DEFAULT '1',
  `STATU` varchar(8) DEFAULT '',
  `TABLE_NAME` varchar(32) NOT NULL,
  `CREATE_DATE` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `UPDATE_DATE` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '记录更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;
