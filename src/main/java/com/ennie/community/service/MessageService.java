package com.ennie.community.service;

import com.ennie.community.Util.SensitiveFilter;
import com.ennie.community.dao.MessageMapper;
import com.ennie.community.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    SensitiveFilter sensitiveFilter;

    public List<Message> findConversations(int userId, int offset, int limit){
        return messageMapper.selectConversations(userId,offset,limit);
    }

    public int findConversationCount(int userId){
        return messageMapper.selectConversationCount(userId);
    }

    public List<Message> findLetters(String conversationId, int offset , int limit){
        return messageMapper.selectLetters(conversationId,offset,limit);
    }


    public int findLetterCount(String conversationId){
        return messageMapper.selectLetterCount(conversationId);
    }

    public int findLetterUnreadCount(int userId, String conversationId){
        return messageMapper.selectLetterUnreadCount(userId,conversationId);
    }

    public int addMessage(Message message){
        message.setContent(HtmlUtils.htmlEscape(sensitiveFilter.filter(message.getContent())));
        return messageMapper.insertMessage(message);
    }



    public  int readMessage(List<Integer> ids){
        return messageMapper.updateStatus(ids,1);
    }

}