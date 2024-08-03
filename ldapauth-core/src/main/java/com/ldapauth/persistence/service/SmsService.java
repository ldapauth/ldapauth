package com.ldapauth.persistence.service;

import com.ldapauth.pojo.dto.SmsAliyunDto;
import com.ldapauth.pojo.vo.Result;

public interface SmsService {

    Result send(SmsAliyunDto dto);

}
