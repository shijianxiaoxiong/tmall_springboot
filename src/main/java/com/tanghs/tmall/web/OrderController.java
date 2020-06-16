package com.tanghs.tmall.web;

import com.tanghs.tmall.pojo.Order;
import com.tanghs.tmall.service.OrderItemService;
import com.tanghs.tmall.service.OrderService;
import com.tanghs.tmall.util.Page4Navigator;
import com.tanghs.tmall.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;

@RestController
public class OrderController {
    @Autowired OrderService orderService;
    @Autowired OrderItemService orderItemService;

    /**
     * @Author tanghs
     * @Description:  查询订单
     * @Date: 2020/5/15 16:32
     * @Version 1.0
     */
    @GetMapping("/orders")
    public Page4Navigator<Order> list(@RequestParam(value = "start", defaultValue = "0") Integer start,@RequestParam(value = "size", defaultValue = "5") Integer size) throws Exception {
        start = start<0?0:start;
        Page4Navigator<Order> page =orderService.list(start, size, 5);
        orderItemService.fill(page.getContent());
        orderService.removeOrderFromOrderItem(page.getContent());     //删除orderItem里的属性Order，避免调用Order数据时，RestFul API生成json数据（包括生成OrderItem），而OrderItem里包含Order属性，又再次生成Order，陷入无穷递归
        return page;
    }

    /**
     * @Author tanghs
     * @Description:  订单发货
     * @Date: 2020/5/15 16:32
     * @Version 1.0
     */
    @PutMapping("deliveryOrder/{oid}")
    public Object deliveryOrder(@PathVariable Integer oid) throws IOException {
        Order o = orderService.get(oid);
        o.setDeliveryDate(new Date());
        o.setStatus(OrderService.waitConfirm);
        orderService.update(o);
        return Result.success();
    }
}