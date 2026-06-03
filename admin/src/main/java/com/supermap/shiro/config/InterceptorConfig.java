package com.supermap.shiro.config;

import com.supermap.shiro.interceptor.TokenInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Map;

@Configuration
@AllArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {

    private final TokenInterceptor tokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        Map<String, String> filterChainDefinitionMap = ShiroConfig.filterChainDefinitionMap;
        InterceptorRegistration reg = registry.addInterceptor(tokenInterceptor);

        filterChainDefinitionMap.forEach((k, v) -> {
            if (v.contains("anon") || v.contains("logout")) {
                reg.excludePathPatterns(k);
            } else {
                reg.addPathPatterns(k);
            }
        });
    }

}