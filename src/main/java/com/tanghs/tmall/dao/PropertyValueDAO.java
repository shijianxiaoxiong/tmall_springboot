package com.tanghs.tmall.dao;

import com.tanghs.tmall.pojo.Product;
import com.tanghs.tmall.pojo.Property;
import com.tanghs.tmall.pojo.PropertyValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyValueDAO extends JpaRepository<PropertyValue,Integer>{
     List<PropertyValue> findByProductOrderByIdDesc(Product product);
     PropertyValue getByPropertyAndProduct(Property property, Product product);
}
 
