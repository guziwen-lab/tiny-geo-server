package com.supermap.shiro.realm;

import com.supermap.common.util.StringUtils;
import com.supermap.modules.sys.entity.UserEntity;
import com.supermap.shiro.credential.RetryLimitCredentialsMatcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 */
@Slf4j
@Component
public class UsernamePasswordRealm extends AbstractAuthenticationRealm<UsernamePasswordToken> {

    public UsernamePasswordRealm(RetryLimitCredentialsMatcher credentialsMatcher) {
        setCredentialsMatcher(credentialsMatcher);
    }

    @Override
    protected Class<UsernamePasswordToken> getTokenClass() {
        return UsernamePasswordToken.class;
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

            return new SimpleAuthenticationInfo(buildLoginUser(userEntity), userEntity.getPassword(), getName());
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.debug("认证过程出现异常", e);
            throw new AuthenticationException("认证过程出现异常", e);
        }
    }

}
