package com.tanghs.tmall.web;

import com.tanghs.tmall.pojo.Property;
import com.tanghs.tmall.service.PropertyService;
import com.tanghs.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

//用于获取数据，页面跳转和数据获取分离开来，便于后期维护
@RestController
public class PropertyController {
    @Autowired PropertyService propertyService;

    /**
     * @Author tanghs
     * @Description: list分页查询跳转
     * @Date: 2020/5/9 9:38
     * @Version 1.0
     */
    @GetMapping("/categories/{cid}/properties")
    public Page4Navigator<Property> list(@PathVariable("cid") int cid,@RequestParam(value = "start", defaultValue = "0") int start,
                                         @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        start = start<0?0:start;
        Page4Navigator<Property> page =propertyService.list(cid,start, size, 5);  //5表示导航分页最多有5个，像 [1,2,3,4,5] 这样
        return page;
    }

    /**
     * @Author tanghs
     * @Description: 新增分类
     * @Date: 2020/5/9 17:44
     * @Version 1.0
     */
    @PostMapping("/properties")
    public Object add(@RequestBody Property bean,HttpServletRequest request) throws Exception {
        propertyService.add(bean);  //新增分类
        return bean;
    }

    /**
     * @Author tanghs
     * @Description: 删除分类
     * @Date: 2020/5/11 9:44
     * @Version 1.0
     */
    @DeleteMapping("/properties/{id}")
    public String delete(@PathVariable("id") Integer id,HttpServletRequest request) throws Exception{
        propertyService.delete(id);  //删除分类
        return null;
    }

    /**
     * @Author tanghs
     * @Description: 分类编辑
     * @Date: 2020/5/11 10:30
     * @Version 1.0
     */
    @GetMapping("/properties/{id}")
    public Optional<Property> get(@PathVariable("id") Integer id) throws Exception{
        return propertyService.get(id);
    }
    /**
     * @Author tanghs
     * @Description: 修改属性
     * @Date: 2020/5/13 9:52
     * @Version 1.0
     */
    @PutMapping("/properties/{id}")
    public Property update(@RequestBody Property bean,HttpServletRequest request) throws Exception{
        propertyService.update(bean);
        return bean;
    }

}