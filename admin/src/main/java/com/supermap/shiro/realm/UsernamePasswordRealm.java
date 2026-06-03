package com.supermap.shiro.realm;

import com.supermap.common.util.StringUtils;
import com.supermap.modules.sys.entity.UserEntity;
import com.supermap.modules.sys.service.UserService;
import com.supermap.shiro.credential.RetryLimitCredentialsMatcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 */
@Slf4j
@Component
public class UsernamePasswordRealm extends AuthorizingRealm {

    public static final String REALM_NAME = "usernamePasswordRealm";

    private final ObjectProvider<UserService> userProvider;

    /**
     * 构造函数
     *
     * @param userProvider       UserServiceProvider 防止过早注入一个没有被动态代理的UserService
     * @param credentialsMatcher 密码匹配器
     */
    public UsernamePasswordRealm(ObjectProvider<UserService> userProvider, RetryLimitCredentialsMatcher credentialsMatcher) {
        setCredentialsMatcher(credentialsMatcher);
        this.userProvider = userProvider;
    }

    private UserService getUserService() {
        UserService svc = userProvider.getIfAvailable();
        if (svc == null) throw new IllegalStateException("UserService 未就绪");
        return svc;
    }

    @Override
    public String getName() {
        return REALM_NAME;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        try {
            String username = (String) authenticationToken.getPrincipal();

            UserEntity userEntity = getUserService().getByUsername(username);
            if (userEntity == null)
                throw new UnknownAccountException("用户不存在: " + username);

            if (StringUtils.isEmpty(userEntity.getPassword()))
                throw new AccountException("用户未设置密码: " + username);

            return new SimpleAuthenticationInfo(username, userEntity.getPassword(), getName());
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
