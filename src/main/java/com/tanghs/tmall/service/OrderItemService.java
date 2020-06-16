package com.tanghs.tmall.service;
 
import com.tanghs.tmall.dao.OrderItemDAO;
import com.tanghs.tmall.pojo.Order;
import com.tanghs.tmall.pojo.OrderItem;
import com.tanghs.tmall.pojo.Product;
import com.tanghs.tmall.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
 
import java.util.List;
 
@Service
@CacheConfig(cacheNames="orderItems")
public class OrderItemService {
    @Autowired OrderItemDAO orderItemDAO;
    @Autowired ProductImageService productImageService;

    /**
     * @Author tanghs
     * @Description:
     * @Date: 2020/5/21 15:07
     * @Version 1.0
     */
    public void fill(List<Order> orders) {
        for (Order order : orders)
            fill(order);
    }
    /**
     * @Author tanghs
     * @Description:  填充订单项
     * @Date: 2020/5/21 15:14
     * @Version 1.0
     */
    public void fill(Order order) {
        List<OrderItem> orderItems = listByOrder(order);
        float total = 0;
        int totalNumber = 0;           
        for (OrderItem oi :orderItems) {
            total+=oi.getNumber()*oi.getProduct().getPromotePrice();
            totalNumber+=oi.getNumber();
            productImageService.setFirstProductImage(oi.getProduct());
        }
        order.setTotal(total);
        order.setOrderItems(orderItems);
        order.setTotalNumber(totalNumber);     
    }
    @Cacheable(key="'orderItems-oid-'+ #p0.id")
    public List<OrderItem> listByOrder(Order order) {
        return orderItemDAO.findByOrderOrderByIdDesc(order);
    }

    /**
     * @Author tanghs
     * @Description:   统计销售额
     * @Date: 2020/5/21 15:08
     * @Version 1.0
     */
    public int getSaleCount(Product product) {
        List<OrderItem> orderItems =listByProduct(product);
        int result =0;
        for (OrderItem orderItem : orderItems) {
            if(null!=orderItem.getOrder())
                if(null!= orderItem.getOrder() && null!=orderItem.getOrder().getPayDate())
                    result+=orderItem.getNumber();
        }
        return result;
    }
    @Cacheable(key="'orderItems-pid-'+ #p0.id")
    public List<OrderItem> listByProduct(Product product) {
        return orderItemDAO.findByProduct(product);
    }

    /**
     * @Author tanghs
     * @Description:  根据用户查询订单详情
     * @Date: 2020/6/9 9:51
     * @Version 1.0
     */
    @Cacheable(key="'orderItems-uid-'+ #p0.id")
    public List<OrderItem> listByUser(User user) {
        return orderItemDAO.findByUserAndOrderIsNull(user);
    }

    @CacheEvict(allEntries=true)
    public void update(OrderItem orderItem) {
        orderItemDAO.save(orderItem);
    }
    @CacheEvict(allEntries=true)
    public void add(OrderItem orderItem) {
        orderItemDAO.save(orderItem);
    }
    @CacheEvict(allEntries=true)
    public void delete(Integer orderItemId) {
        orderItemDAO.deleteById(orderItemId);
    }
    @Cacheable(key="'orderItems-one-'+ #p0")
    public OrderItem get(Integer orderId) {
       return orderItemDAO.findById(orderId).orElse(null);
    }
}