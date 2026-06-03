package com.supermap.common.util;

import cn.hutool.http.HttpStatus;
import com.supermap.common.util.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;

/**
 * @author gzw
 */
public class ServletUtils {

    public static String getOrigin() {
        HttpServletRequest request = getHttpServletRequest();
        return request.getHeader("Origin");
    }

    /**
     * 根据当前的SpringMVC环境，获取请求对象
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getHttpServletRequest() {
        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        return requestAttributes.getRequest();
    }

    /**
     * 将JSON字符串渲染到客户端
     *
     * @param response 渲染对象
     * @param obj      待渲染的对象
     */
    public static void renderJson(HttpServletResponse response, Object obj) throws IOException {
        response.setStatus(HttpStatus.HTTP_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().print(JSON.toJSONString(obj));
    }

}
