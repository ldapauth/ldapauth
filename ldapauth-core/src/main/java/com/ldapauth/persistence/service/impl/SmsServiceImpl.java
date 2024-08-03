package com.ldapauth.persistence.service.impl;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;
import com.ldapauth.enums.SmsBusinessExceptionEnum;
import com.ldapauth.exception.BusinessException;
import com.ldapauth.persistence.service.SmsProviderService;
import com.ldapauth.persistence.service.SmsService;
import com.ldapauth.pojo.dto.SmsAliyunDto;

import com.ldapauth.pojo.entity.SmsProvider;
import com.ldapauth.pojo.vo.Result;

import com.ldapauth.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: orangeBabu
 * @time: 12/7/2024 PM3:05
 */

@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    SmsProviderService smsProviderService;

    @Override
    public Result send(SmsAliyunDto dto) {
        if (StringUtils.isEmpty(dto.getTemplateId())) {
            throw new BusinessException(
                    SmsBusinessExceptionEnum.SMS_REQUIRED_ERROR_TEMPLATE_ID.getCode(),
                    SmsBusinessExceptionEnum.SMS_REQUIRED_ERROR_TEMPLATE_ID.getMsg());
        }

        if (ObjectUtils.isEmpty(dto.getMobile())) {
            throw new BusinessException(
                    SmsBusinessExceptionEnum.SMS_REQUIRED_ERROR_TO.getCode(),
                    SmsBusinessExceptionEnum.SMS_REQUIRED_ERROR_TO.getMsg());
        }

        SmsProvider smsProvider = this.getSmsProvider();

        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", smsProvider.getAppKey(), smsProvider.getAppSecret());
        IAcsClient client = new DefaultAcsClient(profile);


        SendSmsRequest request = new SendSmsRequest();
        request.setSignName(smsProvider.getSignName());
        request.setTemplateCode(dto.getTemplateId());
        request.setPhoneNumbers(dto.getMobile());
        request.setTemplateParam(JsonUtils.toString(dto.getTemplateParam()));
        SendSmsResponse response;
        try {
            log.debug("signName:{},", request.getSignName());
            log.debug("templateCode:{},", request.getTemplateCode());
            log.debug("templateParam:{},", request.getTemplateParam());
            log.debug("phoneNumbers:{},", request.getPhoneNumbers());
            response = client.getAcsResponse(request);
            log.info(new Gson().toJson(response));
            if (response.getCode().equals("OK")) {
                return Result.success(null);
            } else {
                log.error("短信发送失败:" + "signName:{}," + "templateCode:{}," + "templateParam:{}," + "phoneNumbers:{}," + "aliyun error code:{}," + "aliyun error requestId:{}," + "aliyun error message:{}", request.getSignName(), request.getTemplateCode(), request.getTemplateParam(), request.getPhoneNumbers(), response.getCode(), response.getRequestId(), response.getMessage());
                return Result.error(SmsBusinessExceptionEnum.SMS_ALIYUN_ERROR.getCode(), response.getMessage());
            }
        } catch (ServerException e) {
            throw new BusinessException(SmsBusinessExceptionEnum.SMS_ALIYUN_ERROR.getCode(), e.getErrCode() + "_" + e.getErrMsg() + "_" + e.getRequestId());
        } catch (ClientException e) {
            log.error("ErrCode:" + e.getErrCode());
            log.error("ErrMsg:" + e.getErrMsg());
            log.error("RequestId:" + e.getRequestId());
            throw new BusinessException(SmsBusinessExceptionEnum.SMS_ALIYUN_ERROR.getCode(), e.getErrCode() + "_" + e.getErrMsg() + "_" + e.getRequestId());
        }
    }

    /**
     * 从redis加载配置
     *
     * @return
     */
    private SmsProvider getSmsProvider() {
        SmsProvider cnfSmsProvider = smsProviderService.getCurrentSmsProvider();
        if (ObjectUtils.isEmpty(cnfSmsProvider)) {
            throw new BusinessException(SmsBusinessExceptionEnum.NOT_CNF_ERROR.getCode(),
                    SmsBusinessExceptionEnum.NOT_CNF_ERROR.getMsg());
        }
        return cnfSmsProvider;
    }
}
