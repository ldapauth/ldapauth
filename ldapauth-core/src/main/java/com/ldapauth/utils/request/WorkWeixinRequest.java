package com.ldapauth.utils.request;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.ldapauth.domain.Config;
import com.ldapauth.domain.WorkweixinDeptment;
import com.ldapauth.domain.WorkweixinUser;
import com.ldapauth.exception.BusinessException;
import com.ldapauth.utils.responce.WorkWeixinDeptmentResponce;
import com.ldapauth.utils.responce.WorkWeixinResponce;
import com.ldapauth.utils.responce.WorkWeixinUsersResponce;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class WorkWeixinRequest {

    private Config config;

    public WorkWeixinRequest(Config config){
        this.config = config;
    }

    private String accessToken;

   public WorkWeixinRequest token(String accessToken) {
       this.accessToken = accessToken;
       return this;
   }

    /**
     * 请求查询部门接口获取部门列表，采用企业标识和应用密钥（通讯录密钥无法获取数据）
     * 接口文档
     * https://developer.work.weixin.qq.com/document/path/90208
     * @return
     */
   public WorkWeixinDeptmentResponce departmentsList(){
       String simplelistURI = config.getBaseUri() + "/cgi-bin/department/list?access_token="+accessToken;
       String body = HttpUtil.get(simplelistURI);
       if (StringUtils.isNotBlank(body)) {
           WorkWeixinDeptmentResponce responce = JSON.parseObject(body, WorkWeixinDeptmentResponce.class);
           if (responce.getErrcode() == 0) {
               return responce;
           } else {
               throw new BusinessException(HttpStatus.BAD_REQUEST.value(),responce.getErrmsg());
           }
       }
       return null;
   }

    /**
     * 获取部门成员
     * 接口文档
     * https://developer.work.weixin.qq.com/document/path/90200
     * @return
     */
    public List<WorkweixinUser> userList(List<Integer> deptmentList){
        List<WorkweixinUser> resultUserList = new ArrayList<>();
        for (Integer id : deptmentList) {
            String deptUserUri = config.getBaseUri() + "/cgi-bin/user/simplelist?access_token=" + accessToken+"&department_id="+id;
            String body = HttpUtil.get(deptUserUri);
            if (StringUtils.isNotBlank(body)) {
                WorkWeixinUsersResponce responce = JSONObject.parseObject(body, WorkWeixinUsersResponce.class);
                if (responce.getErrcode() == 0) {
                     List<WorkweixinUser> users = responce.getUserlist();
                     for (WorkweixinUser user : users) {
                         String userInfoUri = config.getBaseUri() + "/cgi-bin/user/get?access_token=" + accessToken + "&userid=" + user.getUserid();
                         body = HttpUtil.get(userInfoUri);
                         WorkweixinUser workweixinUser = JSON.toJavaObject(JSON.parseObject(body), WorkweixinUser.class);
                         resultUserList.add(workweixinUser);
                     }
                } else {
                    throw new BusinessException(HttpStatus.BAD_REQUEST.value(),responce.getErrmsg());
                }
            }
        }
        return resultUserList;
    }

    /**
     * 创建企业微信部门
     * 文档地址
     * https://developer.work.weixin.qq.com/document/path/90205
     * @param objectMap
     * @return
     */
    public WorkWeixinResponce createDeptmentRequest(Map<String,Object> objectMap){
        String createDeptUri = config.getBaseUri() + "/cgi-bin/department/create?access_token="+accessToken;
        String body = HttpRequest.post(createDeptUri)
                .header("Content-Type", "application/json")
                .body(JSON.toJSONString(objectMap))
                .execute()
                .body();
        log.debug("createDeptmentRequest responce :{}",body);
        if (StringUtils.isNotBlank(body)) {
            WorkWeixinResponce responce = JSONObject.parseObject(body, WorkWeixinResponce.class);
            if (responce.getErrcode().intValue() == 0) {
                return responce;
            } else {
                return WorkWeixinResponce.error(responce.getErrmsg());
            }
        }
        return WorkWeixinResponce.error("请求失败，无法创建部门。");
    }

    /**
     * 修改企业微信部门
     * 文档地址
     * https://developer.work.weixin.qq.com/document/path/90206
     * @param objectMap
     * @return
     */
    public WorkWeixinResponce updateDeptmentRequest(Map<String,Object> objectMap){
        String actionUrl = config.getBaseUri() + "/cgi-bin/department/update?access_token="+accessToken;
        String body = HttpRequest.post(actionUrl)
                .header("Content-Type", "application/json")
                .body(JSON.toJSONString(objectMap))
                .execute()
                .body();
        log.debug("updateDeptmentRequest responce :{}",body);
        if (StringUtils.isNotBlank(body)) {
            WorkWeixinResponce responce = JSONObject.parseObject(body, WorkWeixinResponce.class);
            if (responce.getErrcode().intValue() == 0) {
                return responce;
            } else {
                return WorkWeixinResponce.error(responce.getErrmsg());
            }
        }
        return WorkWeixinResponce.error("请求失败，无法修改部门。");
    }


    /**
     * 删除企业微信部门
     * 文档地址
     * https://developer.work.weixin.qq.com/document/path/90207
     * @param id
     * @return
     */
    public WorkWeixinResponce deleteDeptmentRequest(String id){
        String actionUrl = config.getBaseUri() + "/cgi-bin/department/delete?access_token="+accessToken+"&id="+id;

        String body = HttpRequest.get(actionUrl)
                .execute()
                .body();
        log.debug("deleteDeptmentRequest responce :{}",body);
        if (StringUtils.isNotBlank(body)) {
            WorkWeixinResponce responce = JSONObject.parseObject(body, WorkWeixinResponce.class);
            if (responce.getErrcode().intValue() == 0) {
                return responce;
            } else {
                return WorkWeixinResponce.error(responce.getErrmsg());
            }
        }
        return WorkWeixinResponce.error("请求失败，无法删除部门。");
    }


    /**
     * 创建企业微信用户
     * 文档地址
     * https://developer.work.weixin.qq.com/document/path/90205
     * @param objectMap
     * @return
     */
    public WorkWeixinResponce createUserRequest(Map<String,Object> objectMap){
        String uri = config.getBaseUri() + "/cgi-bin/user/create?access_token="+accessToken;
        if (objectMap.containsKey("enable")) {
            String enable = objectMap.get("enable").toString();
            //1代表启动 0禁用，与IAM系统相反
            objectMap.put("enable",enable.equalsIgnoreCase("0") ? 1 : 0);
        }
        String body = HttpRequest.post(uri)
                .header("Content-Type", "application/json")
                .body(JSON.toJSONString(objectMap))
                .execute()
                .body();
        log.debug("createUserRequest responce :{}",body);
        if (StringUtils.isNotBlank(body)) {
            WorkWeixinResponce responce = JSONObject.parseObject(body, WorkWeixinResponce.class);
            if (responce.getErrcode().intValue() == 0) {
                return responce;
            } else {
                return WorkWeixinResponce.error(responce.getErrmsg());
            }
        }
        return WorkWeixinResponce.error("请求失败，无法创建用户。");
    }

    /**
     * 修改企业微信用户
     * 文档地址
     * https://developer.work.weixin.qq.com/document/path/90205
     * @param objectMap
     * @return
     */
    public WorkWeixinResponce updateUserRequest(Map<String,Object> objectMap){
        String uri = config.getBaseUri() + "/cgi-bin/user/update?access_token="+accessToken;
        if (objectMap.containsKey("enable")) {
            String enable = objectMap.get("enable").toString();
            //1代表启动 0禁用，与IAM系统相反
            objectMap.put("enable",enable.equalsIgnoreCase("0") ? 1 : 0);
        }
        String body = HttpRequest.post(uri)
                .header("Content-Type", "application/json")
                .body(JSON.toJSONString(objectMap))
                .execute()
                .body();
        log.debug("updateUserRequest responce :{}",body);
        if (StringUtils.isNotBlank(body)) {
            WorkWeixinResponce responce = JSONObject.parseObject(body, WorkWeixinResponce.class);
            if (responce.getErrcode().intValue() == 0) {
                return responce;
            } else {
                return WorkWeixinResponce.error(responce.getErrmsg());
            }
        }
        return WorkWeixinResponce.error("请求失败，无法修改用户。");
    }


    /**
     * 修改企业微信用户
     * 文档地址
     * https://developer.work.weixin.qq.com/document/path/90198
     * @param userid 用户ID
     * @return
     */
    public WorkWeixinResponce deleteUserRequest(String userid){
        String uri = config.getBaseUri() + "/cgi-bin/user/delete?access_token="+accessToken+"&userid="+userid;
        String body = HttpRequest.get(uri)
                .execute()
                .body();
        log.debug("deleteUserRequest responce :{}",body);
        if (StringUtils.isNotBlank(body)) {
            WorkWeixinResponce responce = JSONObject.parseObject(body, WorkWeixinResponce.class);
            if (responce.getErrcode().intValue() == 0) {
                return responce;
            } else {
                return WorkWeixinResponce.error(responce.getErrmsg());
            }
        }
        return WorkWeixinResponce.error("请求失败，无法删除用户。");
    }
}
