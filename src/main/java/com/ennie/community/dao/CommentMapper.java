package com.ennie.community.dao;

import com.ennie.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    //返回评论数量
    int selectCountByEntity(int entityType, int entityId);

    //返回特定对象的评论数
    List<Comment> selectCommentByEntity(int entityType, int entityId, int offset, int limit);

    //增加一条帖子
    int insertComment(Comment comment);

    //根据id获取评论
    Comment selectCommentById(int id);

}
