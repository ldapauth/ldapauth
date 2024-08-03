package com.ldapauth.web.contorller;

import com.ldapauth.authn.web.AuthorizationUtils;
import com.ldapauth.persistence.service.FileUploadService;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.pojo.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @description:
 * @author: orangeBabu
 * @time: 4/7/2024 PM2:36
 */

@Tag(
        name = "文件服务",
        description = "文件上传"
)
@RestController
@RequestMapping(value = { "/file" })
@Slf4j
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @Operation(summary = "图片上传", description = "返回结果", security = {@SecurityRequirement(name = "Authorization")})
    @ApiResponse(responseCode = "200", description = "成功")
    @PostMapping("/upload")
    public Result<String> upload(@RequestPart("uploadFile") MultipartFile file) {
        UserInfo currentUser = AuthorizationUtils.getUserInfo();
        return fileUploadService.upload(file, currentUser);
    }

    @GetMapping("/preview/{id}")
    public void previewImage(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
        fileUploadService.previewImage(id, response);
    }
}
