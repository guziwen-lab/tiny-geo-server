package com.supermap.common.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author gzw
 */
public class IpUtils {

    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            // 可能有多个代理，会有多个 ip，用逗号分隔，第一个才是客户端真实IP
            return ip.split(",")[0].trim();
        }

        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        // 如果没有反向代理
        return request.getRemoteAddr();
    }

}
