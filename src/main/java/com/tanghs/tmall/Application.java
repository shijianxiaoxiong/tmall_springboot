package com.tanghs.tmall;
import com.tanghs.tmall.util.PortUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableCaching
@EnableElasticsearchRepositories(basePackages = "com.tanghs.tmall.es")   //为es和jpa分别指定不同的包名，否则会冲突
@EnableJpaRepositories(basePackages = {"com.tanghs.tmall.dao", "com.tanghs.tmall.pojo"}) //为es和jpa分别指定不同的包名，否则会冲突
public class Application {
    static{
        PortUtil.checkPort(6379,"Redis 服务端",true);     //用于检查Redis端口是否启动
        PortUtil.checkPort(9300,"ElasticSearch 服务端",true);
        /*PortUtil.checkPort(5601,"Kibana 工具", true);*/
    }
    public static void main(String[] args) {
       System.setProperty("es.set.netty.runtime.available.processors", "false");    //解决ES启动报错问题
        SpringApplication.run(Application.class, args);    
    }
}