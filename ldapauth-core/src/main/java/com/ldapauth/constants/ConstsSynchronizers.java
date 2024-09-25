package com.ldapauth.constants;

/**
 * 同步器静态常量
 */
public final class ConstsSynchronizers {

    public static final String SYSTEM = "system";
    public static final String OPEN_LDAP = "openldap";
    public static final String DINGDING = "dingding";
    public static final String FEISHU = "feishu";
    public static final String WORKWEIXIN = "workweixin";

    public static final String ACTIVEDIRECTORY = "activedirectory";

    /**
     * 定义API接口前缀
     */
    public final static class BaseApi {
        public final static String FEISHU = "https://open.feishu.cn";
        public final static String DINGDING = "https://oapi.dingtalk.com";

        public final static String WORKWEIXIN = "https://qyapi.weixin.qq.com";
    }


    /**
     * 操作方法，add=新增 update=修改 delete=删除
     */
    public final static class SyncActionType {

        public final static String ADD = "add";
        public final static String UPDATE = "update";
        public final static String DELETE = "delete";
    }


    /**
     * department=部门 或者 organization=组织
     */
    public final static class ClassifyType {

        public final static String DEPARTMENT = "department";
        public final static String ORGS = "organization";
    }


    /**
     * 同步结果 0=成功 1失败
     */
    public final static class SyncResultStatus {


        public final static Integer SUCCESS = 0;
        public final static Integer FAIL = 1;
    }

    /**
     * 同步类型 0=组织 1用户 2组
     */
    public final static class SyncType {

        public final static Integer ORGS = 0;
        public final static Integer USER = 1;
        public final static Integer GROUP = 2;
    }
}
