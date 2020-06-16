package com.tanghs.tmall.interceptor;
 //1. 准备字符串数组 requireAuthPages，存放那些需要登录才能访问的路径
//2. 获取uri
//3. 去掉前缀/tmall_springboot
//4. 判断是否是以 requireAuthPages 里的开头的
//4.1 如果是就判断是否登陆，未登陆就跳转到 login 页面
//4.2 如果不是就放行
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import com.tanghs.tmall.pojo.User;
import org.apache.catalina.security.SecurityUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
 
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        HttpSession session = httpServletRequest.getSession();
        String contextPath=session.getServletContext().getContextPath();
        String[] requireAuthPages = new String[]{
                "buy",
                "alipay",
                "payed",
                "cart",
                "bought",
                "confirmPay",
                "orderConfirmed",
                 
                "fore_buyOne",
                "fore_buy",
                "fore_addCart",
                "fore_cart",
                "fore_changeOrderItem",
                "fore_deleteOrderItem",
                "fore_createOrder",
                "fore_payed",
                "fore_bought",
                "fore_confirmPay",
                "fore_orderConfirmed",
                "fore_deleteOrder",
                "fore_review",
                "fore_doReview"
        };
  
        String uri = httpServletRequest.getRequestURI();
 
        uri = StringUtils.remove(uri, contextPath+"/");
        String page = uri;
         
        if(beginWith(page, requireAuthPages)){
            Subject subject = SecurityUtils.getSubject();
            if(!subject.isAuthenticated()){
                httpServletResponse.sendRedirect("login");
                return false;
            }
        }
        return true;  
    }
 
    private boolean beginWith(String page, String[] requiredAuthPages) {
        boolean result = false;
        for (String requiredAuthPage : requiredAuthPages) {
            if(StringUtils.startsWith(page, requiredAuthPage)) {
                result = true; 
                break;
            }
        }
        return result;
    }
 
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }
 
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }
}