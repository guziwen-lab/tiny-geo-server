package com.supermap.shiro.interceptor;

import com.supermap.common.enumeration.BizCodeEnum;
import com.supermap.common.pojo.R;
import com.supermap.common.util.ServletUtils;
import com.supermap.common.util.StringUtils;
import com.supermap.shiro.token.RedisToken;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.WebUtils;

/**
 * @author gzw
 */
@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 跳过 Tomcat 错误页面的转发请求，此类请求不会经过 Shiro Filter，
        // ThreadContext 中没有 SecurityManager，直接放行避免 UnavailableSecurityManagerException
        if (request.getDispatcherType() == DispatcherType.ERROR) {
            return true;
        }

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        if (hasRequiresGuest((HandlerMethod) handler)) {
            return true;
        }

        // 从请求中获取 token
        String token = getTokenFromRequest(request);

        if (StringUtils.isEmpty(token)) {
            ServletUtils.renderJson(response, R.error(BizCodeEnum.UNAUTHORIZED));
            return false;
        }

        try {
            Subject subject = SecurityUtils.getSubject();
            if (!subject.isAuthenticated()) {
                subject.login(new RedisToken(token));
            }
            return true;
        } catch (Exception e) {
            log.debug("认证失败", e);
            ServletUtils.renderJson(response, R.error(BizCodeEnum.AUTHENTICATION_FAILED));
            return false;
        }
    }

    /**
     * 判断当前请求是否需要 guest
     *
     * @param handler HandlerMethod
     * @return boolean
     */
    private boolean hasRequiresGuest(HandlerMethod handler) {
        boolean guestMethod = handler.hasMethodAnnotation(RequiresGuest.class);
        boolean guestClass = handler.getBeanType().isAnnotationPresent(RequiresGuest.class);
        return guestMethod || guestClass;
    }

    /**
     * 从请求中提取 token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String token = null;

        String authHeader = request.getHeader("Authorization");
        if (StringUtils.isNotEmpty(authHeader) && authHeader.startsWith("Bearer "))
            token = authHeader.substring(7); // 去掉 "Bearer "

        if (StringUtils.isEmpty(token))
            token = request.getHeader("token");

        if (StringUtils.isEmpty(token))
            token = request.getParameter("token");

        if (StringUtils.isEmpty(token)) {
            Cookie cookie = WebUtils.getCookie(request, "token");
            if (cookie != null)
                token = cookie.getValue();
        }

        return token;
    }

}
