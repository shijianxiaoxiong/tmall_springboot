package com.tanghs.tmall.pojo;
 
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Entity
@Table(name = "category")
@JsonIgnoreProperties({ "handler","hibernateLazyInitializer" })
@Data
public class Category implements Serializable{

    public Category(){}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")   
    Integer id;
     
    String name;

    //一个分类下有多个产品,供前端调用
    @Transient
    List<Product> products;

    //即一个分类又对应多个 List<Product>，提供这个属性，是为了在首页竖状导航的分类名称右边显示推荐产品列表
    //一个分类会对应多行产品，而一行产品里又有多个产品记录
    @Transient
    List<List<Product>> productsByRow;
}