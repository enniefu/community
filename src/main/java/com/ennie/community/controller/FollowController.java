package com.ennie.community.controller;

import com.ennie.community.Util.CommunityConstant;
import com.ennie.community.Util.CommunityUtil;
import com.ennie.community.Util.HostHolder;
import com.ennie.community.entity.Page;
import com.ennie.community.entity.User;
import com.ennie.community.service.FollowService;
import com.ennie.community.service.UserService;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityConstant{


    @Autowired
    FollowService followService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType, int entityId){
        User user = hostHolder.getUser();
        followService.follow(user.getId(),entityType,entityId);
        return CommunityUtil.getJsonString(0,"已关注！");
    }

    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType, int entityId){
        User user = hostHolder.getUser();
        followService.unfollow(user.getId(),entityType,entityId);
        return CommunityUtil.getJsonString(0,"已取消关注！");
    }



    //访问关注列表
    @RequestMapping(path = "/followees/{userId}", method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId") int userId, Model model, Page page){
        User user = userService.findUserById(userId);
        model.addAttribute("user",user);
        if(user==null){
            throw new IllegalArgumentException("该用户不存在");
        }

        page.setPath("/followees/"+userId);
        page.setLimit(5);
        page.setRows((int)followService.findFolloweeCount(userId, ENTITY_TYPE_USER));


        List<Map<String,Object>> userList = followService.findFollowees(userId,page.getOffset(),page.getLimit());
        for(Map<String,Object> map:userList){

            User u = (User) map.get("user");
            map.put("hasFollowed",hasFollowed(u.getId()));
        }

        model.addAttribute("users",userList);
        return "/site/followee";

    }


    //访问粉丝列表
    @RequestMapping(path = "/followers/{userId}", method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId") int userId, Model model, Page page){

        User user = userService.findUserById(userId);
        model.addAttribute("user",user);
        if(user==null){
            throw new IllegalArgumentException("该用户不存在");
        }

        page.setPath("/followers/"+userId);
        page.setLimit(5);
        page.setRows((int)followService.findFollowerCount(ENTITY_TYPE_USER,userId));


        List<Map<String,Object>> userList = followService.findFollowers(userId,page.getOffset(),page.getLimit());
        for(Map<String,Object> map:userList){

            User u = (User) map.get("user");
            map.put("hasFollowed",hasFollowed(u.getId()));
        }

        model.addAttribute("users",userList);
        return "/site/follower";

    }



    //一个私有方法，用来判断当前登陆用户是否关注某用户
    private boolean hasFollowed (int userId){
        //如果没有登陆，直接就表明没有关注
        if(hostHolder.getUser()==null){
            return false;
        }
        return followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
    }


}
