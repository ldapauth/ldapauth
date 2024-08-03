package com.ldapauth.persistence.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ldapauth.constants.ConstsRoles;
import com.ldapauth.constants.ConstsStatus;
import com.ldapauth.persistence.service.LoginLogService;
import com.ldapauth.pojo.entity.Group;
import com.ldapauth.pojo.entity.LoginLog;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.persistence.service.UserInfoService;
import com.ldapauth.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class LoginRepository {
    private static Logger _logger = LoggerFactory.getLogger(LoginRepository.class);


    protected UserInfoService userInfoService;

    protected LoginLogService loginLogService;

    /**
     * 1 (USERNAME)  2 (USERNAME | MOBILE) 3 (USERNAME | MOBILE | EMAIL)
     */
    public  static  int LOGIN_ATTRIBUTE_TYPE = 2;


    public LoginRepository(){

    }

    public LoginRepository(LoginLogService loginLogService,UserInfoService userInfoService){
        this.userInfoService=userInfoService;
        this.loginLogService = loginLogService;
    }

    public UserInfo find(String username, String password) {
        List<UserInfo> listUserInfo = findByUsername(username,password);
        UserInfo userInfo = null;
        if (CollectionUtil.isNotEmpty(listUserInfo)) {
            userInfo = listUserInfo.get(0);
        }
        _logger.debug("load UserInfo : " + userInfo);
        return userInfo;
    }

    public List<UserInfo> findByUsername(String username, String password) {
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getUsername,username);
    	return userInfoService.list(queryWrapper);
    }


    /**
     * 閿佸畾鐢ㄦ埛锛歩slock锛�1 鐢ㄦ埛瑙ｉ攣 2 鐢ㄦ埛閿佸畾
     *
     * @param userInfo
     */
    public void updateLock(UserInfo userInfo) {
        try {
            if (userInfo != null && Objects.nonNull(userInfo.getId())) {
                LambdaUpdateWrapper<UserInfo> updateWrapper = new LambdaUpdateWrapper<>();
                //update t_userinfo set islocked = ?  , unlocktime = ? where id = ?
                updateWrapper.eq(UserInfo::getId,userInfo.getId());
                updateWrapper.set(UserInfo::getIsLocked,ConstsStatus.LOCK);
                updateWrapper.set(UserInfo::getUnLockTime,new Date());
                userInfoService.update(updateWrapper);
                userInfo.setIsLocked(ConstsStatus.LOCK);
            }
        } catch (Exception e) {
            _logger.error("lockUser Exception",e);
        }
    }

    /**
     *
     * @param userInfo
     */
    public void updateUnlock(UserInfo userInfo) {
        try {
            if (userInfo != null && Objects.nonNull(userInfo.getId())) {
                LambdaUpdateWrapper<UserInfo> updateWrapper = new LambdaUpdateWrapper<>();
                //update t_userinfo set islocked = ? , unlocktime = ? where id = ?
                updateWrapper.eq(UserInfo::getId,userInfo.getId());
                updateWrapper.set(UserInfo::getIsLocked,ConstsStatus.ACTIVE);
                updateWrapper.set(UserInfo::getUnLockTime,new Date());
                userInfoService.update(updateWrapper);
                userInfo.setIsLocked(ConstsStatus.ACTIVE);
            }
        } catch (Exception e) {
            _logger.error("unlockUser Exception",e);
        }
    }

    /**
    * reset BadPasswordCount And Lockout
     *
     * @param userInfo
     */
    public void updateLockout(UserInfo userInfo) {
        try {
            if (userInfo != null && Objects.nonNull(userInfo.getId())) {
                LambdaUpdateWrapper<UserInfo> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(UserInfo::getId,userInfo.getId());
                updateWrapper.set(UserInfo::getBadPasswordCount,0);
                updateWrapper.set(UserInfo::getIsLocked,ConstsStatus.ACTIVE);
                updateWrapper.set(UserInfo::getUnLockTime,new Date());
                userInfoService.update(updateWrapper);

                userInfo.setIsLocked(ConstsStatus.ACTIVE);
            }
        } catch (Exception e) {
            _logger.error("resetBadPasswordCountAndLockout Exception",e);
        }
    }

    /**
     * if login password is error ,BadPasswordCount++ and set bad date
     *
     * @param userInfo
     */
    public void updateBadPasswordCount(UserInfo userInfo) {
        try {
            if (userInfo != null && Objects.nonNull(userInfo.getId())) {
                int badPasswordCount = userInfo.getBadPasswordCount() + 1;
                userInfo.setBadPasswordCount(badPasswordCount);
                //update t_userinfo set badpasswordcount = ? , badpasswordtime = ?  where id = ?
                LambdaUpdateWrapper<UserInfo> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(UserInfo::getId,userInfo.getId());
                updateWrapper.set(UserInfo::getBadPasswordCount,badPasswordCount);
                updateWrapper.set(UserInfo::getBadPasswordTime,new Date());
                userInfoService.update(updateWrapper);
            }
        } catch (Exception e) {
            e.printStackTrace();
            _logger.error(e.getMessage());
        }
    }

    public ArrayList<GrantedAuthority> queryAuthorizedApps(UserInfo userInfo , ArrayList<GrantedAuthority> grantedAuthoritys) {
/*        String grantedAuthorityString="'ROLE_ALL_USER'";
        for(GrantedAuthority grantedAuthority : grantedAuthoritys) {
            grantedAuthorityString += ",'"+ grantedAuthority.getAuthority()+"'";
        }
        String queryAuthorizedAppsSql = String.format(DEFAULT_MYAPPS_SELECT_STATEMENT, grantedAuthorityString,userInfo.getId(),userInfo.getId());
        _logger.debug("Query Authorized Apps SQL {}",queryAuthorizedAppsSql);
        ArrayList<GrantedAuthority> listAuthorizedApps = (ArrayList<GrantedAuthority>) jdbcTemplate.query(
        		queryAuthorizedAppsSql,
                new RowMapper<GrantedAuthority>() {
            public GrantedAuthority mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new SimpleGrantedAuthority(rs.getString("id"));
            }
        });

        _logger.debug("list Authorized Apps  " + listAuthorizedApps);*/
        return new ArrayList<>();
    }


    /**
     * grant Authority by userinfo
     *
     * @param userInfo
     * @return ArrayList<GrantedAuthority>
     */
    public ArrayList<GrantedAuthority> grantAuthority(UserInfo userInfo) {
        ArrayList<GrantedAuthority> grantedAuthority = new ArrayList<>();
        boolean isAdmin = false;
        List<Group> list = userInfoService.getUserGroup(userInfo.getId());
        //提取分组ID
        List<Long> ids = list.stream().map(Group::getId).collect(Collectors.toList());
        if (ids.contains(ConstsRoles.ADMIN_GROUP_ID)) {
            grantedAuthority.add(ConstsRoles.ROLE_ADMINISTRATORS);
            isAdmin = true;
        } else {
            grantedAuthority.add(ConstsRoles.ROLE_USER);
            grantedAuthority.add(ConstsRoles.ROLE_ALL_USER);
        }

        if (isAdmin == false && userInfo.getId() == ConstsRoles.ADMIN_USER_ID) {
            grantedAuthority.add(ConstsRoles.ROLE_ADMINISTRATORS);
        }
        return grantedAuthority;
    }

    public void updateLastLogin(UserInfo userInfo) {
        LambdaUpdateWrapper<UserInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserInfo::getId,userInfo.getId());
        updateWrapper.set(UserInfo::getLastLoginTime,new Date());
        updateWrapper.set(UserInfo::getLastLoginIp,userInfo.getLastLoginIp());
        userInfoService.update(updateWrapper);
    }

    /**
     * 记录登录日志
     * @param historyLogin
     */
    public void history(LoginLog historyLogin) {
        loginLogService.save(historyLogin);
    }

}


