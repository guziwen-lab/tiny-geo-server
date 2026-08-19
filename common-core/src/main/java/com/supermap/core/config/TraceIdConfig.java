package com.supermap.core.config;

import com.supermap.core.decorator.TraceIdTaskDecorator;
import com.supermap.core.filter.TraceIdFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.task.TaskDecorator;

/**
 * @author gzw
 */
@Configuration
public class TraceIdConfig {

    @Bean
    public FilterRegistrationBean<TraceIdFilter> traceIdFilter() {
        FilterRegistrationBean<TraceIdFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new TraceIdFilter());
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Bean
    public TaskDecorator traceIdTaskDecorator() {
        return new TraceIdTaskDecorator();
    }

}
