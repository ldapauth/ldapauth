package com.ldapauth.persistence.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ldapauth.pojo.entity.FileUpload;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.pojo.vo.FileUploadVo;
import com.ldapauth.pojo.vo.Result;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface FileUploadService extends IService<FileUpload> {
    Result<String> upload(MultipartFile fileUpload, UserInfo currentUser);

    void previewImage(Long id, HttpServletResponse response) throws IOException;

    String generateImageUrl(Long id);

    Result<String> uploadFile(MultipartFile fileUpload, UserInfo currentUser);

}
