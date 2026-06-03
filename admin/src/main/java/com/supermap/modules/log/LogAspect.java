package com.supermap.modules.log;

import com.supermap.common.util.JSON;
import com.supermap.modules.log.entity.AccessEntity;
import com.supermap.modules.log.service.AccessService;
import com.supermap.shiro.LoginUser;
import com.supermap.shiro.LoginUserContextHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Arrays;

/**
 * 日志切面
 *
 * @author gzw
 */
@Aspect
@Order(510)
@Component
@Slf4j
public class LogAspect {

    private static final int MAX_LENGTH = 2000;

    private final AccessService accessService;

    public LogAspect(AccessService accessService) {
        this.accessService = accessService;
    }

    @Pointcut("@annotation(com.supermap.modules.log.Log)")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = method.getName();
        String className = signature.getDeclaringTypeName();
        Log accessLog = method.getAnnotation(Log.class);
        String label = null;
        if (accessLog != null)
            label = StringUtils.isNotBlank(accessLog.value())
                    ? accessLog.value()
                    : StringUtils.isNotBlank(accessLog.label())
                    ? accessLog.label()
                    : null;

        Object[] args = filterArgs(proceedingJoinPoint.getArgs());
        String params = JSON.toJSONString(args);
        params = limit(params);

        // 打印参数
        if (log.isDebugEnabled()) {
            String request = String.format("请求方法: %s.%s ---------------- 参数: %s",
                    className,
                    methodName,
                    params);
            log.debug(request);
        }
        long begin = System.currentTimeMillis();
        String resultStr = null;
        try {
            Object result = proceedingJoinPoint.proceed();
            if (result instanceof StreamingResponseBody) {
                resultStr = "[stream]";
            } else {
                if (result != null) {
                    try {
                        resultStr = JSON.toJSONString(result);
                    } catch (Exception ex) {
                        resultStr = "[serialize-error]";
                    }
                }
            }
            resultStr = limit(resultStr);

            long cost = System.currentTimeMillis() - begin;
            log.debug("执行结束 ---------------- 返回值: {}, 耗时：{}", resultStr, cost);

            persistLog(label, className, methodName, params, resultStr, cost);
            return result;
        } catch (Throwable e) {
            long cost = System.currentTimeMillis() - begin;
            String stackTrace = ExceptionUtils.getStackTrace(e);
            persistLog(label, className, methodName, params, resultStr, cost, e.getLocalizedMessage(), stackTrace);
            throw e;
        }
    }

    private Object[] filterArgs(Object[] args) {
        return Arrays.stream(args)
                .filter(arg ->
                        !(arg instanceof HttpServletRequest) &&
                                !(arg instanceof HttpServletResponse) &&
                                !(arg instanceof MultipartFile))
                .toArray();
    }

    private String limit(String value) {
        if (value == null) return null;
        return value.length() > MAX_LENGTH
                ? value.substring(0, MAX_LENGTH) + "..."
                : value;
    }

    private void persistLog(String label, String className, String methodName, String params, String resultStr,
                            long take) {
        persistLog(label, className, methodName, params, resultStr, take, null, null);
    }

    private void persistLog(String label, String className, String methodName, String params, String resultStr,
                            long take, String errorMsg, String exception) {
        AccessEntity accessEntity = new AccessEntity();
        accessEntity.setTraceId(MDC.get("traceId"));
        accessEntity.setLabel(label);
        accessEntity.setClassName(className);
        accessEntity.setMethodName(methodName);
        accessEntity.setParams(params);
        accessEntity.setResult(resultStr);
        accessEntity.setCost(take);
        accessEntity.setErrorMsg(errorMsg);
        accessEntity.setException(exception);
        LoginUser loginUser = LoginUserContextHandler.getLoginUser();
        if (loginUser != null) {
            accessEntity.setRequestUser(loginUser.getUserId());
        }
        accessEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        accessService.asyncSave(accessEntity);
    }

}
