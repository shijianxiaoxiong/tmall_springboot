package com.tanghs.tmall.dao;
  
import java.util.List;
 
import org.springframework.data.jpa.repository.JpaRepository;
 
import com.tanghs.tmall.pojo.Product;
import com.tanghs.tmall.pojo.Review;
 
public interface ReviewDAO extends JpaRepository<Review,Integer>{
 
    List<Review> findByProductOrderByIdDesc(Product product);
    Integer countByProduct(Product product);
 
}