package com.tanghs.tmall.util;
 
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
//用于aop无法直接拦截，springboot 的缓存机制是通过切面编程 aop来实现的，用这种方式故意诱发 aop
@Component
public class SpringContextUtil implements ApplicationContextAware {
     
    private SpringContextUtil() {
         
    }
     
    private static ApplicationContext applicationContext;
     
    @Override
    public void setApplicationContext(ApplicationContext applicationContext){
        SpringContextUtil.applicationContext = applicationContext;
    }
     
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }
 
}