package com.tanghs.tmall.service;
 
import java.util.List;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
 
import com.tanghs.tmall.dao.ReviewDAO;
import com.tanghs.tmall.pojo.Product;
import com.tanghs.tmall.pojo.Review;
 
@Service
@CacheConfig(cacheNames="reviews")
public class ReviewService {
 
    @Autowired ReviewDAO reviewDAO;
    @Autowired ProductService productService;

    /**
     * @Author tanghs
     * @Description:   添加产品评价
     * @Date: 2020/5/21 15:03
     * @Version 1.0
     */
    @CacheEvict(allEntries=true)
    public void add(Review review) {
        reviewDAO.save(review);
    }

    /**
     * @Author tanghs
     * @Description:    评价列表
     * @Date: 2020/5/21 15:03
     * @Version 1.0
     */
    @Cacheable(key="'reviews-pid-'+ #p0.id")
    public List<Review> list(Product product){
        List<Review> result =  reviewDAO.findByProductOrderByIdDesc(product);
        return result;
    }

    /**
     * @Author tanghs
     * @Description:   统计评价
     * @Date: 2020/5/21 15:03
     * @Version 1.0
     */
    @Cacheable(key="'reviews-count-pid-'+ #p0.id")
    public int getCount(Product product) {
        return reviewDAO.countByProduct(product);
    }
     
}