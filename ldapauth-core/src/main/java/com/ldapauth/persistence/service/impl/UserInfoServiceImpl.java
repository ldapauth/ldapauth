package com.ldapauth.persistence.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ldapauth.cache.CacheService;
import com.ldapauth.constants.ConstsCacheData;
import com.ldapauth.constants.ConstsStatus;
import com.ldapauth.constants.ConstsSynchronizers;
import com.ldapauth.crypto.password.PasswordReciprocal;
import com.ldapauth.enums.OrgsBusinessExceptionEnum;
import com.ldapauth.exception.BusinessCode;
import com.ldapauth.exception.BusinessException;
import com.ldapauth.otp.AbstractOtp;
import com.ldapauth.persistence.mapper.GroupMemberMapper;
import com.ldapauth.persistence.mapper.OrganizationMapper;
import com.ldapauth.persistence.service.*;
import com.ldapauth.pojo.dto.*;
import com.ldapauth.pojo.entity.*;
import com.ldapauth.persistence.mapper.UserInfoMapper;
import com.ldapauth.pojo.vo.Result;
import com.ldapauth.pojo.vo.UserInfoPageVo;
import com.ldapauth.provision.ProvisionAction;
import com.ldapauth.provision.ProvisionService;
import com.ldapauth.provision.ProvisionTopic;
import com.ldapauth.utils.PasswordGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.ldapauth.constants.ConstsStatus.DISABLE_STATUS;
import static com.ldapauth.exception.BusinessCode.*;

@Slf4j
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    OrganizationMapper organizationMapper;

    @Autowired
    GroupMemberMapper groupMemberMapper;

    @Autowired
    ProvisionService provisionService;

    @Autowired
    SynchronizersService synchronizersService;

    @Autowired
    AbstractOtp mobileOtp;

    @Autowired
    SmsService smsService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    PolicyPasswordService policyPasswordService;

    @Autowired
    FileUploadService fileUploadService;

    @Autowired
    IdentifierGenerator identifierGenerator;

    /**
     * 短信注册短信验证码模板id
     */
    @Value("${sms.templateName.forgot}")
    private String forgotSmsTemplate;
    @Autowired
    private CacheService cacheService;


    @Override
    public Page<UserInfoPageVo> fetch(UserInfoQueryDTO userInfoQueryDTO) {
        return userInfoMapper.selectUserPage(userInfoQueryDTO.build(), userInfoQueryDTO);
    }


    @Transactional
    @Override
    public boolean updateStatus(ChangeStatusDTO userInfoChangeStatusDTO, Long updateBy, Date updateTime) {
        LambdaUpdateWrapper<UserInfo> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(UserInfo::getId, userInfoChangeStatusDTO.getId());
        lambdaUpdateWrapper.set(UserInfo::getStatus, userInfoChangeStatusDTO.getStatus());
        lambdaUpdateWrapper.set(UserInfo::getUpdateBy, updateBy);
        lambdaUpdateWrapper.set(UserInfo::getUpdateTime, updateTime);
        return super.update(lambdaUpdateWrapper);
    }

    @Override
    public List<Group> getUserGroup(Long userId) {
        return userInfoMapper.selectUserGroup(userId);
    }

    @Override
    public UserInfo getUserByOpenId(String objectFrom,String openId) {
        LambdaQueryWrapper<UserInfo> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(UserInfo::getOpenId, openId);
        lambdaQueryWrapper.eq(UserInfo::getObjectFrom, objectFrom);
        return super.getOne(lambdaQueryWrapper);
    }

    @Override
    public UserInfo getUserByLdapId(String ldapId) {
        LambdaQueryWrapper<UserInfo> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(UserInfo::getLdapId,ldapId);
        return super.getOne(lambdaQueryWrapper);
    }

    @Override
    public UserInfo getUserByUsername(String username) {
        LambdaQueryWrapper<UserInfo> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(UserInfo::getUsername, username);
        return super.getOne(lambdaQueryWrapper);
    }

    @Override
    public UserInfo getUserByMobile(String mobile) {
        LambdaQueryWrapper<UserInfo> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(UserInfo::getMobile, mobile);
        return super.getOne(lambdaQueryWrapper);
    }

    @Override
    public UserInfo getUserByLdapDn(String ldapDn) {
        LambdaQueryWrapper<UserInfo> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(UserInfo::getLdapDn, ldapDn);
        return super.getOne(lambdaQueryWrapper);
    }

    @Transactional
    @Override
    public boolean save(UserInfo entity) {
        String username = entity.getUsername();
        String email = entity.getEmail();
        String mobile = entity.getMobile();

        if (isUsernameExists(username)) {
            throw new BusinessException(
                    USERNAME_USED.getCode(),
                    USERNAME_USED.getMsg()
            );
        }

        if (StringUtils.isNotBlank(mobile) && isMobileExists(mobile)) {
            throw new BusinessException(
                    MOBILE_USED.getCode(),
                    MOBILE_USED.getMsg()
            );
        }

        if (StringUtils.isNotBlank(email) && isEmailExists(email)) {
            throw new BusinessException(
                    EMAIL_USED.getCode(),
                    EMAIL_USED.getMsg()
            );
        }

        // 设置缺省值
        if (StringUtils.isEmpty(entity.getObjectFrom())) {
            if (synchronizersService.LdapConfig().getStatus() == ConstsStatus.DATA_ACTIVE) {
                entity.setObjectFrom(ConstsSynchronizers.OPEN_LDAP);
            } else {
                entity.setObjectFrom(ConstsSynchronizers.SYSTEM);
            }
        }
        if (StringUtils.isNotBlank(entity.getPassword())) {
            String password = passwordEncoder.encode(entity.getPassword());
            String eecipherable =PasswordReciprocal.getInstance().encode(entity.getPassword());
            entity.setPassword(password);
            entity.setDecipherable(eecipherable);
            entity.setPasswordLastSetTime(new Date());
        }
        boolean flag = super.save(entity);
        if (flag) {
            provisionService.send(
                    ProvisionTopic.USERINFO_TOPIC,
                    entity,
                    ProvisionAction.CREATE_ACTION);
        }
        return flag;
    }

    /**
     * @Description: 编辑用户手机号码时检查是否重复
     * @Param: [mobile, id]
     */
    private void checkMobileDuplicateWhenEdit(String mobile, Long id) {
        if (StringUtils.isNotBlank(mobile)) {
            List<UserInfo> userInfos = super.list(Wrappers.<UserInfo>lambdaQuery()
                    .eq(UserInfo::getMobile, mobile)
                    .ne(UserInfo::getId, id));
            if (StringUtils.isNotBlank(mobile) && ObjectUtils.isNotEmpty(userInfos)) {
                throw new BusinessException(
                        400,
                        "系统检测，当前手机号码已存在绑定关系。"
                );
            }
        }
    }

    /**
     * @Description: 编辑用户邮箱时检查是否重复
     * @Param: [email, id]
     */
    private void checkEmailDuplicateWhenEdit(String email, Long id) {
        if (StringUtils.isNotBlank(email)) {
            List<UserInfo> userInfos = super.list(Wrappers.<UserInfo>lambdaQuery()
                    .eq(UserInfo::getEmail, email)
                    .ne(UserInfo::getId, id));
            if (StringUtils.isNotBlank(email) && ObjectUtils.isNotEmpty(userInfos)) {
                throw new BusinessException(
                        400,
                        "系统检测，当前邮箱地址已存在绑定关系。"
                );
            }
        }
    }

    @Transactional
    @Override
    public boolean updateById(UserInfo entity) {
        String mobile = entity.getMobile();
        String email = entity.getEmail();
        Long id = entity.getId();

        checkMobileDuplicateWhenEdit(mobile, id);
        checkEmailDuplicateWhenEdit(email, id);

        boolean flag = super.updateById(entity);
        if (flag) {
            provisionService.send(
                    ProvisionTopic.USERINFO_TOPIC,
                    entity,
                    ProvisionAction.UPDATE_ACTION);
        }
        return flag;

    }

    @Override
    @Transactional
    public Result<String> deleteBatch(IdsListDto idsListDto) {
        List<Long> ids = idsListDto.getIds();

        List<UserInfo> userInfoList = super.list(Wrappers.<UserInfo>lambdaQuery()
                .in(UserInfo::getId, ids));

        //判断删除的用户是否已被禁用
        for (UserInfo userInfo : userInfoList) {
            if (userInfo.getStatus() == 0) {
                throw new BusinessException(
                        OrgsBusinessExceptionEnum.CURRENT_USERS_ACTIVE.getCode(),
                        OrgsBusinessExceptionEnum.CURRENT_USERS_ACTIVE.getMsg()
                );
            }
        }


        boolean result = super.removeBatchByIds(ids);
        if (result) {
            //移除分组和用户的关系
            groupMemberMapper.delete(Wrappers.<GroupMember>lambdaQuery()
                    .in(GroupMember::getMemberId, ids));
        }

        if (result) {
            for (UserInfo userInfo : userInfoList) {
                provisionService.send(
                        ProvisionTopic.USERINFO_TOPIC,
                        userInfo,
                        ProvisionAction.DELETE_ACTION);
            }
        }

        return result ? Result.success("删除成功") : Result.failed("删除失败");
    }

    @Override
    public Result<String> disableBatch(IdsListDto idsListDto, Long updateBy, Date updateTime) {
        List<Long> ids = idsListDto.getIds();

        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(UserInfo::getId, ids);
        UserInfo userInfo = new UserInfo();
        userInfo.setStatus(1);
        userInfo.setUpdateBy(updateBy);
        userInfo.setUpdateTime(updateTime);

        boolean result = super.update(userInfo, wrapper);
        return result ? Result.success("禁用成功") : Result.failed("禁用失败");
    }

    @Override
    public Result<String> activeBatch(IdsListDto idsListDto, Long updateBy, Date updateTime) {
        List<Long> ids = idsListDto.getIds();

        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(UserInfo::getId, ids);
        UserInfo userInfo = new UserInfo();
        userInfo.setStatus(0);
        userInfo.setUpdateBy(updateBy);
        userInfo.setUpdateTime(updateTime);

        boolean result = super.update(userInfo, wrapper);
        return result ? Result.success("启用成功") : Result.failed("启用失败");
    }

    @Override
    public boolean updateLdapDnAndLdapIdById(Long id, String ldapId, String ldapDn) {
        LambdaUpdateWrapper<UserInfo> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(UserInfo::getLdapDn, ldapDn);
        if (StringUtils.isNotEmpty(ldapId)) {
            lambdaUpdateWrapper.set(UserInfo::getLdapId, ldapId);
        }
        lambdaUpdateWrapper.set(UserInfo::getUpdateTime, new Date());
        lambdaUpdateWrapper.eq(UserInfo::getId, id);
        return super.update(lambdaUpdateWrapper);
    }

    @Override
    public Result<String> sendCode(SmsCodeDto dto) {
        String mobile = dto.getMobile();
        String otpType = dto.getOtpType();

        String produce = "";
        SmsAliyunDto smsAliyunDto = new SmsAliyunDto();

        if (Objects.equals(otpType, AbstractOtp.OtpMsgTypes.FORGOT)) {
            UserInfo userInfo = super.getOne(Wrappers.<UserInfo>lambdaQuery()
                    .eq(UserInfo::getMobile, mobile));

            //判断该手机号码是否已绑定账号
            if (Objects.isNull(userInfo)) {
                throw new BusinessException(
                        BusinessCode.USER_VERIFY_MOBILE_ABSENT.getCode(),
                        BusinessCode.USER_VERIFY_MOBILE_ABSENT.getMsg()
                );
            }

            //判断账号是否被禁用
            if (DISABLE_STATUS.equals(userInfo.getStatus())) {
                throw new BusinessException(
                        BusinessCode.USER_FORBIDDEN.getCode(),
                        BusinessCode.USER_FORBIDDEN.getMsg()
                );
            }

            produce = mobileOtp.produce(mobile, AbstractOtp.OtpMsgTypes.FORGOT);
            Map<String, String> templateParam = new HashMap<>();
            templateParam.put("code", produce);
//            templateParam.put("expire_time", "5");
            smsAliyunDto.setMobile(mobile);
            smsAliyunDto.setTemplateParam(templateParam);
            smsAliyunDto.setTemplateId(forgotSmsTemplate);
        }

        //调用aliyun发送短信功能
        Result result = smsService.send(smsAliyunDto);

        if (result.getCode() != 200) {
            return Result.failed(result.getMessage());
        }

        return Result.success("发送成功");
    }

    /**
     * @Description: 检查用户名称是否被用户绑定
     * @Param: [username]
     */
    boolean isUsernameExists(String username) {
        List<UserInfo> userInfos = userInfoMapper.checkInfoByUsername(username);
        return ObjectUtils.isNotEmpty(userInfos);
    }

    /**
     * @Description: 检查手机号是否被用户绑定
     * @Param: [mobile]
     */
    boolean isMobileExists(String mobile) {
        List<UserInfo> userInfos = userInfoMapper.checkInfoByMobile(mobile);
        return ObjectUtils.isNotEmpty(userInfos);
    }

    /**
     * @Description: 检查邮箱否被用户绑定
     * @Param: [mobile]
     */
    boolean isEmailExists(String email) {
        List<UserInfo> userInfos = userInfoMapper.checkInfoByEmail(email);
        return ObjectUtils.isNotEmpty(userInfos);
    }

    @Override
    public Result<String> validate(MobileValidateDto dto) {
        String otpType = dto.getOtpType();
        String mobile = dto.getMobile();
        String smsCode = dto.getMobileOtp();

        boolean validate = mobileOtp.validate(mobile, smsCode, otpType);
        if (!validate) {
            return Result.failed("短信验证码错误");
        }

        UserInfo userInfo = super.getOne(Wrappers.<UserInfo>lambdaQuery()
                .eq(UserInfo::getMobile, mobile));

        //判断该手机号码是否已绑定账号
        if (Objects.isNull(userInfo)) {
            throw new BusinessException(
                    BusinessCode.USER_VERIFY_MOBILE_ABSENT.getCode(),
                    BusinessCode.USER_VERIFY_MOBILE_ABSENT.getMsg()
            );
        }

        //判断账号是否被禁用
        if (DISABLE_STATUS.equals(userInfo.getStatus())) {
            throw new BusinessException(
                    BusinessCode.USER_FORBIDDEN.getCode(),
                    BusinessCode.USER_FORBIDDEN.getMsg()
            );
        }

        Long tempId = identifierGenerator.nextId("PK").longValue();
        //临时ID设置redis,缓存2小时
        cacheService.setCacheObject(ConstsCacheData.PWD_FIRST_STEP_VALIDATE + tempId, String.valueOf(tempId), 2, TimeUnit.HOURS);

        return Result.success(String.valueOf(tempId));
    }

    @Override
    public Result<String> setNewPassword(PwdSetDto dto) {
        String password = dto.getPassword();
        String mobile = dto.getMobile();
        UserInfo userInfo = super.getOne(Wrappers.<UserInfo>lambdaQuery()
                .eq(UserInfo::getMobile, mobile));
        if (Objects.isNull(userInfo)) {
            throw new BusinessException(
                    BusinessCode.USER_VERIFY_MOBILE_ABSENT.getCode(),
                    BusinessCode.USER_VERIFY_MOBILE_ABSENT.getMsg()
            );
        }

        //加密后的新密码
        // 判断原始密码和新密码是否相同
        if (passwordEncoder.matches(password, userInfo.getPassword())) {
            return Result.failed("新密码不能与上一个旧密码相同");
        }

        //判断密码是否符合策略
        Result<String> result = policyPasswordService.validatePassword(password);
        if (result.getCode() != 200) {
            return Result.failed(result.getMessage());
        }

        String redisTempKey = ConstsCacheData.PWD_FIRST_STEP_VALIDATE + dto.getTempId();
        String redisValue = cacheService.getCacheObject(redisTempKey);
        if (StringUtils.isBlank(redisValue)) {
            return Result.failed("非法操作, 请先进行手机号码验证!");
        }
        cacheService.deleteObject(redisTempKey);


        //修改登录密码
        LambdaUpdateWrapper<UserInfo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserInfo::getId,userInfo.getId());
        wrapper.set(UserInfo::getPassword,passwordEncoder.encode(password));
        wrapper.set(UserInfo::getDecipherable,PasswordReciprocal.getInstance().encode(password));
        wrapper.set(UserInfo::getPasswordLastSetTime,new Date());
        boolean resultd = super.update(wrapper);
        if (resultd) {
            return Result.success("密码修改成功");
        } else {
            return Result.failed("密码修改失败");
        }
    }


    @Override
    public Result<String> updateAvatar(MultipartFile file, UserInfo userInfo) {
        if (file.isEmpty()) {
            return Result.failed("缺少上传对象");
        }

        if (userInfo == null) {
            return Result.failed("获取不到当前用户");
        }

        try {
            FileUpload fileUpload = createFileUpload(file, userInfo.getId());
            boolean save = fileUploadService.save(fileUpload);

            if (save) {
                String avatarUrl = fileUploadService.generateImageUrl(fileUpload.getId());
                boolean result = super.update(Wrappers.<UserInfo>lambdaUpdate()
                        .set(UserInfo::getAvatar, avatarUrl)
                        .eq(UserInfo::getId, userInfo.getId()));
                return result ? Result.success(avatarUrl) : Result.failed("修改失败");
            }
        } catch (IOException e) {
            log.error("FileUpload IOException", e);
        }

        return Result.failed("上传失败");
    }

    private FileUpload createFileUpload(MultipartFile file, Long userId) throws IOException {
        FileUpload fileUpload = new FileUpload();
        fileUpload.setContentType(file.getContentType());
        fileUpload.setFileName(file.getOriginalFilename());
        fileUpload.setContentSize(file.getSize());
        fileUpload.setUploaded(file.getBytes());
        fileUpload.setCreateTime(new Date());
        fileUpload.setCreateBy(userId);
        return fileUpload;
    }

    @Override
    public Result<String> updateProfile(ProfileUpdateDto dto, UserInfo currentUser) {
        String mobile = dto.getMobile();
        String email = dto.getEmail();
        Long id = currentUser.getId();

        checkMobileDuplicateWhenEdit(mobile, id);
        checkEmailDuplicateWhenEdit(email, id);

        UserInfo userInfo = BeanUtil.copyProperties(dto, UserInfo.class);
        userInfo.setUpdateBy(id);
        userInfo.setUpdateTime(new Date());
        userInfo.setId(id);
        boolean flag = super.updateById(userInfo);
        if (flag) {
            UserInfo entity = super.getById(id);
            provisionService.send(
                    ProvisionTopic.USERINFO_TOPIC,
                    entity,
                    ProvisionAction.UPDATE_ACTION);
        }
        return  flag ? Result.success("修改成功") : Result.failed("修改失败");
    }

    @Override
    public Result<String> updatePassword(PwdUpdateDto dto, UserInfo currentUser) {
        String oldPassword = dto.getOldPassword();
        String newPassword = dto.getNewPassword();
        String dbPassword = currentUser.getPassword();
        if (newPassword.equals(oldPassword)) {
            return Result.failed("新密码不能与旧密码相同");
        }
        if (currentUser.getObjectFrom().equalsIgnoreCase(ConstsSynchronizers.SYSTEM)) {
            if (!passwordEncoder.matches(oldPassword, dbPassword)) {
                return Result.failed("原密码错误");
            }
        }
        //判断密码是否符合策略
        Result<String> result = policyPasswordService.validatePassword(newPassword);
        if (result.getCode() != 200) {
            return Result.failed(result.getMessage());
        }
        //修改登录密码
        LambdaUpdateWrapper<UserInfo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserInfo::getId,currentUser.getId());
        wrapper.set(UserInfo::getPassword,passwordEncoder.encode(newPassword));
        wrapper.set(UserInfo::getDecipherable,PasswordReciprocal.getInstance().encode(newPassword));
        wrapper.set(UserInfo::getPasswordLastSetTime,new Date());
        boolean resultd = super.update(wrapper);
        if (resultd) {
            UserInfo userInfo = super.getById(currentUser.getId());
            provisionService.send(
                    ProvisionTopic.USERINFO_TOPIC,
                    userInfo,
                    ProvisionAction.PASSWORD_ACTION);
            return Result.success("密码修改成功");
        }
        return Result.success("密码修改失败");
    }

    @Override
    public Result<String> resetPassword(Long id) {
        PolicyPassword policyPassword = policyPasswordService.getCache();
        if (Objects.isNull(policyPassword)) {
            return Result.failed("数据库中不存在密码策略信息，请联系管理员添加");
        }

        //根据密码策略生成密码
        String randomPassword =
                PasswordGenerator.generatePassword(policyPassword.getMinLength(), policyPassword.getMaxLength(),
                        policyPassword.getIsLowerCase(),
                        policyPassword.getIsSpecial(),
                        policyPassword.getIsDigit());
        LambdaUpdateWrapper<UserInfo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserInfo::getId, id);
        wrapper.set(UserInfo::getPassword,passwordEncoder.encode(randomPassword));
        wrapper.set(UserInfo::getDecipherable,PasswordReciprocal.getInstance().encode(randomPassword));
        wrapper.set(UserInfo::getPasswordLastSetTime,new Date());
        boolean resetPwdResult = super.update(wrapper);
        if (resetPwdResult) {
           UserInfo userInfo = super.getById(id);
            provisionService.send(
                    ProvisionTopic.USERINFO_TOPIC,
                    userInfo,
                    ProvisionAction.PASSWORD_ACTION);
            return Result.success(randomPassword);
        }
        return Result.failed("重置密码失败");
    }
}

