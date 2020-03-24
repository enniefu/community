package com.ennie.community;


import com.ennie.community.Util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {





    @Autowired
    MailClient mailClient;

    //测试类中模板引擎需要手动获取
    @Autowired
    TemplateEngine templateEngine;

    @Test
    public void testSendText(){
        mailClient.sendMail("enniii@126.com","测试代码","老师您好，我的作业可以交了么");

    }





    @Test
    public void testSendHTML(){

        //利用这个对象给模板传递参数
        Context context = new Context();
        context.setVariable("username","ennie");

        //调用模板引擎组合数据和模板
        //之后会生成一个字符串，也就是一个网页
        String content = templateEngine.process("/mail/demo",context);
        System.out.println(content);

        mailClient.sendMail("enniii@126.com","测试发送HTML邮件",content);

    }












}
