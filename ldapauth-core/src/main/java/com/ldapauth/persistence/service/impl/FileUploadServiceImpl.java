package com.ldapauth.persistence.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ldapauth.autoconfigure.MvcAutoConfiguration;
import com.ldapauth.persistence.mapper.FileUploadMapper;
import com.ldapauth.persistence.mapper.GroupMemberMapper;
import com.ldapauth.persistence.service.FileUploadService;
import com.ldapauth.persistence.service.GroupMemberService;
import com.ldapauth.pojo.entity.FileUpload;
import com.ldapauth.pojo.entity.GroupMember;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.pojo.vo.FileUploadVo;
import com.ldapauth.pojo.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

/**
 * @description:
 * @author: orangeBabu
 * @time: 4/7/2024 PM2:50
 */

@Slf4j
@Service
public class FileUploadServiceImpl extends ServiceImpl<FileUploadMapper, FileUpload> implements FileUploadService {

    @Autowired
    private MvcAutoConfiguration mvcAutoConfiguration;

    @Override
    public Result<String> upload(MultipartFile file, UserInfo currentUser) {
        if (Objects.nonNull(file)) {
            try {
                FileUpload fileUpload = new FileUpload();
                fileUpload.setContentType(file.getContentType());
                fileUpload.setFileName(file.getOriginalFilename());
                fileUpload.setContentSize(file.getSize());
                fileUpload.setUploaded(file.getBytes());
                fileUpload.setCreateTime(new Date());
                if (Objects.nonNull(currentUser)) {
                    fileUpload.setCreateBy(currentUser.getId());
                }
                boolean save = super.save(fileUpload);
                if (save) {
                    return Result.success(generateImageUrl(fileUpload.getId()));
                }
                return Result.failed("上传失败");
            } catch (IOException e) {
                log.error("FileUpload IOException",e);
            }
        }
        return Result.failed("缺少上传对象");
    }

    @Override
    public String generateImageUrl(Long id) {
        FileUpload fileUpload = super.getById(id);
      /*  // 将java.util.Date转换为LocalDateTime
        LocalDateTime localDateTime = fileUpload.getCreateTime().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        // 获取年份、月份和日期
        int year = localDateTime.getYear();
        int month = localDateTime.getMonthValue();
        int day = localDateTime.getDayOfMonth();*/
        // 拼接URL
        return String.format("/%s/%s", "file/preview" , id);
    }

    @Override
    public void previewImage(Long id, HttpServletResponse response) throws IOException{
        FileUpload fileUpload = super.getById(id);
        if (Objects.nonNull(fileUpload)) {
            byte[] uploaded = fileUpload.getUploaded();
            if (ObjectUtils.isNotEmpty(uploaded)) {
                // 设置响应内容类型
                response.setContentType(fileUpload.getContentType());
                // 设置响应长度
                response.setContentLength(uploaded.length);

                // 将字节数组写入响应输出流
                try (OutputStream os = response.getOutputStream()) {
                    os.write(uploaded);
                }
            }
        }
    }
}
