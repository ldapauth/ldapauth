package com.ldapauth.web.contorller;
import com.google.code.kaptcha.Producer;
import java.awt.image.BufferedImage;

import com.ldapauth.authn.jwt.AuthTokenService;
import com.ldapauth.cache.CacheService;
import com.ldapauth.crypto.Base64Utils;
import com.ldapauth.pojo.vo.Result;
import com.ldapauth.vo.ImageCaptchaVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * ImageCaptchaEndpoint  Producer captcha.
 * <p>图片验证码，采用kaptcha生成动态验证码</p>
 *
 * @author Crystal.Sea
 *
 */
@Controller
@Slf4j
public class ImageCaptchaEndpoint {
    /**
     * 验证码生成器
     */
    @Autowired
    Producer captchaProducer;
    @Autowired
    CacheService cacheService;

    /**
     * 认证令牌服务
     */
    @Autowired
    AuthTokenService authTokenService;

    /**
     * captcha image Producer./图片验证码生成
     *
     */
    @GetMapping(value={"/captcha"}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Result<ImageCaptchaVO> captchaHandleRequest(
            @RequestParam(value="captcha",required=false,defaultValue="text") String captchaType,
            @RequestParam(value="state",required=false,defaultValue="state") String state) {
        try {
            String kaptchaText = captchaProducer.createText();
            String kaptchaValue = kaptchaText;
            if (captchaType.equalsIgnoreCase("Arithmetic")) {//算数计算
                Integer minuend = Integer.valueOf(kaptchaText.substring(0, 1));
                Integer subtrahend = Integer.valueOf(kaptchaText.substring(1, 2));
                if (minuend - subtrahend > 0) {
                    kaptchaValue = (minuend - subtrahend ) + "";
                    kaptchaText = minuend + "-" + subtrahend + "=?";
                } else {
                    kaptchaValue = (minuend + subtrahend) + "";
                    kaptchaText = minuend + "+" + subtrahend + "=?";
                }
            }
            String kaptchaKey = "";
            //验证或者生成jwt签名state
            if (StringUtils.isNotBlank(state) &&
                    !state.equalsIgnoreCase("state") &&
                    authTokenService.validateJwtToken(state)) {
            }else {
                state = authTokenService.genRandomJwt();//随机生成state
            }
            //验证码的key =  state 的 jwt id
            kaptchaKey = authTokenService.resolveJWTID(state);
            log.trace("kaptchaKey {} , Captcha Text is {}" ,kaptchaKey, kaptchaValue);
            //存储到缓存
            cacheService.setCacheObject(kaptchaKey,kaptchaValue);
            // create the image with the text，生成BASE64的图片验证码
            BufferedImage bufferedImage = captchaProducer.createImage(kaptchaText);
            String b64Image = Base64Utils.encodeImage(bufferedImage);
            log.trace("b64Image {}" ,b64Image);
            return Result.success(new ImageCaptchaVO(state,b64Image));
        } catch (Exception e) {
            log.error("captcha Producer Error " + e.getMessage());
        }
        return Result.failed("ERROR");
    }


}
