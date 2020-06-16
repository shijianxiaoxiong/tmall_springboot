package com.tanghs.tmall.web;

import com.tanghs.tmall.pojo.Product;
import com.tanghs.tmall.service.ProductImageService;
import com.tanghs.tmall.service.ProductService;
import com.tanghs.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

//用于获取数据，页面跳转和数据获取分离开来，便于后期维护
@RestController
public class ProductController {
    @Autowired
    ProductService productService;
    @Autowired
    ProductImageService productImageService;

    /**
     * @Author tanghs
     * @Description: list分页查询跳转
     * @Date: 2020/5/9 9:38
     * @Version 1.0
     */
    @GetMapping("/categories/{cid}/products")
    public Page4Navigator<Product> list(@PathVariable("cid") int cid,@RequestParam(value = "start", defaultValue = "0") int start,
                                         @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        start = start<0?0:start;
        System.out.println(cid);
        Page4Navigator<Product> page =productService.list(cid,start, size, 5);  //5表示导航分页最多有5个，像 [1,2,3,4,5] 这样
        productImageService.setFirstProductImages(page.getContent());     //用于获取产品默认展示图片
        return page;
    }

    /**
     * @Author tanghs
     * @Description: 新增分类
     * @Date: 2020/5/9 17:44
     * @Version 1.0
     */
    @PostMapping("/products")
    public Object add(@RequestBody Product bean,HttpServletRequest request) throws Exception {
        productService.add(bean);  //新增分类
        return bean;
    }


    /**
     * @Author tanghs
     * @Description: 删除分类
     * @Date: 2020/5/11 9:44
     * @Version 1.0
     */
    @DeleteMapping("/products/{id}")
    public String delete(@PathVariable("id") Integer id,HttpServletRequest request) throws Exception{
        productService.delete(id);  //删除分类
        return null;
    }

    /**
     * @Author tanghs
     * @Description: 分类编辑
     * @Date: 2020/5/11 10:30
     * @Version 1.0
     */
    @GetMapping("/products/{id}")
    public Product get(@PathVariable("id") Integer id) throws Exception{
        return productService.get(id);
    }
    /**
     * @Author tanghs
     * @Description: 修改属性
     * @Date: 2020/5/13 9:52
     * @Version 1.0
     */
    @PutMapping("/products/{id}")
    public Product update(@RequestBody Product bean,HttpServletRequest request) throws Exception{
        productService.update(bean);
        return bean;
    }

}