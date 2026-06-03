package com.supermap.exception;

import com.supermap.common.enumeration.BizCodeEnum;
import com.supermap.common.pojo.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author gzw
 */
@Slf4j
@RestControllerAdvice(basePackages = {"com.supermap.modules"})
@Order(1)
public class ShiroExceptionControllerAdvice {

    /**
     * 处理密码错误异常
     *
     * @param e {@link IncorrectCredentialsException IncorrectCredentialsException}
     * @return R
     */
    @ExceptionHandler(IncorrectCredentialsException.class)
    public R<Void> handleIncorrectCredentialsException(IncorrectCredentialsException e) {
        log.error(e.getMessage(), e);
        return R.error(BizCodeEnum.AUTHENTICATION_FAILED.getCode(), "密码错误");
    }

    @ExceptionHandler(ExcessiveAttemptsException.class)
    public R<Void> handleExcessiveAttemptsException(ExcessiveAttemptsException e) {
        log.error(e.getMessage(), e);
        return R.error(BizCodeEnum.AUTHENTICATION_FAILED.getCode(), e.getLocalizedMessage());
    }

    /**
     * 处理权限不足异常
     *
     * @param e {@link UnauthorizedException UnauthorizedException}
     * @return R
     */
    @ExceptionHandler(UnauthorizedException.class)
    public R<Void> handleUnauthorizedException(UnauthorizedException e) {
        log.error(e.getMessage(), e);
        return R.error(BizCodeEnum.PERMISSIONS_DENIAL.getCode(), "您没有操作此功能的权限，请联系管理员开通权限");
    }

}