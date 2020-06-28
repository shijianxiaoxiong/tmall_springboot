package com.tanghs.tmall.pojo;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Entity
@Table(name = "product")
@JsonIgnoreProperties({ "handler","hibernateLazyInitializer"})
@Document(indexName = "tmall_springboot",type = "product")
public class Product implements Serializable{
    public Product(){}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;
     
    @ManyToOne
    @JoinColumn(name="cid")
    private Category category;
     
    //如果既没有指明 关联到哪个Column,又没有明确要用@Transient忽略，那么就会自动关联到表对应的同名字段
    private String name;           //产品名称
    private String subTitle;       //产品小标题
    private Float originalPrice;    //产品价格
    private Float promotePrice;     //优惠价格
    private Integer stock;          //库存
    /*@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")*/
    private Date createDate;
    @Transient
    private ProductImage firstProductImage;         //后台产品默认显示图片

    @Transient
    private List<ProductImage> productSingleImages;  //单个产品图片集合
    @Transient
    private List<ProductImage> productDetailImages;   //详情产品图片集合
    @Transient
    private int reviewCount;                          //销量
    @Transient
    private int saleCount;                            //累计评价
}