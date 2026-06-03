package com.supermap.modules.sys.service;

import com.supermap.shiro.LoginUser;

/**
 * @author gzw
 */
public interface LoginUserService {

    void refreshLoginUserInfoByUserId(Long userId);

    void refreshLoginUserInfoByRoleId(Long roleId);

    LoginUser refreshLoginUser(Long userId, String token);

}
