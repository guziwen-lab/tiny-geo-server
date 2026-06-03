package com.supermap.shiro.encoder;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * BCrypt 密码加密器
 *
 * @author gzw
 */
public class BCryptPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return BCrypt.withDefaults().hashToString(12, rawPassword.toString().toCharArray());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        BCrypt.Result result = BCrypt.verifyer().verify(rawPassword.toString().toCharArray(), encodedPassword);
        return result.verified;
    }

}
