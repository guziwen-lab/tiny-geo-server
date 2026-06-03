package com.supermap.shiro.credential;

import com.supermap.shiro.encoder.PasswordEncoder;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;

/**
 * @author gzw
 */
public class DefaultCredentialsMatcher extends SimpleCredentialsMatcher {

    private final PasswordEncoder passwordEncoder;

    public DefaultCredentialsMatcher(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        // 获取用户输入的密码
        Object tokenCredentials = getCredentials(token);
        if (tokenCredentials == null)
            return true;
        String plainPassword = tokenCredentials instanceof char[]
                ? new String((char[]) tokenCredentials)
                : tokenCredentials.toString();

        // 从 AuthenticationInfo 中取已存储的凭证（通常是加密后的密码字符串）
        Object accountCredentials = getCredentials(info);
        if (accountCredentials == null) {
            return false;
        }
        String storedHash = accountCredentials.toString();

        return passwordEncoder.matches(plainPassword, storedHash);
    }

}
