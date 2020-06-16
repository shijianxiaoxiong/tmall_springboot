package com.tanghs.tmall.service;

import com.tanghs.tmall.dao.PropertyValueDAO;
import com.tanghs.tmall.pojo.Product;
import com.tanghs.tmall.pojo.Property;
import com.tanghs.tmall.pojo.PropertyValue;
import com.tanghs.tmall.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

//PropertyValueService，提供修改，查询和 初始化。
        //初始化的意思是：
        //1 这个方法的作用是初始化PropertyValue。 为什么要初始化呢？ 因为对于PropertyValue的管理，没有增加，只有修改。 所以需要通过初始化来进行自动地增加，以便于后面的修改。
        //2 首先根据产品获取分类，然后获取这个分类下的所有属性集合
        //3 然后用属性id和产品id去查询，看看这个属性和这个产品，是否已经存在属性值了。
        //4 如果不存在，那么就创建一个属性值，并设置其属性和产品，接着插入到数据库中。
        //这样就完成了属性值的初始化。
@Service
@CacheConfig(cacheNames="propertyValues")
public class PropertyValueService {
    @Autowired
    PropertyValueDAO propertyValueDAO;
    @Autowired
    PropertyService propertyService;

    /**
     * @Author tanghs
     * @Description:   list属性值查询列表
     * @Date: 2020/5/14 15:49
     * @Version 1.0
     */
    @Cacheable(key="'propertyValues-pid-'+ #p0.id")
    public List<PropertyValue> list(Product product){
        return propertyValueDAO.findByProductOrderByIdDesc(product);
    }

    /**
     * @Author tanghs
     * @Description:  修改属性值
     * @Date: 2020/5/14 16:09
     * @Version 1.0
     */
    @CacheEvict(allEntries=true)
    public void update(PropertyValue bean){
        propertyValueDAO.save(bean);
    }
    /**
     * @Author tanghs
     * @Description:  初始化属性值
     * @Date: 2020/5/14 15:56
     * @Version 1.0
     */
    @Cacheable(key="'properties-cid-'+#p0+'-page-'+#p1 + '-' + #p2 ")
    public void init(Product product) {
        PropertyValueService propertyValueService = SpringContextUtil.getBean(PropertyValueService.class);    //触发aop拦截
        List<Property> propertys= propertyService.listByCategory(product.getCategory());
        for (Property property: propertys) {
            PropertyValue propertyValue = propertyValueService.getByPropertyAndProduct(property, product);
            if(null==propertyValue){
                propertyValue = new PropertyValue();
                propertyValue.setProduct(product);
                propertyValue.setProperty(property);
                propertyValueDAO.save(propertyValue);
            }
        }
    }

    /**
     * @Author tanghs
     * @Description:  根据产品id和属性id获取属性值
     * @Date: 2020/5/14 15:50
     * @Version 1.0
     */
    @Cacheable(key="'propertyValues-one-pid-'+#p0.id+ '-ptid-' + #p1.id")
    public PropertyValue getByPropertyAndProduct(Property property,Product product) {
        return propertyValueDAO.getByPropertyAndProduct(property,product);
    }
}
