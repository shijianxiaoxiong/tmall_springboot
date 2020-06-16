package com.tanghs.tmall.comparator;
 
import java.util.Comparator;
 
import com.tanghs.tmall.pojo.Product;
 
public class ProductReviewComparator implements Comparator<Product> {
 
    @Override
    public int compare(Product p1, Product p2) {           //人气比较器，根据评价数量排序，评价多的排前面
        return p2.getReviewCount()-p1.getReviewCount();
    }
 
}