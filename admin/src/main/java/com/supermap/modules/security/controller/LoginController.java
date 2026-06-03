package com.supermap.modules.security.controller;

import com.supermap.common.enumeration.BizCodeEnum;
import com.supermap.common.pojo.R;
import com.supermap.common.util.StringUtils;
import com.supermap.modules.security.service.CaptchaService;
import com.supermap.modules.security.service.LoginService;
import com.supermap.modules.security.vo.RouteVO;
import com.supermap.modules.sys.dto.UserLoginDTO;
import com.supermap.modules.sys.service.LoginUserService;
import com.supermap.shiro.LoginUser;
import com.supermap.shiro.LoginUserContextHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * @author gzw
 */
@RestController
@Tag(name = "登录管理")
@AllArgsConstructor
public class LoginController {

    private final LoginService loginService;

    private final LoginUserService loginUserService;

    private final CaptchaService captchaService;

    @GetMapping("captcha")
    @Operation(summary = "验证码")
    public void captcha(@RequestParam String uuid, HttpServletResponse response) throws IOException {
        captchaService.create(uuid, response);
    }

    @Operation(summary = "登录")
    @PostMapping(value = "/login")
    public R<String> login(UserLoginDTO user, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isEmpty(user.getUsername()) ||
                StringUtils.isEmpty(user.getPassword()) ||
                StringUtils.isEmpty(user.getCaptcha()) ||
                StringUtils.isEmpty(user.getUuid())) {
            return R.error(BizCodeEnum.VALID_PARAM_EXCEPTION);
        }

        boolean flag = captchaService.validate(user.getUuid(), user.getCaptcha());
        if (!flag)
            return R.error(BizCodeEnum.CAPTCHA_ERROR);

        String token = loginService.login(user, request);

        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);

        return R.ok(token);
    }

    @Operation(summary = "退出登录")
    @GetMapping("/logout")
    public R<Void> logout() {
        LoginUser loginUser = LoginUserContextHandler.getLoginUser();
        loginService.logout(loginUser.getToken());
        return R.ok();
    }

    @Operation(summary = "登录用户信息")
    @GetMapping("/info")
    public R<LoginUser> getLoginUserInfo() {
        LoginUser loginUser = LoginUserContextHandler.getLoginUser();
        loginUser = loginUserService.refreshLoginUser(loginUser.getUserId(), loginUser.getToken());
        return R.ok(loginUser);
    }

    @Operation(summary = "登录用户路由")
    @GetMapping("/route")
    public R<List<RouteVO>> getLoginUserRoute() {
        List<RouteVO> routes = loginService.getLoginUserRoute(LoginUserContextHandler.getLoginUser().getUserId());
        return R.ok(routes);
    }

}
