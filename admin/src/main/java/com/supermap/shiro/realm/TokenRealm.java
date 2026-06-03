package com.supermap.shiro.realm;

import com.supermap.shiro.LoginUser;
import com.supermap.shiro.credential.AllowAllCredentialsMatcher;
import com.supermap.shiro.token.RedisToken;
import com.supermap.shiro.util.RedisTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 */
@Slf4j
@Component
public class TokenRealm extends AuthorizingRealm {

    public static final String REALM_NAME = "tokenRealm";

    private final ObjectProvider<RedisTokenUtils> redisTokenUtilsProvider;

    public TokenRealm(ObjectProvider<RedisTokenUtils> redisTokenUtilsProvider, AllowAllCredentialsMatcher credentialsMatcher) {
        setCredentialsMatcher(credentialsMatcher);
        this.redisTokenUtilsProvider = redisTokenUtilsProvider;
    }

    private RedisTokenUtils getRedisTokenUtils() {
        RedisTokenUtils redisTokenUtils = redisTokenUtilsProvider.getIfAvailable();
        if (redisTokenUtils == null)
            throw new IllegalStateException("RedisTokenUtils 未就绪");
        return redisTokenUtils;
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
        return token instanceof RedisToken;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        try {
            String token = (String) authenticationToken.getPrincipal();
            try {
                LoginUser loginUser = getRedisTokenUtils().getLoginUser(token);
                return new SimpleAuthenticationInfo(loginUser, token, getName());
            } catch (Exception e) {
                throw new ExpiredCredentialsException(e.getMessage());
            }
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
        try {
            LoginUser loginUser = (LoginUser) principalCollection.getPrimaryPrincipal();
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            info.setRoles(loginUser.getRoles());
            info.setStringPermissions(loginUser.getStringPermissions());
            return info;
        } catch (Exception e) {
            return null;
        }
    }

}
