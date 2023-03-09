package com.jhr.algoNote.config;

import com.jhr.algoNote.interceptor.LoginUserInterceptor;
import com.jhr.algoNote.config.auth.LoginUserArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final LoginUserArgumentResolver loginUserArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(loginUserArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginUserInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/images/**", "/js/**", "/h2-console/**", "/img/**")
                .excludePathPatterns("/api/**")
                .excludePathPatterns("/error", "/member/new");
    }
}