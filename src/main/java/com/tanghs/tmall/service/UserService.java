package com.tanghs.tmall.service;

import com.tanghs.tmall.dao.UserDAO;
import com.tanghs.tmall.pojo.User;
import com.tanghs.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames="users")
public class UserService {
    @Autowired
    UserDAO userDAO;

   /**
    * @Author tanghs
    * @Description:
    * @Date: 2020/5/15 9:40
    * @Version 1.0
    */
   @Cacheable(key="'users-page-'+#p0+ '-' + #p1")
   public Page4Navigator<User> list(int start, int size, int navigatePages) {
       Sort sort = Sort.by(Sort.Direction.DESC, "id");
       Pageable pageable = PageRequest.of(start, size,sort);
       Page pageFromJPA =userDAO.findAll(pageable);
       return new Page4Navigator<>(pageFromJPA,navigatePages);
   }

   /**
    * @Author tanghs
    * @Description:  保存user信息
    * @Date: 2020/5/20 11:51
    * @Version 1.0
    */
   @CacheEvict(allEntries=true)
    public void add(User user) {
        userDAO.save(user);
    }
    /**
     * @Author tanghs
     * @Description:  判断名字是否存在
     * @Date: 2020/5/20 11:50
     * @Version 1.0
     */
    public boolean isExist(String name) {
        User user = getByName(name);
        return null!=user;
    }

    /**
     * @Author tanghs
     * @Description:  根据name获取User信息
     * @Date: 2020/5/20 11:51
     * @Version 1.0
     */
    @Cacheable(key="'users-one-name-'+ #p0")
    public User getByName(String name) {
        return userDAO.findByName(name);
    }

    /**
     * @Author tanghs
     * @Description: 获取User信息
     * @Date: 2020/5/21 11:42
     * @Version 1.0
     */
    @Cacheable(key="'users-one-name-'+ #p0 +'-password-'+ #p1")
    public User get(String name,String passWord) {
        return userDAO.getByNameAndPassword(name, passWord);
    }
}
