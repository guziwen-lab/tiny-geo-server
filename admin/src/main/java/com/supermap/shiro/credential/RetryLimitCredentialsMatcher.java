package com.supermap.shiro.credential;

import com.supermap.constant.AuthenticationConstant;
import com.supermap.shiro.encoder.PasswordEncoder;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RetryLimitCredentialsMatcher extends DefaultCredentialsMatcher {

    private final ObjectProvider<RedisTemplate<String, String>> redisTemplateProvider;

    @Value("${login.retry.max-retry-count:5}")
    private int maxRetryCount = 5;

    private static final long RETRY_EXPIRE_SECONDS = 1800;

    public RetryLimitCredentialsMatcher(PasswordEncoder passwordEncoder,
                                        ObjectProvider<RedisTemplate<String, String>> redisTemplateProvider) {
        super(passwordEncoder);
        this.redisTemplateProvider = redisTemplateProvider;
    }

    private RedisTemplate<String, String> getRedisTemplate() {
        RedisTemplate<String, String> redisTemplate = redisTemplateProvider.getIfAvailable();
        if (redisTemplate == null)
            throw new IllegalStateException("UserService 未就绪");
        return redisTemplate;
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        String username = (String) token.getPrincipal();
        String key = AuthenticationConstant.LOGIN_RETRY_KEY_PREFIX + username;

        String retryStr = getRedisTemplate().opsForValue().get(key);
        int retryCount = retryStr == null ? 0 : Integer.parseInt(retryStr);
        if (retryCount >= maxRetryCount)
            throw new ExcessiveAttemptsException("账号已锁定，请30分钟后再试");

        boolean matches = super.doCredentialsMatch(token, info);
        if (matches) {
            getRedisTemplate().delete(key);
        } else {
            Long val = getRedisTemplate().opsForValue().increment(key);
            if (val != null && val == 1) {
                getRedisTemplate().expire(key, RETRY_EXPIRE_SECONDS, TimeUnit.SECONDS);
            }
        }
        return matches;
    }

}