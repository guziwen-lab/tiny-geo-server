package com.supermap.modules.sys.service.impl;

import com.supermap.common.util.BeanUtils;
import com.supermap.modules.sys.entity.LoginLogEntity;
import com.supermap.modules.sys.entity.UserEntity;
import com.supermap.modules.sys.service.LoginLogService;
import com.supermap.modules.sys.service.UserService;
import com.supermap.modules.sys.service.LoginUserService;
import com.supermap.shiro.LoginUser;
import com.supermap.shiro.LoginUserContextHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gzw
 */
@Service
public class LoginUserServiceImpl implements LoginUserService {

    private UserService userService;

    @Autowired
    @Lazy
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    private final LoginLogService loginLogService;

    public LoginUserServiceImpl(LoginLogService loginLogService) {
        this.loginLogService = loginLogService;
    }

    @Override
    public void refreshLoginUserInfoByUserId(Long userId) {
        List<LoginLogEntity> loginLogEntities = loginLogService.getOnlineByUserId(userId);
        for (LoginLogEntity loginLogEntity : loginLogEntities) {
            refreshLoginUser(loginLogEntity.getUserId(), loginLogEntity.getToken());
        }
    }

    @Override
    public void refreshLoginUserInfoByRoleId(Long roleId) {
        List<LoginLogEntity> loginLogEntities = loginLogService.getOnlineByRoleId(roleId);
        for (LoginLogEntity loginLogEntity : loginLogEntities) {
            refreshLoginUser(loginLogEntity.getUserId(), loginLogEntity.getToken());
        }
    }

    @Override
    public LoginUser refreshLoginUser(Long userId, String token) {
        LoginUser loginUser = new LoginUser();
        UserEntity userEntity = userService.getById(userId);
        BeanUtils.copyProperties(userEntity, loginUser);
        userService.setLoginUserPermsInfo(loginUser);
        loginUser.setToken(token);

        LoginUserContextHandler.refreshLoginUser(loginUser);

        // 更新登录时间
        loginLogService.updateLoginTimeByToken(token);

        return loginUser;
    }

}
