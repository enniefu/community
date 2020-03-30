package com.ennie.community.controller;

import com.ennie.community.Util.CommunityConstant;
import com.ennie.community.entity.DiscussPost;
import com.ennie.community.entity.Page;
import com.ennie.community.entity.User;
import com.ennie.community.service.DiscussPostService;
import com.ennie.community.service.LikeService;
import com.ennie.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    UserService userService;

    @Autowired
    LikeService likeService;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){

        //参数全部由dispatchservlet实例化以及注入
        //并且会自动把page注入model
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussPost> list = discussPostService.findDiscussPosts(0,page.getOffset(),page.getLimit());
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(list != null){
            for (DiscussPost post:list){
                Map<String, Object> map = new HashMap<>();
                map.put("post",post);

                User user = userService.findUserById(post.getUserId());
                System.out.println(user);
                map.put("user",user);

                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId());

                map.put("likeCount",likeCount);



                discussPosts.add(map);
            }
        }

        model.addAttribute("discussPosts",discussPosts);


        return "/index";
    }


    @RequestMapping(path = "/error", method = RequestMethod.GET)
    public String getErrorPage(){
        return "/error/500";
    }


}
