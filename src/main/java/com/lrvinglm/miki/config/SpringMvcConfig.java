package com.lrvinglm.miki.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

//    @Resource
//    LogInterceptor logInterceptor;

//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(logInterceptor)
//                //针对所有请求             过滤掉这些请求
//                .addPathPatterns("/**").excludePathPatterns("/login");
//
//    }
}
