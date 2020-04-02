package com.ennie.community.dao;

import com.ennie.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    //查询当前用户会话列表，针对每个会话只返回最新一条私信
    List<Message> selectConversations(int userId, int offset, int limit);

    //查询当前用户的会话数量
    int selectConversationCount(int userId);

    //查询某个会话所包含的私信列表
    List<Message> selectLetters(String conversationId, int offset, int limit);

    //查询某个会话包含的私信数量
    int selectLetterCount(String conversationId);

    //查询未读私信数量
    int selectLetterUnreadCount(int userId, String conversationId);

    //新增一个消息
    int insertMessage(Message message);

    //改变消息的状态
    int updateStatus(List<Integer> ids, int status);

    //查询某一个主题下最新的通知
    Message selectLatestNotice(int userId, String topic);

    //查询某个主题包含的通知的数量
    int selectNoticeCount(int userId, String topic);


    //查询未读的通知数量
    int selectNoticeUnreadCount(int userId, String topic);

    //查询某一个主题包含的所有通知
    List<Message> selectNotices(int userId, String topic,int offset, int limit);

}
