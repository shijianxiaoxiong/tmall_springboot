package com.tanghs.tmall.pojo;
 
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
 
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Entity
@Table(name = "productimage")
@JsonIgnoreProperties({ "handler","hibernateLazyInitializer"})
//产品图片实体类
public class ProductImage implements Serializable{
    public ProductImage(){}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
     
    @ManyToOne
    @JoinColumn(name="pid")
    @JsonBackReference
    private Product product;
    //@JsonBackReference和@JsonManagedReference：以及@JsonIgnore均是为了解决对象中存在双向引用导致的无限递归（infinite recursion）问题。
    // 这些标注均可用在属性或对应的get、set方法中。
    // 这两个标注通常配对使用，通常用在父子关系中。
    // @JsonBackReference标注的属性在序列化（serialization，即将对象转换为json数据）时，
    // 会被忽略（即结果中的json数据不包含该属性的内容）。
    //@JsonManagedReference标注的属性则会被序列化

    private String type;

}