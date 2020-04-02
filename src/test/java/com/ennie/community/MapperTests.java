package com.ennie.community;


import com.ennie.community.dao.*;
import com.ennie.community.entity.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests<messageList> {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testSelectUser(){
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }


    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("ABC");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
    }



    @Test
    public void testUpdateUser(){
        int rows = userMapper.updateStatus(150,1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150,"http://www.nowcoder.com/102.png");
        System.out.println(rows);

        rows = userMapper.updatePassword(150,"hello");
        System.out.println(rows);

    }


    @Test
    public void testSelectPosts(){
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(149,0,10);

        for (DiscussPost post :list){
            System.out.println(post);
        }


        int ans = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(ans);

    }



    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abs");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*60*10));

        loginTicketMapper.insertLoginTicket(loginTicket);

    }


    @Test
    public void testSelectLogin(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abs");
        System.out.println(loginTicket);
        loginTicketMapper.updateStatus("abc",1);
    }



    @Test
    public void testInsertDiscussPost(){
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(0);
        discussPost.setCommentCount(1);
        discussPost.setContent("牛逼");
        discussPost.setCreateTime(new Date());
        discussPost.setScore(99);
        discussPost.setStatus(0);
        discussPost.setTitle("aoligei");
        discussPost.setType(0);

        System.out.println(discussPostMapper.insertDiscussPost(discussPost));
    }

    @Test
    public void testAddComment(){
        Comment comment = new Comment();
        comment.setUserId(1);
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        comment.setEntityId(1);
        comment.setEntityType(1);
        comment.setTargetId(1);
        comment.setContent("牛逼");
        System.out.println(commentMapper.insertComment(comment));
    }



    @Test
    public void testSelectLetters(){
        List<Message> list = messageMapper.selectConversations(111,0,20);
        for(Message m:list){
            System.out.println(m);
        }


        int result = messageMapper.selectConversationCount(111);
        System.out.println(result);



        list =  messageMapper.selectLetters("111_112",0,10);
        for(Message m:list){
            System.out.println(m);
        }

        result = messageMapper.selectLetterCount("111_112");
        System.out.println(result);

        result = messageMapper.selectLetterUnreadCount(131,"111_131");
        System.out.println(result);


    }


    @Test
    public void testSelectNoticeUnreadCount(){

        System.out.println(messageMapper.selectNoticeUnreadCount(111,"comment"));




    }


}




