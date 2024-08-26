## 项目介绍

基于OpenLDAP企业级认证平台(EIAM)，用于管理企业组织架构、员工账号、身份认证、应用访问，帮助整合本地或云端的业务系统及第三方 SaaS 系统的所有身份，实现一个账号登录访问所有应用。支持OAuth2.x、OIDC、SAML2.0、JWT、CAS等SSO标准协议。

官网地址：http://ldapauth.com

官网邮箱：contact@ldapauth.com

付费服务支持或商务合作：


<img src="weixin.jpg" width="200px" />

--------------------------

<div align="center">⭐️ 如果你喜欢 LdapAuth，请给它一个 Star，您的支持将是我们前行的动力。</div>

--------------------------

## 核心特性

+ 基于OpenLDAP企业级认证平台(EIAM)，用于管理企业组织架构、员工账号、身份认证、应用访问统一管理。
+ 支持企业微信,钉钉,飞书,OpenLDAP等开箱即用数据源。
+ 支持图片动态验证码、短信验证码、Google/Microsoft Authenticator/FreeOTP/支持TOTP或者HOTP，保证用户认证安全可靠。
+ 支持微信、钉钉、飞书QQ等社交认证集成，使企业具有快速纳入互联网化认证能力。
+ 支持 `SAML2`，`OAuth2`，`OIDC`，`CAS`，`JWT`等认证协议及机制，实现单点登录功能。
+ 完善的安全审计，详尽记录每一次用户行为，使每一步操作有据可循，实时记录企业信息安全状况，精准识别企业异常访问和潜在威胁的源头。

## 技术选型
#### 后端技术

| 框架                     | 说明            | 版本     |
|------------------------|---------------|--------|
| Spring Boot            | 系统框架          | 2.7.18 |
| Spring Cloud Alibaba   | 系统框架         | 2021.1 |
| Freemarker             | 模板引擎         | 2.3.32| 
| Knife4j                | API文档         | 4.0.0 | 
| Mysql Connector        | 数据库驱动         | 8.0.31 |
| Druid                  | JDBC 连接池、监控组件 | 1.2.16 |
| Spring-Boot-Data-Redis | Redis         | 2.7.18 |
| MyBatis Plus           | MyBatis 增强工具包 | 3.5.4 | 
| Hutool                 | Java工具类库      | 5.8.15 | 
| Lombok                 | 消除冗长的 Java 代码 | 1.18.26 | 
| Ip2region              | IP解析工具        | 2.6.5 | 
| Pinyin4j               | 文字转换工具        | 2.5.1 |
| JavaxMail              | 邮件发送工具        | 1.6.2 |

#### 前端技术

| 框架           | 说明        | 版本     | 
|--------------|-----------|--------|
| vue          | 框架语言      | 3.4.31 |
| vite         | 构建工具      | 5.3.2 | 
| element-plus | 组件库       | 2.7.6 |
| axios        | 网络库     | 0.28.1 | 
| js-cookie    | Cookie库 | 3.0.5 |
| jsencrypt    | 数据加解密库  | 3.3.2|
| echarts      | 报表库       | 5.5.1 | 



## 单点登录
单点登录(Single Sign On)简称为SSO

用户只需要登录认证中心一次就可以访问所有相互信任的应用系统，无需再次登录，主要功能：

1.所有应用系统共享一个身份认证系统

2.所有应用系统能够识别和提取ticket信息


## LDAP
LDAP 是轻量目录访问协议，英文全称是 Lightweight Directory Access Protocol，一般都简称为 LDAP。

OpenLDAP 默认以 Berkeley DB 作为后端数据库，Berkeley DB 数据库主要以散列的数据类型进行数据存储，如以键值对的方式进行存储。Berkeley DB 是一类特殊的数据库，主要作用于搜索、浏览、更新查询操作，一般用于一次写入数据、多次查询和搜索有很好的效果。Berkeley DB 数据库时面向查询进行优化，面向读取进行优化的数据库。Berkeley DB 不支持事务性数据库（MySQL、MariDB、Oracle 等）所支持的高并发的吞吐量以及复杂的事务操作。

### 目录树概念

在一个目录服务系统中，整个目录信息集可以表示为一个目录信息树，树中的每个节点是一个条目 (Entry)。

条目（Entry）

条目，也叫记录项，是 LDAP 中最基本的颗粒，就像字典中的词条，或者是数据库中的记录。通常对 LDAP 的添加、删除、更改、检索都是以条目为基本对象的。

LDAP 目录的条目（entry）由属性（attribute）的一个聚集组成，并由一个唯一性的名字引用，即专有名称（distinguished name，DN）。例如，DN 能取这样的值：" cn=group,dc=ldapauth,dc=com "。

对象类（ObjectClass）

对象类是属性的集合，LDAP 预想了很多人员组织机构中常见的对象，并将其封装成对象类。比如人员（ person ）含有姓（ sn ）、名（ cn ）、电话 ( telephoneNumber )、密码 ( userPassword ) 等属性，单位职工 ( organizationalPerson ) 是人员 ( person ) 的继承类，除了上述属性之外还含有职务（ title ）、邮政编码（ postalCode ）、通信地址 ( postalAddress ) 等属性。

属性 (Attribute)

每个条目都可以有很多属性（Attribute），比如常见的人都有姓名、地址、电话等属性。每个属性都有名称及对应的值，属性值可以有单个、多个。



