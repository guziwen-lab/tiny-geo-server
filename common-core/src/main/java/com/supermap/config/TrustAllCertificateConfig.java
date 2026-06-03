package com.supermap.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author gzw
 */
@Slf4j
@Component
public class TrustAllCertificateConfig implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        trustAllCertificate();
    }

    /**
     * 信任所有ssl证书
     */
    public void trustAllCertificate() throws Exception {
        // 创建信任所有证书的 SSLContext
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }}, new SecureRandom());

        // 使用信任所有证书的 SSLContext 创建 HTTPS 连接
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
    }

}
