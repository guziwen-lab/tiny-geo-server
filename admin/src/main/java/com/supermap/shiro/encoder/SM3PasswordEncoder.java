package com.supermap.shiro.encoder;

import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.digest.SM3;
import com.supermap.shiro.config.SM3KeyProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 */
@EnableConfigurationProperties(SM3KeyProperties.class)
//@Component
public class SM3PasswordEncoder implements PasswordEncoder {

    private final SM3 sm3;

    public SM3PasswordEncoder(SM3KeyProperties properties) {
        sm3 = SmUtil.sm3WithSalt(properties.getKey().getBytes());
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return sm3.digestHex(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return sm3.digestHex(rawPassword.toString()).equals(encodedPassword);
    }

}
