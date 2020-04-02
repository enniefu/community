package com.ennie.community.controller;

import com.ennie.community.Util.CommunityConstant;
import com.ennie.community.Util.HostHolder;
import com.ennie.community.entity.Comment;
import com.ennie.community.entity.DiscussPost;
import com.ennie.community.entity.Event;
import com.ennie.community.event.EventProducer;
import com.ennie.community.service.CommentService;
import com.ennie.community.service.DiscussPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping(path = "/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    HostHolder users;

    @Autowired
    CommentService commentService;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    DiscussPostService discussPostService;

    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId")int discussPostId, Comment comment) {

        comment.setCreateTime(new Date());
        comment.setUserId(users.getUser().getId());
        comment.setStatus(0);

        commentService.addComment(comment);

        //触发评论事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(users.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId",discussPostId);

        if(comment.getEntityType()== ENTITY_TYPE_POST){
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }else if(comment.getEntityType()==ENTITY_TYPE_COMMENT){
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }

        //好处就是发消息和页面响应同时处理
        eventProducer.fireEvent(event);
        return "redirect:/discuss/detail/"+discussPostId;
    }
}
