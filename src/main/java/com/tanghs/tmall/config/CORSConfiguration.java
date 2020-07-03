package com.tanghs.tmall.config;
 
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//配置类，用于允许所有的请求都跨域。
//因为是二次请求，第一次是获取 html 页面， 第二次通过 html 页面上的 js 代码异步获取数据，
// 一旦部署到服务器就容易面临跨域请求问题，所以允许所有访问都跨域，就不会出现通过 axios 获取数据获取不到的问题了。
@Configuration
public class CORSConfiguration extends WebMvcConfigurerAdapter{
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //所有请求都允许跨域
        registry.addMapping("/**")
                .allowedOrigins("*")      //响应首部中可以携带这个头部表示服务器允许哪些域可以访问该资源
                .allowedMethods("*")      //该首部字段用于预检请求的响应，指明实际请求所允许使用的HTTP方法
                .allowedHeaders("*");     //该首部字段用于预检请求的响应。指明了实际请求中允许携带的首部字段
    }
}