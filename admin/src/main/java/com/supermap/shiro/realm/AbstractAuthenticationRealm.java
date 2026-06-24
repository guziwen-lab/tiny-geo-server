package com.supermap.shiro.realm;

import com.supermap.common.util.BeanUtils;
import com.supermap.modules.sys.entity.UserEntity;
import com.supermap.modules.sys.service.UserService;
import com.supermap.shiro.LoginUser;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author gzw
 */
public abstract class AbstractAuthenticationRealm<T extends AuthenticationToken> extends AuthorizingRealm {

    /**
     * UserServiceProvider 防止过早注入一个没有被动态代理的UserService
     */
    @Autowired
    private ObjectProvider<UserService> userProvider;

    @Autowired
    private ObjectProvider<RedisTemplate<String, String>> redisTemplateProvider;

    protected UserService getUserService() {
        UserService svc = userProvider.getIfAvailable();
        if (svc == null) throw new IllegalStateException("UserService 未就绪");
        return svc;
    }

    protected RedisTemplate<String, String> getRedisTemplate() {
        RedisTemplate<String, String> template = redisTemplateProvider.getIfAvailable();
        if (template == null) throw new IllegalStateException("RedisTemplate 未就绪");
        return template;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    /**
     * 子类声明其能够处理的 Token 类型
     */
    protected abstract Class<T> getTokenClass();

    @Override
    public boolean supports(AuthenticationToken token) {
        return getTokenClass().isInstance(token);
    }

    /**
     * 只有当需要检测用户权限的时候才会调用此方法，例如checkRole,checkPermission之类的
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    protected LoginUser buildLoginUser(UserEntity userEntity) {
        LoginUser loginUser = new LoginUser();
        BeanUtils.copyProperties(userEntity, loginUser);

        // 设置权限信息
        getUserService().setLoginUserPermsInfo(loginUser);

        return loginUser;
    }

}
