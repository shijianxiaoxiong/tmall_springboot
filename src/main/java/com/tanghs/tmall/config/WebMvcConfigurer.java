package com.tanghs.tmall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.tanghs.tmall.interceptor.LoginInterceptor;
import com.tanghs.tmall.interceptor.OtherInterceptor;

@Configuration
class WebMvcConfigurer extends WebMvcConfigurerAdapter{
    //其他拦截器，实现如首页显示的购物车件数，变形金刚图片跳转和搜索框下的产品分类
    @Bean
    public OtherInterceptor getOtherIntercepter() {
        return new OtherInterceptor();
    }

    //登录拦截器
    @Bean
    public LoginInterceptor getLoginIntercepter() {
        return new LoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(getOtherIntercepter())
                .addPathPatterns("/**");
        registry.addInterceptor(getLoginIntercepter())
                .addPathPatterns("/**");
    }
}