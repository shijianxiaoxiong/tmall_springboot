package com.tanghs.tmall.comparator;
 
import java.util.Comparator;
 
import com.tanghs.tmall.pojo.Product;
 
public class ProductDateComparator implements Comparator<Product>{
 
    @Override
    public int compare(Product p1, Product p2) {        //新品比较器，根据创建时间排序，即新品排在前面
        return p2.getCreateDate().compareTo(p1.getCreateDate());
    }
 
}