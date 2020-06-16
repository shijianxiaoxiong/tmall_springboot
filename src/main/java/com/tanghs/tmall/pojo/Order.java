package com.tanghs.tmall.pojo;
 
import java.io.Serializable;
import java.util.Date;
import java.util.List;
 
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
 
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tanghs.tmall.service.OrderService;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Table(name = "order_")
@JsonIgnoreProperties({ "handler","hibernateLazyInitializer" })
public class Order implements Serializable{
    public Order(){}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
     
    @ManyToOne
    @JoinColumn(name="uid")
     
    private User user;
     
    private String orderCode;     //订单编号
    private String address;       //订单地址
    private String post;           //订单链接地址
    private String receiver;       //收货人
    private String mobile;        //电话号码
    private String userMessage;   //用户消息
    private Date createDate;      //订单生成日期
    private Date payDate;          //付款日期
    private Date deliveryDate;     //发货日期
    private Date confirmDate;     //确认收货日期
    private String status;        //**
     
    @Transient
    private List<OrderItem> orderItems;    //订单里的货物日期
    @Transient
    private float total;                   //订单总金额
    @Transient
    private Integer totalNumber;            //订单总数
    @Transient
    private String statusDesc;              //订单状态
     
    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }
     
    public String getStatusDesc(){
        if(null!=statusDesc)
            return statusDesc;
        String desc ="未知";
        switch(status){
            case OrderService.waitPay:
                desc="待付";
                break;
            case OrderService.waitDelivery:
                desc="待发";
                break;
            case OrderService.waitConfirm:
                desc="待收";
                break;
            case OrderService.waitReview:
                desc="等评";
                break;
            case OrderService.finish:
                desc="完成";
                break;
            case OrderService.delete:
                desc="刪除";
                break;
            default:
                desc="未知";
        }
        statusDesc = desc;
        return statusDesc;
    }

}