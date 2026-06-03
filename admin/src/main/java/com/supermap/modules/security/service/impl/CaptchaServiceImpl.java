package com.supermap.modules.security.service.impl;

import com.supermap.constant.AuthenticationConstant;
import com.supermap.modules.security.service.CaptchaService;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Service
public class CaptchaServiceImpl implements CaptchaService {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void create(String uuid, HttpServletResponse response) throws IOException {
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        //生成验证码
        SpecCaptcha captcha = new SpecCaptcha(150, 40);
        captcha.setLen(5);
        captcha.setCharType(Captcha.TYPE_DEFAULT);
        captcha.out(response.getOutputStream());

        //保存到缓存
        setCache(uuid, captcha.text());
    }

    @Override
    public boolean validate(String uuid, String code) {
        //获取验证码
        String captcha = getCache(uuid);
        //效验成功
        return code.equalsIgnoreCase(captcha);
    }

    private void setCache(String uuid, String value) {
        String key = AuthenticationConstant.CAPTCHA_KEY_PREFIX + uuid;
        redisTemplate.opsForValue()
                .set(key, value, AuthenticationConstant.DEFAULT_CAPTCHA_EXPIRE_SECONDS, TimeUnit.SECONDS);
    }

    private String getCache(String uuid) {
        String key = AuthenticationConstant.CAPTCHA_KEY_PREFIX + uuid;
        return redisTemplate.opsForValue().getAndDelete(key);
    }

}