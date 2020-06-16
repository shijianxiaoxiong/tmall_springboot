package com.tanghs.tmall.pojo;
 
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Entity
@Table(name = "orderItem")
@JsonIgnoreProperties({ "handler","hibernateLazyInitializer" })
public class OrderItem implements Serializable{
    public OrderItem(){}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
     
    @ManyToOne
    @JoinColumn(name="pid")
    private Product product;
     
    @ManyToOne
    @JoinColumn(name="oid")
    private Order order;
     
    @ManyToOne
    @JoinColumn(name="uid")
    private User user;
     
    private Integer number;     //订单数量

 
}