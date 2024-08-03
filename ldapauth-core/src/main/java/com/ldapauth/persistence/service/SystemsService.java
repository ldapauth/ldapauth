package com.ldapauth.persistence.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ldapauth.pojo.entity.Systems;

/**
 * @author Shi.bl
 *
 */
public interface SystemsService extends IService<Systems> {


    Systems get();

}
