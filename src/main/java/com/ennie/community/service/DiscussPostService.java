package com.ennie.community.service;

import com.ennie.community.dao.DiscussPostMapper;
import com.ennie.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    DiscussPostMapper discussPostMapper;

    //返回所有评论
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit){
        return discussPostMapper.selectDiscussPosts(userId,offset,limit);
    }

    //返回评论条数
    public int findDiscussPostRows(@Param("userId")int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }



}
