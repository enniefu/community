package com.ennie.community.controller;

import com.ennie.community.Util.HostHolder;
import com.ennie.community.entity.Comment;
import com.ennie.community.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping(path = "/comment")
public class CommentController {

    @Autowired
    HostHolder users;

    @Autowired
    CommentService commentService;

    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId")int discussPostId, Comment comment) {

        comment.setCreateTime(new Date());
        comment.setUserId(users.getUser().getId());
        comment.setStatus(0);

        commentService.addComment(comment);

        return "redirect:/discuss/detail/"+discussPostId;
    }
}
