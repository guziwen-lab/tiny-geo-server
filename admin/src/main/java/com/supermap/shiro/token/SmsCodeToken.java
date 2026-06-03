package com.supermap.shiro.token;

import lombok.AllArgsConstructor;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author gzw
 */
@AllArgsConstructor
public class SmsCodeToken implements AuthenticationToken {

    private final String phoneNumber;

    private final String verificationCode;

    @Override
    public Object getPrincipal() {
        return phoneNumber;
    }

    @Override
    public Object getCredentials() {
        return verificationCode;
    }

}
