package com.supermap.common.util;

import cn.hutool.core.net.NetUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 网络工具类
 *
 * @author gzw
 */
@Slf4j
public class NetUtils extends NetUtil {

    public static String getIp(String url) {
        String ip = "";
        try {
            URL urlObj = new URL(url);
            ip = urlObj.getHost();
        } catch (Exception e) {
            log.error("获取ip失败", e);
        }
        return ip;
    }

    public static int getPort(String url) {
        int port = 80;
        try {
            URL urlObj = new URL(url);
            port = urlObj.getPort();
            if (port == -1)
                port = 80;
        } catch (Exception e) {
            log.error("获取端口失败", e);
        }
        return port;
    }

    public static boolean isConnectivity(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            return false;
        }
    }

}