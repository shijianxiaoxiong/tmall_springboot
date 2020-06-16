package com.tanghs.tmall.web;

import com.tanghs.tmall.pojo.User;
import com.tanghs.tmall.service.UserService;
import com.tanghs.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//用于获取数据，页面跳转和数据获取分离开来，便于后期维护
@RestController
public class UserController {
    @Autowired
    UserService userService;

    /**
     * @Author tanghs
     * @Description:  list分页信息
     * @Date: 2020/5/15 9:43
     * @Version 1.0
     */

    @GetMapping("/users")
    public Page4Navigator<User> list(@RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        start = start<0?0:start;
        Page4Navigator<User> page = userService.list(start,size,5);
        return page;
    }



}