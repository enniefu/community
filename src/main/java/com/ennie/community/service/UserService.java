package com.ennie.community.service;

import com.ennie.community.dao.UserMapper;
import com.ennie.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    public User findUserById(int id){
        return userMapper.selectById(id);
    }
}
