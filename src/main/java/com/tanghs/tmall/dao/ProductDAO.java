package com.tanghs.tmall.dao;

import com.tanghs.tmall.pojo.Category;
import com.tanghs.tmall.pojo.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductDAO extends JpaRepository<Product,Integer>{
     Page<Product> findByCategory(Category category, Pageable pageable);
     //根据分类查询出所有产品
     List<Product> findByCategoryOrderById(Category category);

     //分类页搜索 模糊查询
     List<Product> findByNameLike(String keyword, Pageable pageable);

}
 
