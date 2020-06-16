package com.tanghs.tmall.dao;

import com.tanghs.tmall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDAO extends JpaRepository<User,Integer>{
            User findByName(String name);
            User getByNameAndPassword(String name,String passWord);
}
 
