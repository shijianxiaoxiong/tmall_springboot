package com.tanghs.tmall.comparator;
 
import java.util.Comparator;
 
import com.tanghs.tmall.pojo.Product;
 
public class ProductSaleCountComparator implements Comparator<Product> {
 
    @Override
    public int compare(Product p1, Product p2) {
        return p2.getSaleCount()-p1.getSaleCount();      //销量比较器，根据销量排序，销量多的排在前面
    }
 
}