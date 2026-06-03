package com.supermap.shiro.token;

import lombok.AllArgsConstructor;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author gzw
 */
@AllArgsConstructor
public class RedisToken implements AuthenticationToken {

    private final String token;

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

}
