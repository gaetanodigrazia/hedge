package com.leep.security.hedge.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.ContentCachingRequestWrapper;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<Filter> requestWrapperFilter() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new Filter() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {

                if (request instanceof HttpServletRequest) {
                    ContentCachingRequestWrapper wrappedRequest =
                            new ContentCachingRequestWrapper((HttpServletRequest) request);
                    chain.doFilter(wrappedRequest, response);
                } else {
                    chain.doFilter(request, response);
                }
            }
        });

        registrationBean.setOrder(1);
        registrationBean.setName("ContentCachingRequestWrapperFilter");
        return registrationBean;
    }
}
