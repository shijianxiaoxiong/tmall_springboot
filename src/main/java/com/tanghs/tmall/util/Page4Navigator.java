package com.tanghs.tmall.util;
  
import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
@Getter
@Setter
public class Page4Navigator<T> implements Serializable{

    //jpa 传递出来的分页对象， Page4Navigator 类就是对它进行封装以达到扩展的效果
    Page<T> pageFromJPA;

    //分页的时候 ,如果总页数比较多，那么显示出来的分页超链一个有几个。 比如如果分页出来的超链是这样的： [8,9,10,11,12], 那么 navigatePages 就是5
    Integer navigatePages;

    //总页面数
    Integer totalPages;

    //第几页（基0）
    Integer number;

    //总共有多少条数据
    Integer totalElements;

    //一页最多有多少条数据
    Integer size;

    //当前页有多少条数据 (与 size，不同的是，最后一页可能不满 size 个)
    Integer numberOfElements;

    //数据集合
    List<T> content;

    //是否有数据
    Boolean isHasContent;

    //是否是首页
    Boolean first;

    //是否是末页
    Boolean last;

    //是否有下一页
    Boolean isHasNext;

    //是否有上一页
    Boolean isHasPrevious;

    //分页的时候 ,如果总页数比较多，那么显示出来的分页超链一个有几个。 比如如果分页出来的超链是这样的： [8,9,10,11,12]，那么 navigatepageNums 就是这个数组：[8,9,10,11,12]，这样便于前端展示
    Integer[] navigatepageNums;
      
    public Page4Navigator() {
        //这个空的分页是为了 Redis 从 json格式转换为 Page4Navigator 对象而专门提供的
    }
    //navigatePages：页面显示的个数
    public Page4Navigator(Page<T> pageFromJPA,Integer navigatePages) {
        this.pageFromJPA = pageFromJPA;
        this.navigatePages = navigatePages;
          
        totalPages = pageFromJPA.getTotalPages();
          
        number  = pageFromJPA.getNumber();
          
        totalElements = pageFromJPA.getNumberOfElements();
          
        size = pageFromJPA.getSize();
          
        numberOfElements = pageFromJPA.getNumberOfElements();
          
        content = pageFromJPA.getContent();
          
        isHasContent = pageFromJPA.hasContent();
                  
        first = pageFromJPA.isFirst();
          
        last = pageFromJPA.isLast();
          
        isHasNext = pageFromJPA.hasNext();
          
        isHasPrevious  = pageFromJPA.hasPrevious();      
          
        calcNavigatepageNums();
          
    }
  
    private void calcNavigatepageNums() {
        Integer navigatepageNums[];
        Integer totalPages = getTotalPages();
        Integer num = getNumber();
        //当总页数小于或等于导航页码数时
        if (totalPages <= navigatePages) {
            navigatepageNums = new Integer[totalPages];
            for (int i = 0; i < totalPages; i++) {
                navigatepageNums[i] = i + 1;
            }
        } else { //当总页数大于导航页码数时
            navigatepageNums = new Integer[navigatePages];
            Integer startNum = num - navigatePages / 2;
            Integer endNum = num + navigatePages / 2;

            if (startNum < 1) {
                startNum = 1;
                //(最前navigatePages页
                for (int i = 0; i < navigatePages; i++) {
                    navigatepageNums[i] = startNum++;
                }
            } else if (endNum > totalPages) {
                endNum = totalPages;
                //最后navigatePages页
                for (int i = navigatePages - 1; i >= 0; i--) {
                    navigatepageNums[i] = endNum--;
                }
            } else {
                //所有中间页
                for (int i = 0; i < navigatePages; i++) {
                    navigatepageNums[i] = startNum++;
                }
            }
        } 
        this.navigatepageNums = navigatepageNums;
    }
}