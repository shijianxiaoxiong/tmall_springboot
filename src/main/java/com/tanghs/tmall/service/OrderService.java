package com.tanghs.tmall.service;
 
import com.tanghs.tmall.dao.OrderDAO;
import com.tanghs.tmall.pojo.Order;
import com.tanghs.tmall.pojo.OrderItem;
import com.tanghs.tmall.pojo.User;
import com.tanghs.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
 
@Service
@CacheConfig(cacheNames="orders")
public class OrderService {
    public static final String waitPay = "waitPay";
    public static final String waitDelivery = "waitDelivery";
    public static final String waitConfirm = "waitConfirm";
    public static final String waitReview = "waitReview";
    public static final String finish = "finish";
    public static final String delete = "delete";  
     
    @Autowired OrderDAO orderDAO;
    @Autowired OrderItemService orderItemService;
     /**
      * @Author tanghs
      * @Description:  订单查询
      * @Date: 2020/5/15 13:51
      * @Version 1.0
      */
     @Cacheable(key="'orders-page-'+#p0+ '-' + #p1")
    public Page4Navigator<Order> list(int start, int size, int navigatePages) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(start, size,sort);
        Page pageFromJPA =orderDAO.findAll(pageable);
        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }

    /**
     * @Author tanghs
     * @Description:  删除orderItem里的属性Order，避免调用Order数据时，RestFul API生成json数据（包括生成OrderItem），而OrderItem里包含Order属性，又再次生成Order，陷入无穷递归
     * 为什么不用 @JsonIgnoreProperties 来标记这个字段呢？ 因为后续我们要整合Redis，如果标记成了 @JsonIgnoreProperties 会在和 Redis 整合的时候有 Bug, 所以还是采用这种方式比较好。
     * @Date: 2020/5/15 13:51
     * @Version 1.0
     */
    public void removeOrderFromOrderItem(List<Order> orders) {
        for (Order order : orders) {
            removeOrderFromOrderItem(order);
        }
    }
    public void removeOrderFromOrderItem(Order order) {
        List<OrderItem> orderItems= order.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(null);
        }
    }

    /**
     * @Author tanghs
     * @Description: 编辑时获取获取Item，这里分两步做，感觉较为清晰
     * @Date: 2020/5/15 13:57
     * @Version 1.0
     */
    @Cacheable(key="'orders-one-'+ #p0")
    public Order get(int oid) {
        return orderDAO.findById(oid).orElse(null);
    }

    /**
     * @Author tanghs
     * @Description:   修改订单
     * @Date: 2020/5/15 13:58
     * @Version 1.0
     */
    @CacheEvict(allEntries=true)
    public void update(Order bean) {
        orderDAO.save(bean);
    }
    
    /**
     * @Author tanghs
     * @Description:    生成订单
     * @Date: 2020/6/9 17:15
     * @Version 1.0
     */
    //故意抛出异常代码用来模拟当增加订单后出现异常，观察事务管理是否预期发生。（需要把false修改为true才能观察到）
    @CacheEvict(allEntries=true)
    @Transactional(propagation= Propagation.REQUIRED,rollbackForClassName="Exception")
    public float add(Order order, List<OrderItem> ois) {
        float total = 0;
        add(order);

        if(false)
            throw new RuntimeException();

        for (OrderItem oi: ois) {
            oi.setOrder(order);
            orderItemService.update(oi);
            total+=oi.getProduct().getPromotePrice()*oi.getNumber();
        }
        return total;
    }

    @CacheEvict(allEntries=true)
    public void add(Order order) {
        orderDAO.save(order);
    }

    /**
     * @Author tanghs
     * @Description:   查询所有订单状态，不包括已删除订单
     * @Date: 2020/6/10 8:54
     * @Version 1.0
     */
    public List<Order> listByUserWithoutDelete(User user) {
        List<Order> orders = listByUserAndNotDeleted(user);
        orderItemService.fill(orders);
        return orders;
    }
    @Cacheable(key="'orders-uid-'+ #p0.id")
    public List<Order> listByUserAndNotDeleted(User user) {
        return orderDAO.findByUserAndStatusNotOrderByIdDesc(user, OrderService.delete);
    }
    /**
     * @Author tanghs
     * @Description:  计算存储订单总金额
     * @Date: 2020/6/10 10:06
     * @Version 1.0
     */
    public void totalAmount(Order order) {
        List<OrderItem> orderItems = order.getOrderItems();
        float total = 0;
        for (OrderItem orderItem : orderItems) {
            total+=orderItem.getProduct().getPromotePrice()*orderItem.getNumber();
        }
        order.setTotal(total);
    }
}