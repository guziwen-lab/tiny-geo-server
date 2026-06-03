package com.supermap.shiro.config;

import com.supermap.shiro.credential.DefaultCredentialsMatcher;
import com.supermap.shiro.encoder.BCryptPasswordEncoder;
import com.supermap.shiro.encoder.PasswordEncoder;
import com.supermap.shiro.filter.TokenFilter;
import com.supermap.shiro.pam.CustomModularRealmAuthenticator;
import jakarta.servlet.Filter;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.pam.FirstSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

/**
 * shiro 配置类
 *
 * @author gzw
 */
@Configuration
public class ShiroConfig {

    private final static String CUSTOM_FILTER = "custom";

    public final static Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();

    public ShiroConfig() {
        // 可以匿名访问的接口
        filterChainDefinitionMap.put("/swagger-ui.html/**", "anon");
        filterChainDefinitionMap.put("/swagger-ui/**", "anon");
        filterChainDefinitionMap.put("/webjars/**", "anon");
        filterChainDefinitionMap.put("/v2/**", "anon");
        filterChainDefinitionMap.put("/swagger-resources/**", "anon");
        filterChainDefinitionMap.put("/doc.html/**", "anon");
        filterChainDefinitionMap.put("/v3/**", "anon");
        // 使用nginx代理的话要把访问前缀带上
        filterChainDefinitionMap.put("/new-ygdz/v3/**", "anon");
        filterChainDefinitionMap.put("/new-ygdz-test/v3/**", "anon");
        // 登录
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/captcha", "anon");

        // 其余接口通过redis过滤器
        filterChainDefinitionMap.put("/**", CUSTOM_FILTER);
    }

    @ConditionalOnMissingBean(PasswordEncoder.class)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Filter tokenFilter() {
        return new TokenFilter();
    }

    @ConditionalOnMissingBean(CredentialsMatcher.class)
    @Bean
    public DefaultCredentialsMatcher credentialsMatcher(PasswordEncoder passwordEncoder) {
        return new DefaultCredentialsMatcher(passwordEncoder);
    }

    @Bean
    public DefaultWebSecurityManager defaultWebSecurityManager(Collection<Realm> realms,
                                                               ModularRealmAuthenticator authenticator) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealms(realms);
        securityManager.setAuthenticator(authenticator);

        // 关闭 session
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator evaluator = new DefaultSessionStorageEvaluator();
        evaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(evaluator);
        securityManager.setSubjectDAO(subjectDAO);

        return securityManager;
    }

    @Bean
    public ModularRealmAuthenticator authenticator(Collection<Realm> realms) {
        CustomModularRealmAuthenticator authenticator = new CustomModularRealmAuthenticator();
        authenticator.setAuthenticationStrategy(new FirstSuccessfulStrategy());
        authenticator.setRealms(realms);
        return authenticator;
    }

    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        chainDefinition.addPathDefinitions(filterChainDefinitionMap);
        return chainDefinition;
    }

    /**
     * Shiro内置常用的过滤器：
     * anon: 无需认证（登录）可以访问
     * authc: 必须认证才可以访问
     * user: 如果使用rememberMe的功能可以直接访问
     * perms： 该资源必须得到资源权限才可以访问
     * role: 该资源必须得到角色权限才可以访问
     */
    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager defaultWebSecurityManager,
                                                         ShiroFilterChainDefinition shiroFilterChainDefinition,
                                                         Filter tokenFilter) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(defaultWebSecurityManager);
        shiroFilterFactoryBean.setLoginUrl("/login");

        Map<String, Filter> filters = new HashMap<>();
        filters.put(CUSTOM_FILTER, tokenFilter);
        shiroFilterFactoryBean.setFilters(filters);

        Map<String, String> filterChainMap = shiroFilterChainDefinition.getFilterChainMap();
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainMap);

        return shiroFilterFactoryBean;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

}
