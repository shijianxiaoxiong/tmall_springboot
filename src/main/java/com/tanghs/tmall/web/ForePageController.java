package com.tanghs.tmall.web;
 
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

//用于页面跳转，页面跳转和数据获取分离开来，便于后期维护
@Controller
public class ForePageController {


    @GetMapping(value="/")                                              //首页_跳转
    public String index(){
        return "redirect:home";
    }
    @GetMapping(value="/home")
    public String home(){ return "fore/home"; }


    @GetMapping(value="/register")                                      //注册页_跳转
    public String register(){ return "fore/register"; }


    @GetMapping(value="/registerSuccess")                               //注册成功页_跳转
    public String registerSuccess(){ return "fore/registerSuccess"; }


    @GetMapping(value="/login")                                         //登陆页_跳转
    public String login(){ return "fore/login"; }


    @GetMapping(value="/product")                                       //产品详情页_跳转
    public String product(){ return "fore/product"; }


    @GetMapping(value="/category")                                      //产品分类页_跳转
    public String category(){ return "fore/category"; }


    @GetMapping(value="/search")                                        //分类页搜索跳转
    public String searchResult(){
        return "fore/search";
    }

    @GetMapping(value="/buy")                                          //跳转到购买结算页面
    public String buy(){
        return "fore/buy";
    }
    @GetMapping(value="/cart")
    public String cart(){
        return "fore/cart";
    }

    @GetMapping(value="/alipay")                                        //订单支付跳转页面
    public String alipay(){
        return "fore/alipay";
    }

    @GetMapping(value="/payed")                                        //支付成功跳转页面
    public String payed(){
        return "fore/payed";
    }

    @GetMapping(value="/bought")                                        //我的订单跳转
    public String bought(){
        return "fore/bought";
    }

    @GetMapping(value="/confirmPay")                                   //确认支付跳转
    public String confirmPay(){
        return "fore/confirmPay";
    }

    @GetMapping(value="/orderConfirmed")
    public String orderConfirmed(){
        return "fore/orderConfirmed";
    }    //确认收货跳转

    @GetMapping(value="/review")
    public String review(){
        return "fore/review";
    }                   //评价跳转

    /**                                                               //t
     * @Author tanghs
     * @Description:  退出登陆跳转
     * @Date: 2020/6/11 17:36
     * @Version 1.0
     */
    @GetMapping("fore_logout")
    public String logout(){
        Subject subject = SecurityUtils.getSubject();
        if(subject.isAuthenticated()){
            subject.logout();
        }
        return "redirect:home";
    }
}