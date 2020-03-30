package com.ennie.community.service;

import com.ennie.community.Util.CommunityConstant;
import com.ennie.community.Util.CommunityUtil;
import com.ennie.community.Util.MailClient;
import com.ennie.community.dao.LoginTicketMapper;
import com.ennie.community.dao.UserMapper;
import com.ennie.community.entity.LoginTicket;
import com.ennie.community.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    UserMapper userMapper;

    @Autowired
    MailClient mailClient;

    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    LoginTicketMapper loginTicketMapper;



    @Value("${community.path.domain}")
    public String domain;

    @Value("${server.servlet.context-path}")
    public String contextPath;

    public User findUserById(int id){
        return userMapper.selectById(id);
    }


    public Map<String,Object> register(User user){

        Map<String, Object> map = new HashMap<>();

        if(user==null){
            throw new IllegalArgumentException("参数不能为空！");
        }

        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号名不能为空");
            return map;
        }


        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","账号名不能为空");
            return map;
        }

        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }

        //验证用户名、邮箱是否存在
        User u = userMapper.selectByName(user.getUsername());
        if(u != null){
            map.put("usernameMsg","该用户名已存在");
            return map;
        }

        u = userMapper.selectByEmail(user.getEmail());
        if(u != null){
            map.put("emailMsg","该邮箱已经被注册");
            return map;
        }

        //加盐
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        //用加密的密码覆盖原来的密码
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        //生成激活验🐎
        user.setActivationCode(CommunityUtil.generateUUID().substring(0,5));
        //状态是未激活
        user.setStatus(0);
        //类型是普通用户
        user.setType(0);
        //设置头像
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        //设置创建时间
        user.setCreateTime(new Date());

        //将创建好的user对象插入到表中
        userMapper.insertUser(user);

        //发送激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        String url = domain + contextPath +"/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活邮箱",content);

        return map;

    }


    public int activation(int userId,String code){
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1){
            //重复激活
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(user.getId(),1);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String,Object> login(String username,String password, long expiredSeconds){
        Map<String,Object> map = new HashMap<>();

        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","账号名为空！");
            return map;
        }

        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","账号密码为空！");
            return map;
        }

        User user = userMapper.selectByName(username);

        if(user == null){
            map.put("usernameMsg","用户名错误");
            return map;
        }

        //验证状态
        if(user.getStatus()==0){
            map.put("statusMsg","用户未激活");
            return map;
        }

        //验证密码

        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码不正确!");
            return map;
        }


        //登陆成功后创建令牌
        //创建LoginTicket实体

        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setStatus(0);
        loginTicket.setTicket(CommunityUtil.generateUUID());

        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds*1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket",loginTicket.getTicket());
        return map;
    }



    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket,1);
    }


    public LoginTicket findLoginTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }

    public int updateHeader(int userId,String headerUrl){
        int result = userMapper.updateHeader(userId,headerUrl);
        return result;
    }




    //找回密码
    public Map<String, Object> forgetPassword( String email ){

        Map<String, Object> map = new HashMap<>();

        if(StringUtils.isBlank(email)){
            map.put("emailMsg","请输入邮箱！");
            return map;
        }

        //输入成功格式map为null

        return map;
    }


    //修改密码
    public int updatePassword(String email, String password){

        User user = userMapper.selectByEmail(email);
        return userMapper.updatePassword(user.getId(), password);

    }





    public User findUserByName(String username){
        return userMapper.selectByName(username);
    }
















}
