package com.supermap.shiro.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.supermap.common.util.JSON;
import com.supermap.shiro.LoginUser;
import com.supermap.shiro.config.JWTProperties;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Calendar;

@Component
@EnableConfigurationProperties(JWTProperties.class)
@AllArgsConstructor
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class JWTUtils {

    private final JWTProperties jwtProperties;

    /**
     * 生成token
     */
    public String createToken(LoginUser user) {
        Calendar expires = Calendar.getInstance();
        expires.add(Calendar.SECOND, jwtProperties.getExpire());

        return JWT.create()
                .withHeader(jwtProperties.getHeader())
                .withPayload(JSON.toJSONString(user))
                .withExpiresAt(expires.getTime())
                .sign(Algorithm.HMAC256(jwtProperties.getSecret()));
    }

    /**
     * 验证token
     */
    public void verify(String token) {
        getPayload(token);  // 如果验证通过，则不会报错，否则会报错
    }

    /**
     * 获取DecodedJWT
     */
    public DecodedJWT getPayload(String token) {
        return JWT.require(Algorithm.HMAC256(jwtProperties.getSecret())).build().verify(token);
    }

    /**
     * 获取payload并转为LoginUser
     */
    public LoginUser getLoginUser(String token) {
        DecodedJWT jwt = JWT.require(Algorithm.HMAC256(jwtProperties.getSecret())).build().verify(token);
        String payload = jwt.getPayload();

        Base64.Decoder decoder = Base64.getUrlDecoder();
        return JSON.parseObject(new String(decoder.decode(payload)), LoginUser.class);
    }

}

