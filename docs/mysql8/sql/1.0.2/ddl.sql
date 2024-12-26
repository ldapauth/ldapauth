/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 80030
 Source Host           : 127.0.0.1:3306
 Source Schema         : ldapauth

 Target Server Type    : MySQL
 Target Server Version : 80030
 File Encoding         : 65001

 Date: 25/09/2024 10:15:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for lda_client
-- ----------------------------
DROP TABLE IF EXISTS `lda_client`;
CREATE TABLE `lda_client`  (
    `id` bigint NOT NULL DEFAULT 0 COMMENT 'id',
    `app_code` varchar(255)  NULL DEFAULT '' COMMENT '应用编码',
    `app_name` varchar(255)  NULL DEFAULT '' COMMENT '应用名称',
    `path` varchar(255)  NULL DEFAULT '' COMMENT '访问路径-鉴权路径',
    `sys_id` bigint NULL DEFAULT 0 COMMENT '系统ID',
    `icon` varchar(255)  NULL DEFAULT '' COMMENT '图标',
    `protocol` tinyint NULL DEFAULT 0 COMMENT '标签,0=oidc 1=saml  2=jwt 3=cas',
    `security_level` tinyint NULL DEFAULT 0 COMMENT '安全级别，1~10，级别越高，安全越高',
    `device_type` tinyint NULL DEFAULT 0 COMMENT '设备类型，0=移动端 1=PC端 2=WEB端',
    `home_uri` varchar(255)  NULL DEFAULT '' COMMENT '应用访问地址',
    `logout_uri` varchar(255)  NULL DEFAULT '' COMMENT '应用退出地址',
    `description` varchar(255)  NULL DEFAULT '' COMMENT '描述',
    `status` tinyint NULL DEFAULT 0 COMMENT '状态 0正常 1禁用',
    `client_id` varchar(255)  NULL DEFAULT '' COMMENT '客户端ID',
    `client_secret` varchar(255)  NULL DEFAULT '' COMMENT '客户端密钥',
    `create_by` varchar(255)  NULL DEFAULT '' COMMENT '创建人',
    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(255)  NULL DEFAULT '' COMMENT '修改人',
    `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  COMMENT = '应用主表' ;

-- ----------------------------
-- Table structure for lda_client_cas_details
-- ----------------------------
DROP TABLE IF EXISTS `lda_client_cas_details`;
CREATE TABLE `lda_client_cas_details`  (
    `id` bigint NOT NULL COMMENT 'id',
    `device_type` tinyint NULL DEFAULT 0 COMMENT '设备类型，0=移动端 1=PC端 2=WEB端',
    `app_id` bigint NULL DEFAULT 0 COMMENT '应用ID',
    `subject` varchar(255)  NULL DEFAULT '' COMMENT '响应主体-username/mobile/email等',
    `server_names` varchar(255)  NULL DEFAULT '' COMMENT 'CAS客户端service',
    `ip_white_ids` varchar(255)  NULL DEFAULT '' COMMENT 'IP白名单集合',
    `ticket_validity` int NULL DEFAULT 0 COMMENT 'ticket有效时间(单位秒)',
    `status` tinyint NULL DEFAULT 0 COMMENT '状态 0正常 1禁用',
    `create_by` varchar(255)  NULL DEFAULT '' COMMENT '创建人',
    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(255)  NULL DEFAULT '' COMMENT '修改人',
    `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  COMMENT = 'CAS扩展表' ;

-- ----------------------------
-- Table structure for lda_client_jwt_details
-- ----------------------------
DROP TABLE IF EXISTS `lda_client_jwt_details`;
CREATE TABLE `lda_client_jwt_details`  (
    `id` bigint NOT NULL DEFAULT 0 COMMENT 'id',
    `device_type` tinyint NULL DEFAULT 0 COMMENT '设备类型，0=移动端 1=PC端 2=WEB端',
    `app_id` bigint NULL DEFAULT 0 COMMENT '应用ID',
    `subject` varchar(255)  NULL DEFAULT '' COMMENT '响应主体-username/mobile/email等',
    `redirect_uri` varchar(255)  NULL DEFAULT '' COMMENT '回调地址',
    `ip_white_ids` varchar(255)  NULL DEFAULT '' COMMENT 'IP白名单集合',
    `sso_binding` varchar(255)  NULL DEFAULT '' COMMENT '单点登陆请求方式 POST/GET',
    `id_token_validity` int NULL DEFAULT 0 COMMENT 'IDToken有效时间(单位秒)',
    `jwt_name` varchar(255)  NULL DEFAULT '' COMMENT 'jwtName',
    `is_signature` varchar(255)  NULL DEFAULT '' COMMENT '是否签名 yes签名 no不签名',
    `signature` varchar(255)  NULL DEFAULT '' COMMENT '签名方法',
    `signature_key` varchar(4096)  NULL DEFAULT '' COMMENT '签名密钥',
    `is_algorithm` varchar(255)  NULL DEFAULT '' COMMENT '是否加密 yes加密 no不签名',
    `algorithm` varchar(255)  NULL DEFAULT '' COMMENT '加密算法',
    `algorithm_method` varchar(255)  NULL DEFAULT '' COMMENT '加密方法',
    `algorithm_key` varchar(255)  NULL DEFAULT '' COMMENT '加密key',
    `issuer` varchar(255)  NULL DEFAULT '' COMMENT '签发人',
    `status` tinyint NULL DEFAULT 0 COMMENT '状态 0正常 1禁用',
    `create_by` varchar(255)  NULL DEFAULT '' COMMENT '创建人',
    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(255)  NULL DEFAULT '' COMMENT '修改人',
    `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  COMMENT = 'JWT扩展表' ;

-- ----------------------------
-- Table structure for lda_client_login_log
-- ----------------------------
DROP TABLE IF EXISTS `lda_client_login_log`;
CREATE TABLE `lda_client_login_log`  (
  `id` bigint NOT NULL DEFAULT 0 COMMENT '日志ID',
  `user_id` bigint NULL DEFAULT 0 COMMENT '用户ID',
  `display_name` varchar(255)  NULL DEFAULT '' COMMENT '用户显示名称',
  `app_id` bigint NULL DEFAULT NULL COMMENT '应用ID',
  `app_name` varchar(255)  NULL DEFAULT NULL COMMENT '应用名称',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  COMMENT = '应用访问日志表' ;

-- ----------------------------
-- Table structure for lda_client_oidc_details
-- ----------------------------
DROP TABLE IF EXISTS `lda_client_oidc_details`;
CREATE TABLE `lda_client_oidc_details`  (
    `id` bigint NOT NULL DEFAULT 0 COMMENT 'id',
    `device_type` tinyint NULL DEFAULT 0 COMMENT '设备类型，0=移动端 1=PC端 2=WEB端',
    `app_id` bigint NULL DEFAULT 0 COMMENT '应用ID',
    `client_id` varchar(255)  NULL DEFAULT '' COMMENT '客户端ID',
    `client_secret` varchar(255)  NULL DEFAULT '' COMMENT '客户端密钥',
    `ip_white_ids` varchar(255)  NULL DEFAULT '' COMMENT 'IP白名单集合',
    `scope` varchar(255)  NULL DEFAULT '' COMMENT '作用域',
    `authorized_grant_types` varchar(255)  NULL DEFAULT '' COMMENT '授权类型',
    `redirect_uri` varchar(255)  NULL DEFAULT '' COMMENT '回调地址',
    `code_validity` int NULL DEFAULT 0 COMMENT 'code有效时间(单位秒)',
    `access_token_validity` int NULL DEFAULT 0 COMMENT 'accessToken有效时间(单位秒)',
    `refresh_token_validity` int NULL DEFAULT NULL COMMENT 'refreshToken有效时间(单位秒)',
    `additional_information` varchar(255)  NULL DEFAULT '' COMMENT '扩展信息',
    `subject` varchar(255)  NULL DEFAULT '' COMMENT '响应主体-username/mobile/email等',
    `response_mode` varchar(255)  NULL DEFAULT '' COMMENT 'responseMode query/fragment/form_post等',
    `id_token_validity` int NULL DEFAULT 0 COMMENT 'IDToken有效时间(单位秒)',
    `is_signature` varchar(255)  NULL DEFAULT '' COMMENT '是否签名 yes签名 no不签名',
    `signature` varchar(255)  NULL DEFAULT '' COMMENT '签名方法',
    `signature_key` text  NULL COMMENT '签名密钥',
    `is_algorithm` varchar(255)  NULL DEFAULT '' COMMENT '是否加密 yes加密 no不签名',
    `algorithm` varchar(255)  NULL DEFAULT '' COMMENT '加密算法',
    `algorithm_method` varchar(255)  NULL DEFAULT '' COMMENT '加密方法',
    `algorithm_key` varchar(255)  NULL DEFAULT '' COMMENT '加密key',
    `issuer` varchar(255)  NULL DEFAULT '' COMMENT '签发人',
    `status` tinyint NULL DEFAULT 0 COMMENT '状态 0正常 1禁用',
    `create_by` varchar(255)  NULL DEFAULT '' COMMENT '创建人',
    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(255)  NULL DEFAULT '' COMMENT '修改人',
    `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  COMMENT = 'OAUTH扩展表' ;

-- ----------------------------
-- Table structure for lda_client_saml_details
-- ----------------------------
DROP TABLE IF EXISTS `lda_client_saml_details`;
CREATE TABLE `lda_client_saml_details`  (
     `id` bigint NOT NULL DEFAULT 0 COMMENT 'id',
     `device_type` tinyint NULL DEFAULT 0 COMMENT '设备类型，0=移动端 1=PC端 2=WEB端',
     `app_id` bigint NULL DEFAULT 0 COMMENT '应用ID',
     `subject` varchar(255)  NULL DEFAULT '' COMMENT '响应主体-username/mobile/email等',
     `cert_issuer` varchar(255)  NULL DEFAULT '' COMMENT '证书颁发者',
     `cert_subject` varchar(255)  NULL DEFAULT '' COMMENT '证书主题',
     `cert_expiration` varchar(255)  NULL DEFAULT '' COMMENT '证书过期时间',
     `keystore` blob NULL COMMENT '证书对象',
     `sp_acs_uri` varchar(255)  NULL DEFAULT '' COMMENT 'Sp Acs Uri',
     `issuer` varchar(255)  NULL DEFAULT '' COMMENT 'issuer',
     `entity_id` varchar(255)  NULL DEFAULT '' COMMENT 'entityId',
     `name_id_format` varchar(255)  NULL DEFAULT '' COMMENT 'nameIdFormat',
     `name_id_convert` varchar(255)  NULL DEFAULT '' COMMENT 'nameIdConvert',
     `name_id_suffix` varchar(255)  NULL DEFAULT '' COMMENT 'nameIdSuffix',
     `audience` varchar(255)  NULL DEFAULT '' COMMENT 'audience',
     `encrypted` varchar(255)  NULL DEFAULT '' COMMENT 'encrypted',
     `binding` varchar(255)  NULL DEFAULT '' COMMENT 'binding',
     `signature` varchar(255)  NULL DEFAULT '' COMMENT 'signature',
     `meta_uri` varchar(255)  NULL DEFAULT '' COMMENT 'metaUri',
     `digest_method` varchar(255)  NULL DEFAULT '' COMMENT 'digestMethod',
     `ip_white_ids` varchar(255)  NULL DEFAULT '' COMMENT 'IP白名单集合',
     `meta_type` varchar(255)  NULL DEFAULT '' COMMENT '元数据类型',
     `status` tinyint NULL DEFAULT 0 COMMENT '状态 0正常 1禁用',
     `create_by` varchar(255)  NULL DEFAULT '' COMMENT '创建人',
     `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
     `update_by` varchar(255)  NULL DEFAULT '' COMMENT '修改人',
     `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  COMMENT = 'SAML扩展表' ;

-- ----------------------------
-- Table structure for lda_file_upload
-- ----------------------------
DROP TABLE IF EXISTS `lda_file_upload`;
CREATE TABLE `lda_file_upload`  (
    `id` bigint NOT NULL,
    `file_name` varchar(400)  NULL DEFAULT NULL,
    `uploaded` longblob NULL,
    `content_size` bigint NULL DEFAULT NULL,
    `content_type` varchar(100)  NULL DEFAULT NULL,
    `create_by` bigint NULL DEFAULT NULL,
    `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  COMMENT = '文件上传表' ;

-- ----------------------------
-- Table structure for lda_group
-- ----------------------------
DROP TABLE IF EXISTS `lda_group`;
CREATE TABLE `lda_group`  (
  `id` bigint NOT NULL DEFAULT 0 COMMENT '组ID',
  `object_from` varchar(32)  NULL DEFAULT 'system' COMMENT '来源 ldap=ldap 或者 system=本地',
  `ldap_dn` varchar(512)  NULL DEFAULT '' COMMENT 'LDAPDN',
  `name` varchar(256)  NULL DEFAULT '' COMMENT '组名称',
  `open_id` varchar(128)  NULL DEFAULT '' COMMENT '开放ID',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0-启用；1-禁用',
  `description` varchar(200)  NULL DEFAULT '' COMMENT '备注',
  `create_by` bigint NULL DEFAULT 0 COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT 0 COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `ldap_id` varchar(255)  NULL DEFAULT '' COMMENT 'LDAP唯一标识（entryUUID）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  COMMENT = '分组表' ;

-- ----------------------------
-- Table structure for lda_group_apps
-- ----------------------------
DROP TABLE IF EXISTS `lda_group_apps`;
CREATE TABLE `lda_group_apps`  (
    `id` bigint NOT NULL DEFAULT 0 COMMENT '组ID',
    `group_id` bigint NULL DEFAULT 0 COMMENT '分组ID',
    `app_id` bigint NULL DEFAULT 0 COMMENT '应用ID',
    `create_by` bigint NULL DEFAULT 0 COMMENT '创建人',
    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by` bigint NULL DEFAULT 0 COMMENT '修改人',
    `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_group_id`(`group_id`) USING BTREE COMMENT '分组ID索引',
    INDEX `idx_app_id`(`app_id`) USING BTREE COMMENT '应用ID索引'
) ENGINE = InnoDB  COMMENT = '分组应用关系表' ;

-- ----------------------------
-- Table structure for lda_group_member
-- ----------------------------
DROP TABLE IF EXISTS `lda_group_member`;
CREATE TABLE `lda_group_member`  (
     `id` bigint NOT NULL DEFAULT 0 COMMENT '组ID',
     `group_id` bigint NULL DEFAULT 0 COMMENT '分组ID',
     `member_id` bigint NULL DEFAULT 0 COMMENT '成员ID',
     `create_by` bigint NULL DEFAULT 0 COMMENT '创建人',
     `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
     `update_by` bigint NULL DEFAULT 0 COMMENT '修改人',
     `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
     PRIMARY KEY (`id`) USING BTREE,
     INDEX `idx_group_id`(`group_id`) USING BTREE COMMENT '分组ID索引',
     INDEX `idx_member_id`(`member_id`) USING BTREE COMMENT '成员ID索引'
) ENGINE = InnoDB  COMMENT = '分组成员表' ;

-- ----------------------------
-- Table structure for lda_group_resources
-- ----------------------------
DROP TABLE IF EXISTS `lda_group_resources`;
CREATE TABLE `lda_group_resources`  (
    `id` bigint NOT NULL COMMENT '编码',
    `resource_id` bigint NULL DEFAULT 0 COMMENT '资源编码',
    `group_id` bigint NULL DEFAULT 0 COMMENT '组编码',
    `create_by` bigint NULL DEFAULT 0 COMMENT '创建人',
    `create_time` datetime NULL DEFAULT '1970-01-01 00:00:01' COMMENT '创建时间',
    `update_by` bigint NULL DEFAULT 0 COMMENT '修改人',
    `update_time` datetime NULL DEFAULT '1970-01-01 00:00:01' COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_group_id`(`group_id`) USING BTREE COMMENT '分组ID索引',
    INDEX `idx_resource_id`(`resource_id`) USING BTREE COMMENT '资源ID索引'
) ENGINE = InnoDB  COMMENT = '组-资源表' ;

-- ----------------------------
-- Table structure for lda_login_log
-- ----------------------------
DROP TABLE IF EXISTS `lda_login_log`;
CREATE TABLE `lda_login_log`  (
  `id` bigint NOT NULL DEFAULT 0 COMMENT '日志ID',
  `user_id` bigint NULL DEFAULT 0 COMMENT '用户ID',
  `display_name` varchar(255)  NULL DEFAULT '' COMMENT '用户显示名称',
  `login_type` varchar(255)  NULL DEFAULT '' COMMENT '登录方式',
  `provider` varchar(255)  NULL DEFAULT '' COMMENT '身份提供商',
  `operate_system` varchar(255)  NULL DEFAULT '' COMMENT '操作系统',
  `browser` varchar(255)  NULL DEFAULT '' COMMENT '浏览器',
  `ip_addr` varchar(255)  NULL DEFAULT '' COMMENT 'ip地址',
  `country` varchar(255)  NULL DEFAULT '' COMMENT '国家',
  `province` varchar(255)  NULL DEFAULT '' COMMENT '省',
  `city` varchar(255)  NULL DEFAULT '' COMMENT '市',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态 0成功 1失败',
  `message` text  NULL COMMENT '日志内容',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  ;

-- ----------------------------
-- Table structure for lda_organization
-- ----------------------------
DROP TABLE IF EXISTS `lda_organization`;
CREATE TABLE `lda_organization`  (
    `id` bigint NOT NULL DEFAULT 0 COMMENT '组织ID',
    `object_from` varchar(32)  NULL DEFAULT 'system' COMMENT '来源 ldap=ldap 或者 system=本地',
    `ldap_dn` varchar(256)  NULL DEFAULT '' COMMENT 'ldap 路径（ldapdn）',
    `org_code` varchar(64)  NULL DEFAULT '' COMMENT '组织编码',
    `org_name` varchar(64)  NULL DEFAULT '' COMMENT '组织名称',
    `classify` varchar(20)  NULL DEFAULT '' COMMENT '分类',
    `parent_id` bigint NULL DEFAULT 0 COMMENT '父级ID',
    `parent_name` varchar(64)  NULL DEFAULT '' COMMENT '父级名称',
    `name_path` varchar(1024)  NULL DEFAULT '' COMMENT '名称路径',
    `id_path` varchar(1024)  NULL DEFAULT '' COMMENT 'ID路径',
    `status` tinyint NULL DEFAULT 0 COMMENT '状态：0-启用；1-禁用',
    `sort_index` int NULL DEFAULT 1 COMMENT '排序',
    `description` varchar(200)  NULL DEFAULT '' COMMENT '备注',
    `create_by` bigint NULL DEFAULT 0 COMMENT '创建人',
    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by` bigint NULL DEFAULT 0 COMMENT '修改人',
    `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
    `open_department_id` varchar(255)  NULL DEFAULT NULL COMMENT '组织开放ID，第三方ID',
    `ldap_id` varchar(255)  NULL DEFAULT '' COMMENT 'LDAP唯一标识（entryUUID）',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_org_code`(`org_code`) USING BTREE COMMENT '组织编码索引',
    INDEX `idx_org_name`(`org_name`) USING BTREE COMMENT '组织名称索引',
    INDEX `idx_open_department_id`(`open_department_id`) USING BTREE COMMENT '组织开放ID'
) ENGINE = InnoDB  COMMENT = '组织机构表' ;

-- ----------------------------
-- Table structure for lda_policy_login
-- ----------------------------
DROP TABLE IF EXISTS `lda_policy_login`;
CREATE TABLE `lda_policy_login`  (
    `id` bigint NOT NULL COMMENT '编码',
    `refresh_token_validity` int NULL DEFAULT 36000 COMMENT 'refresh token有效期（单位秒）',
    `access_token_validity` int NULL DEFAULT 3600 COMMENT 'access_token有效期（单位秒）',
    `is_captcha` int NULL DEFAULT 1 COMMENT '图形验证码 0开启 1关闭',
    `password_attempts` int NULL DEFAULT 3 COMMENT '密码输入错误次数',
    `login_lock_interval` int NULL DEFAULT 1800 COMMENT '锁定时间（分钟）',
    `create_by` bigint NULL DEFAULT 0 COMMENT '创建人',
    `create_time` datetime NULL DEFAULT '1970-01-01 00:00:01' COMMENT '创建时间',
    `update_by` bigint NULL DEFAULT 0 COMMENT '修改人',
    `update_time` datetime NULL DEFAULT '1970-01-01 00:00:01' COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  COMMENT = '登录策略表' ;

-- ----------------------------
-- Table structure for lda_policy_password
-- ----------------------------
DROP TABLE IF EXISTS `lda_policy_password`;
CREATE TABLE `lda_policy_password`  (
    `id` bigint NOT NULL COMMENT '编码',
    `min_length` tinyint NULL DEFAULT 8 COMMENT '最小长度',
    `max_length` tinyint NULL DEFAULT 20 COMMENT '最大长度',
    `expiration_days` int NULL DEFAULT 180 COMMENT '过期时间(天)',
    `is_digit` tinyint NULL DEFAULT 1 COMMENT '数字',
    `is_special` tinyint NULL DEFAULT 1 COMMENT '特殊字符',
    `is_lower_case` tinyint NULL DEFAULT 1 COMMENT '字母',
    `create_by` bigint NULL DEFAULT 0 COMMENT '创建人',
    `create_time` datetime NULL DEFAULT '1970-01-01 00:00:01' COMMENT '创建时间',
    `update_by` bigint NULL DEFAULT 0 COMMENT '修改人',
    `update_time` datetime NULL DEFAULT '1970-01-01 00:00:01' COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  COMMENT = '密码策略' ;

-- ----------------------------
-- Table structure for lda_resources
-- ----------------------------
DROP TABLE IF EXISTS `lda_resources`;
CREATE TABLE `lda_resources`  (
    `id` bigint NOT NULL COMMENT '编码',
    `name` varchar(200)  NULL DEFAULT '' COMMENT '资源名称',
    `permission` varchar(200)  NULL DEFAULT '' COMMENT '权限值',
    `parent_id` bigint NULL DEFAULT 0 COMMENT '父级编码',
    `url` varchar(200)  NULL DEFAULT '' COMMENT '资源地址',
    `name_path` varchar(1024)  NULL DEFAULT '' COMMENT '名称路径',
    `id_path` varchar(1024)  NULL DEFAULT '' COMMENT 'ID路径',
    `classify` varchar(10)  NULL DEFAULT 'menu' COMMENT '类型 menu-菜单类型 button-按钮类型 api-接口类型',
    `route_path` varchar(200)  NULL DEFAULT '' COMMENT '路由地址',
    `route_component` varchar(200)  NULL DEFAULT '' COMMENT '组件路径',
    `route_query` varchar(200)  NULL DEFAULT '' COMMENT '路由参数',
    `is_frame` tinyint NULL DEFAULT 0 COMMENT '是否为外链 0-否 1-是',
    `is_cache` tinyint NULL DEFAULT 0 COMMENT '是否缓存 0-否 1-是',
    `is_visible` tinyint NULL DEFAULT 0 COMMENT '是否可见 0-否 1-是',
    `sort_order` int NULL DEFAULT 1 COMMENT '排序',
    `res_action` varchar(10)  NULL DEFAULT 'read' COMMENT '资源操作 read-读操作 write-写操作 list-列表操作',
    `request_method` varchar(10)  NULL DEFAULT 'GET' COMMENT '请求方法 GET/POST',
    `icon` varchar(200)  NULL DEFAULT '' COMMENT '图标',
    `app_id` bigint NULL DEFAULT 0 COMMENT '应用id',
    `status` tinyint NULL DEFAULT 0 COMMENT '状态',
    `description` varchar(200)  NULL DEFAULT '' COMMENT '备注',
    `create_by` bigint NULL DEFAULT 0 COMMENT '创建人',
    `create_time` datetime NULL DEFAULT '1970-01-01 00:00:01' COMMENT '创建时间',
    `update_by` bigint NULL DEFAULT 0 COMMENT '修改人',
    `update_time` datetime NULL DEFAULT '1970-01-01 00:00:01' COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  COMMENT = '资源表' ;

-- ----------------------------
-- Table structure for lda_sms_provider
-- ----------------------------
DROP TABLE IF EXISTS `lda_sms_provider`;
CREATE TABLE `lda_sms_provider`  (
    `id` bigint NOT NULL COMMENT 'ID',
    `provider` varchar(50)  NOT NULL DEFAULT '' COMMENT '厂商名称',
    `hostname` varchar(255)  NULL DEFAULT NULL COMMENT '厂商地址',
    `app_key` varchar(64)  NOT NULL DEFAULT '' COMMENT '凭证',
    `app_secret` varchar(64)  NOT NULL DEFAULT '' COMMENT '密钥',
    `sign_name` varchar(50)  NOT NULL DEFAULT '' COMMENT '签名',
    `create_by` bigint NULL DEFAULT 0 COMMENT '创建人',
    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by` bigint NULL DEFAULT 0 COMMENT '修改人',
    `update_time` datetime NULL DEFAULT NULL COMMENT '最后修改时间',
    `status` tinyint NULL DEFAULT 0 COMMENT '状态 0正常 1禁用',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  COMMENT = '短信配置表' ;

-- ----------------------------
-- Table structure for lda_socials_associate
-- ----------------------------
DROP TABLE IF EXISTS `lda_socials_associate`;
CREATE TABLE `lda_socials_associate`  (
    `id` bigint NOT NULL COMMENT '编码',
    `user_id` bigint NULL DEFAULT 0 COMMENT '系统用户ID',
    `socials_user_id` varchar(200)  NULL DEFAULT '' COMMENT '第三方用户ID',
    `socials_id` bigint NULL DEFAULT 0 COMMENT '第三方ID',
    `socials_avatar` varchar(255)  NULL DEFAULT '' COMMENT '第三方头像',
    `socials_nick_name` varchar(64)  NULL DEFAULT '' COMMENT '第三方昵称',
    `create_by` bigint NULL DEFAULT 0 COMMENT '创建人',
    `create_time` datetime NULL DEFAULT '1970-01-01 00:00:01' COMMENT '创建时间',
    `update_by` bigint NULL DEFAULT 0 COMMENT '修改人',
    `update_time` datetime NULL DEFAULT '1970-01-01 00:00:01' COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_s_user_id`(`user_id`, `socials_user_id`, `socials_id`) USING BTREE COMMENT '第三方用户索引'
) ENGINE = InnoDB  COMMENT = '第三方关联用户表' ;

-- ----------------------------
-- Table structure for lda_socials_provider
-- ----------------------------
DROP TABLE IF EXISTS `lda_socials_provider`;
CREATE TABLE `lda_socials_provider`  (
    `id` bigint NOT NULL COMMENT '编码',
    `icon` varchar(255)  NULL DEFAULT '' COMMENT '图标',
    `type` varchar(32)  NULL DEFAULT '' COMMENT '类型',
    `name` varchar(64)  NULL DEFAULT '' COMMENT '名称',
    `client_id` varchar(1024)  NULL DEFAULT '' COMMENT '凭证',
    `client_secret` varchar(1024)  NULL DEFAULT '' COMMENT '密钥',
    `agent_id` varchar(64)  NULL DEFAULT '' COMMENT 'agentId',
    `alipay_public_key` varchar(4096)  NULL DEFAULT '' COMMENT '支付宝公钥',
    `redirect_uri` varchar(255)  NULL DEFAULT '' COMMENT '回调地址',
    `status` tinyint NULL DEFAULT 0 COMMENT '状态',
    `description` varchar(200)  NULL DEFAULT '' COMMENT '备注',
    `create_by` bigint NULL DEFAULT 0 COMMENT '创建人',
    `create_time` datetime NULL DEFAULT '1970-01-01 00:00:01' COMMENT '创建时间',
    `update_by` bigint NULL DEFAULT 0 COMMENT '修改人',
    `update_time` datetime NULL DEFAULT '1970-01-01 00:00:01' COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  COMMENT = '第三方配置表' ;

-- ----------------------------
-- Table structure for lda_synchronizers
-- ----------------------------
DROP TABLE IF EXISTS `lda_synchronizers`;
CREATE TABLE `lda_synchronizers`  (
    `id` bigint NOT NULL DEFAULT 0 COMMENT 'ID',
    `master_type` tinyint NULL DEFAULT 0 COMMENT '主数据: 0-是; 1-否',
    `classify` varchar(32)  NULL DEFAULT 'openldap' COMMENT '类型 openldap=openldap ad-AD feishu-飞书 dingding-钉钉 workweixin-企业微信',
    `cron` varchar(200)  NULL DEFAULT '0 0 0/6 * * ?' COMMENT '任务执行表达式',
    `base_api` varchar(255)  NULL DEFAULT 'ldap://test.com' COMMENT '链接地址，http://或者ldap://',
    `base_domain` varchar(200)  NULL DEFAULT 'test.com' COMMENT '域名',
    `base_dn` varchar(200)  NULL DEFAULT 'DC=test.DC=com' COMMENT '基本DN',
    `client_id` varchar(255)  NULL DEFAULT '' COMMENT '客户端ID，有可能是LDAP账号',
    `client_secret` varchar(255)  NULL DEFAULT '' COMMENT '客户端秘钥，有可能是LDAP密码',
    `user_client_secret` varchar(255)  NULL DEFAULT '' COMMENT '客户端通讯录秘钥，主要用于企业微信',
    `root_id` varchar(255)  NULL DEFAULT '' COMMENT '根节点ID',
    `org_filter` varchar(200)  NULL DEFAULT '' COMMENT '组织过滤条件',
    `user_filter` varchar(200)  NULL DEFAULT '' COMMENT '用户过滤条件',
    `description` varchar(200)  NULL DEFAULT '' COMMENT '备注',
    `create_by` bigint NULL DEFAULT 0 COMMENT '创建人',
    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by` bigint NULL DEFAULT 0 COMMENT '修改人',
    `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
    `status` tinyint NULL DEFAULT 0 COMMENT '状态：0-启用；1-禁用',
    `user_dn` varchar(200)  NULL DEFAULT '' COMMENT '用户所在DN',
    `group_dn` varchar(200)  NULL DEFAULT '' COMMENT '组所在DN',
    `group_filter` varchar(200)  NULL DEFAULT '' COMMENT '组过滤条件',
    `openssl` tinyint NULL DEFAULT 0 COMMENT '是否开启SSL',
    `sslfile` longblob NULL COMMENT 'SSL证书',
    `trust_store` varchar(255)  NULL DEFAULT NULL COMMENT 'JKS文件路径',
    `trust_store_password` varchar(255)  NULL DEFAULT NULL COMMENT 'JKS文件密码',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  COMMENT = '同步器表' ;

-- ----------------------------
-- Table structure for lda_synchronizers_logs
-- ----------------------------
DROP TABLE IF EXISTS `lda_synchronizers_logs`;
CREATE TABLE `lda_synchronizers_logs`  (
    `id` bigint NOT NULL DEFAULT 0 COMMENT '日志ID',
    `synchronizer_id` bigint NULL DEFAULT 0 COMMENT '同步器ID',
    `tracking_unique_id` bigint NULL DEFAULT 0 COMMENT '任务跟踪ID',
    `sync_data` varchar(1024)  NULL DEFAULT '' COMMENT '同步对象内容',
    `sync_type` tinyint NULL DEFAULT 0 COMMENT '同步类型 0=组织 1用户',
    `sync_result_status` tinyint NULL DEFAULT 0 COMMENT '同步结果 0=成功 1失败',
    `sync_message` varchar(4096)  NULL DEFAULT '' COMMENT '同步消息',
    `sync_action_type` varchar(45)  NULL DEFAULT 'add' COMMENT '操作方法，add=新增 update=修改 delete=删除',
    `sync_api_uri` varchar(255)  NULL DEFAULT '' COMMENT '接口地址',
    `create_by` bigint NULL DEFAULT 0 COMMENT '创建人',
    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by` bigint NULL DEFAULT 0 COMMENT '修改人',
    `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_synchronizer_type_status_type`(`synchronizer_id`, `sync_type`, `sync_result_status`, `sync_action_type`) USING BTREE COMMENT '同步日志索引'
) ENGINE = InnoDB  COMMENT = '同步器日志表' ;

-- ----------------------------
-- Table structure for lda_system
-- ----------------------------
DROP TABLE IF EXISTS `lda_system`;
CREATE TABLE `lda_system`  (
    `id` bigint NOT NULL DEFAULT 0 COMMENT 'ID',
    `logo` varchar(255)  NULL DEFAULT '' COMMENT '系统logo',
    `title` varchar(255)  NULL DEFAULT '' COMMENT '系统标题',
    `copyright` varchar(1024)  NULL DEFAULT '' COMMENT '版权信息',
    `create_by` bigint NULL DEFAULT 0 COMMENT '创建人',
    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by` bigint NULL DEFAULT 0 COMMENT '修改人',
    `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB  COMMENT = '系统信息表' ;

-- ----------------------------
-- Table structure for lda_userinfo
-- ----------------------------
DROP TABLE IF EXISTS `lda_userinfo`;
CREATE TABLE `lda_userinfo`  (
    `id` bigint NOT NULL DEFAULT 0 COMMENT '主键id',
    `object_from` varchar(32)  NULL DEFAULT 'system' COMMENT '来源 ldap=ldap 或者 system=本地',
    `ldap_dn` varchar(256)  NULL DEFAULT '' COMMENT 'ldap 路径（ldapdn）',
    `username` varchar(256)  NULL DEFAULT '' COMMENT '登录账号',
    `password` varchar(256)  NULL DEFAULT '' COMMENT '密码',
    `decipherable` varchar(500)  NULL DEFAULT NULL COMMENT 'DE密码',
    `gender` tinyint NULL DEFAULT 0 COMMENT '性别:0-其他；1-男；2-女',
    `display_name` varchar(256)  NULL DEFAULT '' COMMENT '显示名称',
    `nick_name` varchar(256)  NULL DEFAULT '' COMMENT '昵称',
    `avatar` varchar(255)  NULL DEFAULT NULL COMMENT '头像',
    `email` varchar(256)  NULL DEFAULT '' COMMENT '电子邮箱',
    `mobile` varchar(64)  NULL DEFAULT '' COMMENT '手机号码',
    `birth_date` date NULL DEFAULT '1970-01-01' COMMENT '出生日期',
    `department_id` bigint NULL DEFAULT 0 COMMENT '所属部门标识',
    `is_locked` tinyint NULL DEFAULT 1 COMMENT '锁定状态：1-正常；5-锁定',
    `un_lock_time` datetime NULL DEFAULT NULL COMMENT '解锁时间',
    `bad_password_count` tinyint NULL DEFAULT 0 COMMENT '错误密码次数',
    `bad_password_time` datetime NULL DEFAULT '1970-01-01 00:00:00' COMMENT '错误密码时间',
    `password_last_set_time` datetime NULL DEFAULT '1970-01-01 00:00:00' COMMENT '最后修改密码时间',
    `login_count` int NULL DEFAULT 0 COMMENT '登录次数',
    `create_by` bigint NULL DEFAULT 0 COMMENT '创建人',
    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by` bigint NULL DEFAULT 0 COMMENT '修改人',
    `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
    `status` tinyint NULL DEFAULT 0 COMMENT '用户状态：0-启用；1-禁用',
    `last_login_time` datetime NULL DEFAULT '1970-01-01 00:00:00' COMMENT '最后登录时间',
    `last_login_ip` varchar(255)  NULL DEFAULT '' COMMENT '最后登录IP',
    `login_failed_count` tinyint NULL DEFAULT 0 COMMENT '登录失败次数',
    `login_failed_time` datetime NULL DEFAULT '1970-01-01 00:00:00' COMMENT '登录失败时间',
    `description` varchar(200)  NULL DEFAULT NULL COMMENT '备注',
    `open_id` varchar(255)  NULL DEFAULT NULL COMMENT '开放ID',
    `ldap_id` varchar(255)  NULL DEFAULT '' COMMENT 'LDAP唯一标识（entryUUID）',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_username`(`username`) USING BTREE COMMENT '登录账号索引',
    INDEX `idx_mobile`(`mobile`) USING BTREE COMMENT '手机号码索引',
    INDEX `idx_open_id`(`open_id`) USING BTREE COMMENT '开放用户ID'
) ENGINE = InnoDB  COMMENT = '用户表' ;

SET FOREIGN_KEY_CHECKS = 1;
