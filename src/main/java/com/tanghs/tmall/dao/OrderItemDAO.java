package com.tanghs.tmall.dao;
  
import java.util.List;
 
import org.springframework.data.jpa.repository.JpaRepository;
 
import com.tanghs.tmall.pojo.Order;
import com.tanghs.tmall.pojo.OrderItem;
import com.tanghs.tmall.pojo.Product;
import com.tanghs.tmall.pojo.User;
 
public interface OrderItemDAO extends JpaRepository<OrderItem,Integer>{
    List<OrderItem> findByOrderOrderByIdDesc(Order order);
    List<OrderItem> findByProduct(Product product);
    //立即购买
    List<OrderItem> findByUserAndOrderIsNull(User user);
}