package com.tanghs.tmall.service;

import com.tanghs.tmall.dao.CategoryDAO;
import com.tanghs.tmall.dao.PropertyDAO;
import com.tanghs.tmall.pojo.Category;
import com.tanghs.tmall.pojo.Property;
import com.tanghs.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames="properties")
public class PropertyService {
    @Autowired PropertyDAO propertyDAO;
    @Autowired CategoryDAO categoryDAO;

    /**
     * @Author tanghs
     * @Description:分类的分页
     * @Date: 2020/5/9 9:25
     * @Version 1.0
     */
    @Cacheable(key="'properties-cid-'+#p0+'-page-'+#p1 + '-' + #p2 ")
    public Page4Navigator<Property> list(int cid,int start, int size, int navigatePages) {
       Optional<Category>  category = categoryDAO.findById(cid);
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(start, size,sort);
        Page pageFromJPA =propertyDAO.findByCategory(category,pageable);
        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }

  /*  *//**
     * @Author tanghs
     * @Description:分类查询
     * @Date: 2020/5/9 9:24
     * @Version 1.0
     *//*
    @Cacheable(key="'properties-all-'+#p0")
    public List<Property> list() {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return propertyDAO.findAll(sort);
    }*/

    /**
     * @Author tanghs
     * @Description: 新增分类
     * @Date: 2020/5/9 16:01
     * @Version 1.0
     */
    @CacheEvict(allEntries=true)
    public void add(Property bean){
        propertyDAO.save(bean);
    }

    /**
     * @Author tanghs
     * @Description: 删除分类
     * @Date: 2020/5/11 9:42
     * @Version 1.0
     */
    @CacheEvict(allEntries=true)
    public void delete(Integer id){
        propertyDAO.deleteById(id);
    }

    /**
     * @Author tanghs
     * @Description: 分类编辑
     * @Date: 2020/5/11 10:32
     * @Version 1.0
     */
    @Cacheable(key="'properties-one-'+ #p0")
    public Optional<Property> get(Integer id){
        Optional<Property> property = propertyDAO.findById(id);
        return property;
    }

    /**
     * @Author tanghs
     * @Description: 修改分类
     * @Date: 2020/5/11 14:32
     * @Version 1.0
     */
    @CacheEvict(allEntries=true)
    public void update(Property bean){
        propertyDAO.save(bean);
    }

    /**
     * @Author tanghs
     * @Description:  通过分类获取所有属性集合的方法
     * @Date: 2020/5/14 15:32
     * @Version 1.0
     */
    @Cacheable(key="'properties-cid-'+ #p0.id")
    public List<Property> listByCategory(Category category){
        return propertyDAO.findByCategory(category);
    }
}