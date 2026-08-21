package com.supermap.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5000);//单位为ms
        factory.setConnectTimeout(5000);//单位为ms
        return factory;
    }

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }

    @Bean("insecureRestTemplate")
    public RestTemplate insecureRestTemplate() throws Exception {

        TrustManager[] trustAllCerts = {
                new X509TrustManager() {

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    @Override
                    public void checkClientTrusted(
                            X509Certificate[] chain,
                            String authType) {
                    }

                    @Override
                    public void checkServerTrusted(
                            X509Certificate[] chain,
                            String authType) {
                    }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(
                null,
                trustAllCerts,
                new SecureRandom()
        );

        HostnameVerifier hostnameVerifier =
                (hostname, session) -> true;

        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory() {

                    @Override
                    protected void prepareConnection(
                            HttpURLConnection connection,
                            String httpMethod) throws IOException {

                        super.prepareConnection(connection, httpMethod);

                        if (connection instanceof HttpsURLConnection httpsConnection) {
                            httpsConnection.setSSLSocketFactory(
                                    sslContext.getSocketFactory()
                            );
                            httpsConnection.setHostnameVerifier(
                                    hostnameVerifier
                            );
                        }
                    }
                };

        return new RestTemplate(requestFactory);
    }

}