package com.tanghs.tmall.comparator;
 
import java.util.Comparator;
 
import com.tanghs.tmall.pojo.Product;
 
public class ProductPriceComparator implements Comparator<Product> {
 
    @Override
    public int compare(Product p1, Product p2) {         //根据价格排序，价格低的放前面
        return (int) (p1.getPromotePrice()-p2.getPromotePrice());
    }
 
}