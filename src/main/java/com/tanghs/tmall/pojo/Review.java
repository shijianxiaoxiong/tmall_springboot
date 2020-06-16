package com.tanghs.tmall.pojo;
 
import java.io.Serializable;
import java.util.Date;
 
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

@Data
@Entity
@Table(name = "review")
@JsonIgnoreProperties({ "handler","hibernateLazyInitializer" })
public class Review implements Serializable{
    public Review(){}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
 
    @ManyToOne
    @JoinColumn(name="uid")
    private User user;
 
    @ManyToOne
    @JoinColumn(name="pid")
    private Product product;
 
    private String content;       //评价内容
    private Date createDate;
}