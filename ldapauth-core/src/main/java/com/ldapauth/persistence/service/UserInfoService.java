package com.ldapauth.persistence.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ldapauth.pojo.dto.*;
import com.ldapauth.pojo.entity.Group;
import com.ldapauth.pojo.entity.Organization;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.pojo.vo.Result;
import com.ldapauth.pojo.vo.UserInfoPageVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;


/**
 * @author Shi.bl
 *
 */
public interface UserInfoService extends IService<UserInfo> {

    /**
     * 分页查询方法
     * @param userInfoQueryDTO 分页查询参数
     * @return
     */
    Page<UserInfoPageVo> fetch(UserInfoQueryDTO userInfoQueryDTO);


    /**
     * 修改用户状态
     * @param userInfoChangeStatusDTO
     * @param updateBy 修改人
     * @param updateTime 修改时间
     * @return
     */
    boolean updateStatus(ChangeStatusDTO userInfoChangeStatusDTO, Long updateBy, Date updateTime);

    /**
     * 获取用户分组集合
     * @param userId
     * @return
     */
    List<Group> getUserGroup(Long userId);

    /**
     * 根据开放ID查询对象
     * @param objectFrom 来源
     * @param openId 开放ID
     * @return
     */
    UserInfo getUserByOpenId(String objectFrom,String openId);

    /**
     * 根据开放ID查询对象
     * @param ldapId
     * @return
     */
    UserInfo getUserByLdapId(String ldapId);


    /**
     * 根据登录账号查询对象
     * @param username 登录账号
     * @return
     */
    UserInfo getUserByUsername(String username);

    /**
     * 根据登录账号查询对象
     * @param mobile 手机号码
     * @return
     */
    UserInfo getUserByMobile(String mobile);

    /**
     * 根据ldapDn查询对象
     * @param ldapDn
     * @return
     */
    UserInfo getUserByLdapDn(String ldapDn);

    /**
     * @Description:
     * @Param: [idsListDto]
     * @return: com.ldapauth.pojo.vo.Result<java.lang.String>
     */
    Result<String> deleteBatch(IdsListDto idsListDto);

    /**
     * @Description: 禁用/批量禁用
     * @Param: [idsListDto, updateBy, updateTime]
     * @return: com.ldapauth.pojo.vo.Result<java.lang.String>
     */
    Result<String> disableBatch(IdsListDto idsListDto, Long updateBy, Date updateTime);

    /**
     * @Description: 启用/批量启用
     * @Param: [idsListDto, updateBy, updateTime]
     * @return: com.ldapauth.pojo.vo.Result<java.lang.String>
     */
    Result<String> activeBatch(IdsListDto idsListDto, Long updateBy, Date updateTime);


    /**
     * 修改ldapdn
     * @param id
     * @param ldapId
     * @param ldapDn
     * @return
     */
    boolean updateLdapDnAndLdapIdById(Long id,String ldapId,String ldapDn);

    /**
     * @Description: 发送验证码
     * @Param: [dto]
     */
    Result<String> sendCode(SmsCodeDto dto);

    /**
     * @Description: 验证码验证
     * @Param: [dto]
     */
    Result<String> validate(MobileValidateDto dto);

    /**
     * @Description: 找回密码-设置密码
     * @Param: [dto]
     */
    Result<String> setNewPassword(PwdSetDto dto);

    /**
     * @Description: 修改用户头像
     * @Param: [file, userInfo]
     */
    Result<String> updateAvatar(MultipartFile file, UserInfo userInfo);

    /**
     * @Description: 修改个人信息
     * @Param: [profileUpdateDto]
     */
    Result<String> updateProfile(ProfileUpdateDto profileUpdateDto, UserInfo currentUser);

    /**
     * @Description: 修改用户密码
     * @Param: [dto]
     */
    Result<String> updatePassword(PwdUpdateDto dto, UserInfo currentUser);

    /**
     * @Description: 重置用户密码
     * @Param: [id]
     */
    Result<String> resetPassword(Long id);

    /**
     * 修改ldapdn
     * @param ldapId
     * @param ldapDn
     * @return
     */
    boolean updateLdapDnAndByLdapId(String ldapId,String ldapDn);
}
