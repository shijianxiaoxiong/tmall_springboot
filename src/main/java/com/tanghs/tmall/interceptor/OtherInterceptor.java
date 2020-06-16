package com.tanghs.tmall.interceptor;
 //其他拦截器，实现如首页显示的购物车件数，变形金刚图片跳转和搜索框下的产品分类
import java.util.List;
 
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
 
import com.tanghs.tmall.pojo.Category;
import com.tanghs.tmall.pojo.OrderItem;
import com.tanghs.tmall.pojo.User;
import com.tanghs.tmall.service.CategoryService;
import com.tanghs.tmall.service.OrderItemService;
 
public class OtherInterceptor implements HandlerInterceptor {
    @Autowired CategoryService categoryService;
    @Autowired OrderItemService orderItemService;
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        return true;  
    }
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        HttpSession session = httpServletRequest.getSession();
        User user =(User) session.getAttribute("user");
            int  cartTotalItemNumber = 0;
            if(null!=user) {
            List<OrderItem> ois = orderItemService.listByUser(user);
            for (OrderItem oi : ois) {
                cartTotalItemNumber+=oi.getNumber();              //购物车总数
            }
         
        }
         
        List<Category> cs =categoryService.list();
        String contextPath=httpServletRequest.getServletContext().getContextPath();
 
        httpServletRequest.getServletContext().setAttribute("categories_below_search", cs);    //搜索框下的产品分类
        session.setAttribute("cartTotalItemNumber", cartTotalItemNumber);
        httpServletRequest.getServletContext().setAttribute("contextPath", contextPath);      //天猫首页路径跳转
    }
 
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }
}