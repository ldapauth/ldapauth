package com.ldapauth.persistence.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ldapauth.pojo.dto.IdsListDto;
import com.ldapauth.pojo.entity.SmsProvider;
import com.ldapauth.pojo.vo.Result;

public interface SmsProviderService extends IService<SmsProvider> {

    Result<String> add(SmsProvider smsProvider);

    Result<String> edit(SmsProvider smsProvider);

    Result<SmsProvider> get(Long id);

    SmsProvider getCurrentSmsProvider();

    Result<String> delete(IdsListDto idsListDto);

    Result<String> disable(Long id);

    Result<String> active(Long id);
}
