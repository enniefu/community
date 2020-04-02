package com.ennie.community.controller;


import com.ennie.community.Util.CommunityConstant;
import com.ennie.community.Util.CommunityUtil;
import com.ennie.community.Util.HostHolder;
import com.ennie.community.entity.Event;
import com.ennie.community.entity.User;
import com.ennie.community.event.EventProducer;
import com.ennie.community.service.LikeService;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements CommunityConstant {

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;


    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId,int postId){
        User user = hostHolder.getUser();

        //点赞
        likeService.like(user.getId(),entityType,entityId,entityUserId);

        //判断数量
        long likeCount= likeService.findEntityLikeCount(entityType,entityId);

        //判断状态
         int likeStatus= likeService.findEntityLikeStatus(user.getId(),entityType,entityId);

        Map<String,Object> map = new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);

        //触发点赞事件
        //只有赞同的时候回发送通知，取消赞的时候不会发送通知

        if (likeStatus==1){
            Event event = new Event();
            event.setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId",postId);
            eventProducer.fireEvent(event);
        }


        return CommunityUtil.getJsonString(0,null,map);
    }







}
