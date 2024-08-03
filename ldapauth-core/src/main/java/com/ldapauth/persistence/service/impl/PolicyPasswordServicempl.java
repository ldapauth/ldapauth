package com.ldapauth.persistence.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ldapauth.cache.CacheService;
import com.ldapauth.constants.ConstsCacheData;
import com.ldapauth.persistence.mapper.PolicyPasswordMapper;
import com.ldapauth.persistence.service.PolicyPasswordService;
import com.ldapauth.pojo.entity.PolicyLogin;
import com.ldapauth.pojo.entity.PolicyPassword;
import com.ldapauth.pojo.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PolicyPasswordServicempl extends ServiceImpl<PolicyPasswordMapper, PolicyPassword> implements PolicyPasswordService {

    @Autowired
    CacheService cacheService;

    static Pattern patternWhiteSpace = Pattern.compile("\\s");

    @Override
    public PolicyPassword get() {
        List<PolicyPassword> list = super.list();
        if (CollectionUtil.isNotEmpty(list)) {
            return list.get(0);
        }
        PolicyPassword policy = new PolicyPassword();
        policy.setId(1L);
        policy.setMaxLength(20);
        policy.setMinLength(8);
        policy.setExpirationDays(180);
        policy.setIsDigit(1);
        policy.setIsSpecial(1);
        policy.setIsLowerCase(1);
        return policy;
    }

    @Override
    public boolean saveOrUpdate(PolicyPassword entity) {
        boolean flag =  super.saveOrUpdate(entity);
        if(flag) {
            cacheService.deleteObject(ConstsCacheData.POLICY_PASSWORD_KEY);
            getCache();
        }
        return flag;
    }

    @Override
    public PolicyPassword getCache() {
        PolicyPassword policy = cacheService.getCacheObject(ConstsCacheData.POLICY_PASSWORD_KEY);
        if(Objects.isNull(policy)) {
            policy = get();
            cacheService.setCacheObject(ConstsCacheData.POLICY_PASSWORD_KEY,policy);
        }
        return policy;
    }

    @Override
    public Result<String> validatePassword(String password) {
        boolean chineseFlag = containsChineseCharacters(password);
        if (chineseFlag) {
            return Result.failed("密码禁止输入中文字符");
        }

        PolicyPassword cache = getCache();
        if (Objects.nonNull(cache)) {
            int minLength = cache.getMinLength();
            int maxLength = cache.getMaxLength();
            int passwordLength = password.length();
            if (passwordLength < minLength || passwordLength > maxLength) {
                return Result.failed("密码长度需要" + minLength + "~" + maxLength + "位非空字符");
            }

            Matcher matcher = patternWhiteSpace.matcher(password);
            if (matcher.find()) {
                return Result.failed("密码不能包含空格");
            }

            StringBuilder pattern = new StringBuilder();
            List<String> resultMsgList = new ArrayList<>();
            boolean have = false;

            //判断小写字母
            if (1 == cache.getIsLowerCase()){
                have = true;
                pattern.append("(?=.*[a-z])");
                resultMsgList.add("小写字母");
            }

            if (1 == cache.getIsSpecial()){
                have = true;
                pattern.append("(?=.*[!@#$%^&*()_+={}\\[\\]:;<>,.?~\\\\-])");
                resultMsgList.add("特殊字符");
            }
            if (1 == cache.getIsDigit()){
                have = true;
                pattern.append("(?=.*[0-9])");
                resultMsgList.add("数字");
            }
            if (have){
                pattern.append(".*");
                boolean isValid = Pattern.matches(String.valueOf(pattern), password);
                if (!isValid){
                    String result = resultMsgList.stream()
                            .collect(Collectors.joining("、"));
                    return Result.failed("密码至少包含一位" + result);
                }
            }

            return Result.success("密码符合规范");
        }

        return Result.failed("数据库中不存在密码策略信息，请联系管理员添加");
    }

    private boolean containsChineseCharacters(String password) {
        // 正则表达式匹配中文字符
        String chineseRegex = "[\\u4e00-\\u9fa5]";

        Pattern chinesePattern = Pattern.compile(chineseRegex);

        Matcher chineseMatcher = chinesePattern.matcher(password);

        return chineseMatcher.find();
    }
}
