package com.tanghs.tmall.web;
 
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
 //用于页面跳转，页面跳转和数据获取分离开来，便于后期维护
@Controller
public class AdminPageController {
    //分类管理页面
    @GetMapping(value="/admin")
    public String admin(){
        return "redirect:admin_category_list";
    }

    @GetMapping(value="/admin_category_list")  //list页面跳转
    public String listCategory(){
        return "admin/category/listCategory";
    }

     @GetMapping(value="/admin_category_edit") //编辑页面跳转
     public String editCategory(){
         return "admin/category/editCategory";
     }

     //产品属性页面
     @GetMapping(value="/admin_property_list") //列表页面跳转
     public String listProperty(){
         return "admin/property/listProperty";
     }
     @GetMapping(value="/admin_property_edit") //编辑页面跳转
     public String editProperty(){
         return "admin/property/editProperty";
     }

     //产品管理页面
     @GetMapping(value="/admin_product_list") //列表页面跳转
     public String listProduct(){
         return "admin/product/listProduct";
     }
     @GetMapping(value="/admin_product_edit") //编辑页面跳转
     public String editProduct(){
         return "admin/product/editProduct";
     }

     //产品图片管理
     @GetMapping(value="/admin_productImage_list") //图片详情页面跳转
     public String listProductImage(){
         return "admin/productImage/listProductImage";
     }
     @GetMapping(value="/admin_propertyValue_edit") //设置属性页面跳转
     public String listProductValue(){ return "admin/productImage/listProductValue"; }

     //产品属性值页面
     @GetMapping(value="/admin_propertyValues_edit") //产品属性值编辑页面跳转
     public String editPropertyValue(){ return "admin/propertyValue/editPropertyValue"; }

     //用户管理
     @GetMapping(value="/admin_user_list") //产品属性值编辑页面跳转
     public String listUser(){ return "admin/user/listUser"; }

     //订单管理
     @GetMapping(value="/admin_order_list") //产品属性值编辑页面跳转
     public String listOrder(){ return "admin/order/listOrder"; }


}