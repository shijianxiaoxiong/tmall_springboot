
package com.tanghs.tmall.es;
 
import com.tanghs.tmall.pojo.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//用于Es搜索引擎
public interface ProductESDAO extends ElasticsearchRepository<Product,Integer>{
 
}