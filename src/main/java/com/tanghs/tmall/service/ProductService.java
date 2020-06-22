package com.tanghs.tmall.service;

import com.tanghs.tmall.dao.CategoryDAO;
import com.tanghs.tmall.dao.ProductDAO;
import com.tanghs.tmall.es.ProductESDAO;
import com.tanghs.tmall.pojo.Category;
import com.tanghs.tmall.pojo.Product;
import com.tanghs.tmall.util.Page4Navigator;
import com.tanghs.tmall.util.SpringContextUtil;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@CacheConfig(cacheNames="products")
public class ProductService {
    @Autowired
    ProductDAO productDAO;
    @Autowired
    CategoryDAO categoryDAO;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    ProductESDAO productESDAO;

    /**
     * @Author tanghs
     * @Description:分类的分页
     * @Date: 2020/5/9 9:25
     * @Version 1.0
     */
    @Cacheable(key = "'products-cid-'+#p0+'-page-'+#p1 + '-' + #p2 ")
    public Page4Navigator<Product> list(int cid, int start, int size, int navigatePages) {
        Category category = categoryDAO.findById(cid).orElse(null);
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(start, size, sort);
        Page pageFromJPA = productDAO.findByCategory(category, pageable);

        return new Page4Navigator<>(pageFromJPA, navigatePages);
    }

    /**
     * @Author tanghs
     * @Description:分类查询
     * @Date: 2020/5/9 9:24
     * @Version 1.0
     */
    public List<Product> list() {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return productDAO.findAll(sort);
    }

    /**
     * @Author tanghs
     * @Description: 新增分类
     * @Date: 2020/5/9 16:01
     * @Version 1.0
     */
    @CacheEvict(allEntries = true)
    public void add(Product bean) {
        productDAO.save(bean);
        productESDAO.save(bean);   //增加，删除，修改的时候，通过 ProductESDAO 同步到 es
    }

    /**
     * @Author tanghs
     * @Description: 删除分类
     * @Date: 2020/5/11 9:42
     * @Version 1.0
     */
    @CacheEvict(allEntries = true)
    public void delete(Integer id) {
        productDAO.deleteById(id);
        productESDAO.deleteById(id);  //增加，删除，修改的时候，通过 ProductESDAO 同步到 es
    }

    /**
     * @Author tanghs
     * @Description: 分类编辑
     * @Date: 2020/5/11 10:32
     * @Version 1.0
     */
    @Cacheable(key = "'products-one-'+ #p0")
    public Product get(Integer id) {
        Product product = productDAO.findById(id).orElse(null);
        return product;
    }

    /**
     * @Author tanghs
     * @Description: 修改分类
     * @Date: 2020/5/11 14:32
     * @Version 1.0
     */
    @CacheEvict(allEntries = true)
    public void update(Product bean) {
        productDAO.save(bean);
        productESDAO.save(bean);    //增加，删除，修改的时候，通过 ProductESDAO 同步到 es
    }

    /**
     * @Author tanghs
     * @Description: 为多个分类填充产品集合
     * @Date: 2020/5/18 11:18
     * @Version 1.0
     */
    public void fill(List<Category> categoryList) {
        for (Category category : categoryList) {
            fill(category);
        }
    }

    /**
     * @Author tanghs
     * @Description: 为分类填充产品集合
     * 主要注意点是两个
     * 1. 分页的时候，会带上cid,表示是某个分类下的产品
     * 2. 如果在ProductService的一个方法里，调用另一个 缓存管理 的方法，
     * 不能够直接调用，需要通过一个工具，再拿一次 ProductService， 然后再调用。
     * @Date: 2020/5/18 11:15
     * @Version 1.0
     */
    public void fill(Category category) {
        ProductService productService = SpringContextUtil.getBean(ProductService.class);
        List<Product> productList = productService.listByCategory(category);
        productImageService.setFirstProductImages(productList);   //将单张图片存储传给前端页面
        category.setProducts(productList);
    }

    /**
     * @@@
     * @Author tanghs
     * @Description: 为多个分类填充推荐产品集合，即把分类下的产品集合，
     * 按照8个为一行，拆成多行，以利于后续页面上进行显示
     * @Date: 2020/5/18 11:19
     * @Version 1.0
     */
    public void fillByRow(List<Category> categoryList) {
        int productNumberEachRow = 8;
        for (Category category : categoryList) {
            List<Product> products = category.getProducts();  //分类下的多行产品，每行产品又有多个产品名
            List<List<Product>> productsByRow = new ArrayList<>();
            for (int i = 0; i < products.size(); i += productNumberEachRow) {
                int size = i + productNumberEachRow;
                size = size > products.size() ? products.size() : size;
                List<Product> productsOfEachRow = products.subList(i, size);
                productsByRow.add(productsOfEachRow);
            }
            category.setProductsByRow(productsByRow);
        }
    }

    /**
     * @Author tanghs
     * @Description: 查询某个分类下的所有产品
     * @Date: 2020/5/18 11:23
     * @Version 1.0
     */
    @Cacheable(key = "'products-cid-'+ #p0.id")
    public List<Product> listByCategory(Category category) {
        List<Product> products = productDAO.findByCategoryOrderById(category);
        return products;
    }

    /**
     * @Author tanghs
     * @Description: 设置销售额和评价总数
     * @Date: q 15:22
     * @Version 1.0
     */

    public void setSaleAndReviewNumber(List<Product> products) {
        for (Product product : products)
            setSaleAndReviewNumber(product);
    }

    public void setSaleAndReviewNumber(Product product) {

        Integer saleCount = orderItemService.getSaleCount(product);
        product.setSaleCount(saleCount);
        //存入评价总数
        int reviewCount = reviewService.getCount(product);
        product.setReviewCount(reviewCount);
    }

    /**
     * @Author tanghs
     * @Description: 分类页搜索
     * @Date: 2020/6/4 10:36
     * @Version 1.0
     */
    public List<Product> search(String keyword, int start, int size) {
        initDatabase2ES();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .should(QueryBuilders.matchPhraseQuery("name", keyword));

        ScoreFunctionBuilder scoreFunctionBuilder = ScoreFunctionBuilders.weightFactorFunction(100);
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(queryBuilder, scoreFunctionBuilder)
                .boostMode(CombineFunction.SUM)
                //设置权重分最低分
                .setMinScore(10);
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(start, size, sort);

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(functionScoreQueryBuilder).build();

        Page<Product> page = productESDAO.search(searchQuery);
        return page.getContent();
    /*    List<Product> products = productDAO.findByNameLike("%" + keyword + "%", pageable);
        return products;
    }*/
    }
        /**
         * @Author tanghs
         * @Description: 初始化数据到es. 因为数据刚开始都在数据库中，不在es中，所以刚开始查询，
         * 先看看es有没有数据，如果没有，就把数据从数据库同步到es中
         * @Date: 2020/6/15 10:55
         * @Version 1.0
         */
        private void initDatabase2ES(){
            Pageable pageable = PageRequest.of(0, 5);
            Page<Product> page = productESDAO.findAll(pageable);
            if (page.getContent().isEmpty()) {
                List<Product> products = productDAO.findAll();
                for (Product product : products) {
                    productESDAO.save(product);
                }
            }
        }


}
