package com.supermap.modules.security.service.impl;

import com.supermap.common.util.BeanUtils;
import com.supermap.common.util.IpUtils;
import com.supermap.modules.security.service.LoginService;
import com.supermap.modules.security.vo.RouteVO;
import com.supermap.modules.sys.dto.UserLoginDTO;
import com.supermap.modules.sys.entity.LoginLogEntity;
import com.supermap.modules.sys.entity.PermissionEntity;
import com.supermap.modules.sys.entity.UserEntity;
import com.supermap.modules.sys.service.LoginLogService;
import com.supermap.modules.sys.service.UserService;
import com.supermap.modules.sys.service.impl.PermissionServiceImpl;
import com.supermap.shiro.LoginUser;
import com.supermap.shiro.LoginUserContextHandler;
import com.supermap.shiro.util.RedisTokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

/**
 * @author gzw
 */
@Service
@AllArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final RedisTemplate<String, String> redisTemplate;

    private final PermissionServiceImpl permissionService;

    private final LoginLogService loginLogService;

    private final UserService userService;

    private final RedisTokenUtils redisTokenUtils;

    @Override
    public String login(UserLoginDTO user, HttpServletRequest request) {
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(user.getUsername(), user.getPassword());
        SecurityUtils.getSubject().login(usernamePasswordToken);
        LoginUser loginUser = LoginUserContextHandler.getLoginUser();

        String token = redisTokenUtils.createToken(loginUser);

        // 保存登录日志
        saveLoginLog(request, token, loginUser);

        return token;
    }

    /**
     * 构造 LoginUser
     */
    private LoginUser buildLoginUser(String username) {
        LoginUser loginUser = new LoginUser();
        UserEntity userEntity = userService.getByUsername(username);
        BeanUtils.copyProperties(userEntity, loginUser);

        // 设置权限信息
        userService.setLoginUserPermsInfo(loginUser);

        return loginUser;
    }

    @Override
    public void logout(String token) {
        redisTemplate.delete(redisTokenUtils.getKey(token));
        LoginUserContextHandler.logout();

        LoginLogEntity loginLogEntity = loginLogService.getByToken(token);
        if (loginLogEntity == null)
            return;
        LoginLogEntity update = new LoginLogEntity();
        update.setId(loginLogEntity.getId());
        update.setIsForceLogout(true);
        loginLogService.updateById(update);
    }

    @Override
    public List<RouteVO> getLoginUserRoute(Long userId) {
        List<PermissionEntity> perms = permissionService.getBaseMapper().getLoginUserRoute(userId);
        List<RouteVO> list = perms.stream().map(item -> {
            RouteVO route = new RouteVO();
            route.setPermissionId(item.getPermissionId());
            route.setParentId(item.getParentId());
            route.setPath(item.getUrl());
            route.setName(item.getName());

            route.setTitle(item.getName());
            route.setHidden(item.getHidden() != null && item.getHidden() == 1);
            route.setIcon(item.getIcon());
            route.setOpenStyle(item.getOpenStyle());

            return route;
        }).toList();

        List<RouteVO> root = list.stream()
                .filter(item -> item.getParentId() == null)
                .toList();

        root.forEach(routeVO -> buildRoute(routeVO, list));

        return root;
    }

    private void buildRoute(RouteVO root, List<RouteVO> all) {
        List<RouteVO> children = all.stream()
                .filter(item -> Objects.equals(item.getParentId(), root.getPermissionId()))
                .toList();
        root.setChildren(children);

        children.forEach(item -> buildRoute(item, all));
    }

    private void saveLoginLog(HttpServletRequest request, String token, LoginUser loginUser) {
        LoginLogEntity loginLogEntity = new LoginLogEntity();
        loginLogEntity.setToken(token);
        loginLogEntity.setUserId(loginUser.getUserId());
        loginLogEntity.setLoginTime(new Timestamp(System.currentTimeMillis()));
        loginLogEntity.setIsForceLogout(false);
        String ip = IpUtils.getClientIp(request);
        loginLogEntity.setIp(ip);
        loginLogService.asyncSave(loginLogEntity);
    }

}
