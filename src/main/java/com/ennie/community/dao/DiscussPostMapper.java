package com.ennie.community.dao;

import com.ennie.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {


    //为了实现分页查询。
    //offset代表起始行号，limit代表每页显示的数据条数
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    //返回评论总条数
    //Param用于给参数取别名
    //如果方法只有一个参数，并且使用动态sql，<if>中使用，就必须加别名
    int selectDiscussPostRows(@Param("userId")int userId);



    //增加帖子
    int insertDiscussPost(DiscussPost discussPost);




    //根据id查看帖子详情
    DiscussPost selectDiscussPostById(int id);


    //更新帖子的评论数量字段
    int updateCommentCount(int id, int commentCount);



}
