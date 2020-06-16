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
@Table(name = "propertyvalue")
@JsonIgnoreProperties({ "handler","hibernateLazyInitializer" })
public class PropertyValue implements Serializable{
    public PropertyValue(){}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")   
    private int id;
     
    @ManyToOne
    @JoinColumn(name="pid")
    private Product product;

    @ManyToOne
    @JoinColumn(name="ptid")   
    private Property property;
 
    private String value;     //产品属性值
}