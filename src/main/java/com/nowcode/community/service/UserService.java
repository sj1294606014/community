package com.nowcode.community.service;

import com.nowcode.community.dao.UserMapper;
import com.nowcode.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;


    public User findUserById(int id){
        return userMapper.selectById(id);
    }

}
