package com.tanghs.tmall.web;

import com.tanghs.tmall.pojo.Product;

import com.tanghs.tmall.pojo.PropertyValue;
import com.tanghs.tmall.service.ProductService;
import com.tanghs.tmall.service.PropertyValueService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;


//用于获取数据，页面跳转和数据获取分离开来，便于后期维护
@RestController
public class PropertyValueController {
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    ProductService productService;

    /**
     * @Author tanghs
     * @Description: list分页查询跳转
     * @Date: 2020/5/9 9:38
     * @Version 1.0
     */
    @GetMapping("/products/{pid}/propertyValues")
    public List<PropertyValue> list(@PathVariable("pid") int pid) throws Exception {
        Product product = productService.get(pid);
        propertyValueService.init(product);
        List<PropertyValue> propertyValues = propertyValueService.list(product);
        return propertyValues;
    }

    /**
     * @Author tanghs
     * @Description: 修改属性
     * @Date: 2020/5/13 9:52
     * @Version 1.0
     */
    @PutMapping("/propertyValues")
    public Object update(@RequestBody PropertyValue bean) throws Exception {
        propertyValueService.update(bean);
        return bean;
    }

}