package com.ennie.community.controller;

import com.ennie.community.Util.CommunityUtil;
import com.ennie.community.Util.HostHolder;
import com.ennie.community.entity.Message;
import com.ennie.community.entity.Page;
import com.ennie.community.entity.User;
import com.ennie.community.service.MessageService;
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
public class MessageController {
    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    //私信列表
    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page){

        User user = hostHolder.getUser();

        page.setRows(messageService.findConversationCount(user.getId()));
        page.setLimit(5);
        page.setPath("/letter/list");

        //会话列表
        List<Message> conversationList = messageService.findConversations(
                user.getId(),page.getOffset(),page.getLimit());

        List<Map<String, Object>> conversations = new ArrayList<>();

        for(Message conversation : conversationList){
            Map<String, Object> map = new HashMap<>();
            map.put("conversation",conversation);

            int unreadCount = messageService.findLetterUnreadCount(user.getId(),
                    conversation.getConversationId());
            map.put("unreadCount",unreadCount);

            int letterCount = messageService.findLetterCount(
                    conversation.getConversationId());
            map.put("letterCount",letterCount);

            int targetId = user.getId()==conversation.getToId()?
                    conversation.getFromId():conversation.getToId();

            User target = userService.findUserById(targetId);
            map.put("target",target);

            conversations.add(map);
        }

        model.addAttribute("conversations",conversations);

        //查询总的未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(),null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);

        return "/site/letter";



    }


    //私信详情
    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Model model, Page page){
        page.setLimit(5);
        page.setRows(messageService.findLetterCount(conversationId));
        page.setPath("/letter/detail/"+conversationId);

        conversationId = conversationId.replaceAll(" ","");
        System.out.println("conversationId"+ conversationId);
        List<Message> letterList = messageService.findLetters(conversationId,page.getOffset(),page.getLimit());
        System.out.println(letterList.get(0));
        List<Map<String,Object>> letters = new ArrayList<>();
        if(letterList!=null) {
            for (Message letter : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", letter);
                System.out.println(letter);

                User fromUser = userService.findUserById(letter.getFromId());
                map.put("fromUser", fromUser);
                letters.add(map);
            }
        }
        model.addAttribute("letters",letters);

        model.addAttribute("target",getLetterTarget(conversationId));

        //将未读消息设置为已读

        List<Integer> ids = getUnreadLetterIds(letterList);
        if(!ids.isEmpty()) {
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";

    }



    private User getLetterTarget(String conversationId){
        String [] ids = conversationId.split("_");
        int d0 = Integer.parseInt(ids[0].replaceAll(" ",""));
        int d1 = Integer.parseInt(ids[1].replaceAll(" ",""));

        int targetId = hostHolder.getUser().getId()==d0?d1:d0;
        return userService.findUserById(targetId);

    }





    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String content, String toName){
        User sender = hostHolder.getUser();

        System.out.println("content:"+content);
        System.out.println("toName:"+toName);
        User target = userService.findUserByName(toName);
        if(target==null){
            return CommunityUtil.getJsonString(1,"目标用户不存在");
        }

        Message message = new Message();

        message.setContent(content);
        message.setFromId(sender.getId());
        message.setToId(target.getId());

        if(message.getFromId()<message.getToId()){
            message.setConversationId(message.getFromId()+"_"+message.getToId());
        }else{
            message.setConversationId(message.getToId()+"_"+message.getFromId());
        }

        message.setStatus(0);
        message.setCreateTime(new Date());


        int result = messageService.addMessage(message);
        return CommunityUtil.getJsonString(0);

    }




    //返回被接受者阅读的已读消息的id
    private List<Integer> getUnreadLetterIds(List<Message> letterList){
        List<Integer> ids = new ArrayList<>();

        if(letterList!=null){
            for(Message message:letterList){
                if(hostHolder.getUser().getId()==message.getToId() && message.getStatus()==0){
                    ids.add(message.getId());
                }
            }
        }

        return ids;
    }
}
