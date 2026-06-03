package com.supermap.shiro.realm;

import com.supermap.modules.sys.entity.UserEntity;
import com.supermap.modules.sys.service.UserService;
import com.supermap.shiro.credential.AllowAllCredentialsMatcher;
import com.supermap.shiro.token.SmsCodeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 */
@Slf4j
@Component
public class SmsRealm extends AuthorizingRealm {

    public static final String REALM_NAME = "smsRealm";

    private final ObjectProvider<UserService> userProvider;

    private final ObjectProvider<RedisTemplate<String, String>> redisTemplateProvider;

    /**
     * 构造函数
     *
     * @param userProvider       UserServiceProvider 防止过早注入一个没有被动态代理的UserService
     */
    public SmsRealm(ObjectProvider<UserService> userProvider,
                    ObjectProvider<RedisTemplate<String, String>> redisTemplateProvider,
                    AllowAllCredentialsMatcher credentialsMatcher) {
        setCredentialsMatcher(credentialsMatcher);
        setCachingEnabled(false);
        setAuthorizationCachingEnabled(false);

        this.userProvider = userProvider;
        this.redisTemplateProvider = redisTemplateProvider;
    }

    private UserService getUserService() {
        UserService svc = userProvider.getIfAvailable();
        if (svc == null) throw new IllegalStateException("UserService 未就绪");
        return svc;
    }

    private RedisTemplate<String, String> getRedisTemplate() {
        RedisTemplate<String, String> template = redisTemplateProvider.getIfAvailable();
        if (template == null) throw new IllegalStateException("RedisTemplate 未就绪");
        return template;
    }

    @Override
    public String getName() {
        return REALM_NAME;
    }

    /**
     * 限定这个realm只能处理RedisToken
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof SmsCodeToken;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        try {
            String phone = (String) authenticationToken.getPrincipal();
            String inputCode = (String) authenticationToken.getCredentials();

            UserEntity userEntity = getUserService().getByUsername(phone);
            if (userEntity == null)
                throw new UnknownAccountException("用户不存在: " + authenticationToken.getPrincipal());

            // TODO 验证短信验证码 from redis

            return new SimpleAuthenticationInfo(phone, null, getName());
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.debug("认证过程出现异常", e);
            throw new AuthenticationException("认证过程出现异常", e);
        }
    }

    /**
     * 只有当需要检测用户权限的时候才会调用此方法，例如checkRole,checkPermission之类的
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

}
