package com.ennie.community.controller;

import com.ennie.community.Util.CommunityConstant;
import com.ennie.community.Util.CommunityUtil;
import com.ennie.community.Util.HostHolder;
import com.ennie.community.entity.Comment;
import com.ennie.community.entity.DiscussPost;
import com.ennie.community.entity.Page;
import com.ennie.community.entity.User;
import com.ennie.community.service.CommentService;
import com.ennie.community.service.DiscussPostService;
import com.ennie.community.service.LikeService;
import com.ennie.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @Autowired
    LikeService likeService;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String content, String title){
        System.out.println("访问到了discusspost controller");
        User user = hostHolder.getUser();

        if (user == null){
            return CommunityUtil.getJsonString(403,"您还没有登陆");
        }

        DiscussPost discussPost = new DiscussPost();
        discussPost.setContent(content);
        discussPost.setTitle(title);
        discussPost.setCreateTime(new Date());
        discussPost.setUserId(user.getId());

        int i = discussPostService.addDiscussPost(discussPost);

        //报错的情况将来统一处理
        System.out.println("发布成功");
        return CommunityUtil.getJsonString(0,"发布成功");
    }



    //查看帖子详情
    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId,
                                 Model model, Page page){
        DiscussPost discussPost = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",discussPost);

        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("user",user);

        //点赞
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,discussPostId);
        model.addAttribute("likeCount",likeCount);

        int likeStatus = hostHolder.getUser()==null?0:
                likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_POST,discussPostId);
        model.addAttribute("likeStatus",likeStatus);

        //查评论的分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/"+discussPostId);
        page.setRows(discussPost.getCommentCount());


        List<Comment> commentList = commentService.findCommentsByEntity(
                ENTITY_TYPE_POST,discussPost.getId(),page.getOffset(),page.getLimit());

        //评论的列表
        List<Map<String, Object>> commentVoList = new ArrayList<>();

        //帖子的评论：评论
        //给评论的评论：回复
        if(commentList!=null){
            for(Comment comment :commentList){
                //一个评论的vo
                Map<String,Object> commentVo = new HashMap<>();
                commentVo.put("comment",comment);
                commentVo.put("user",userService.findUserById(comment.getUserId()));

                //点赞
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("likeCount",likeCount);

                likeStatus = hostHolder.getUser()==null?0:
                        likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("likeStatus",likeStatus);

                //回复列表
                List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT,comment.getId(),0,Integer.MAX_VALUE);
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if(replyVoList!=null){
                    for(Comment reply: replyList){
                        Map<String,Object> replyVo = new HashMap<>();
                        //一个回复的vo
                        replyVo.put("reply",reply);
                        replyVo.put("user",userService.findUserById(reply.getUserId()));
                        //回复的目标
                        User target = reply.getTargetId() == 0 ?null:userService.findUserById(reply.getTargetId());
                        replyVo.put("target",target);


                        //点赞
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,reply.getId());
                        replyVo.put("likeCount",likeCount);

                        likeStatus = hostHolder.getUser()==null?0:
                                likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,reply.getId());
                        replyVo.put("likeStatus",likeStatus);




                        replyVoList.add(replyVo);
                    }
                }

                commentVo.put("replys",replyVoList);

                //回复数量
                commentVo.put("replyCount",commentService.findCommentCount(ENTITY_TYPE_COMMENT,comment.getId()));
                System.out.println("replyCount");
                System.out.println(commentService.findCommentCount(ENTITY_TYPE_COMMENT,comment.getId()));
                commentVoList.add(commentVo);
            }
        }




        model.addAttribute("comments",commentVoList);
        return "/site/discuss-detail";
    }


}
