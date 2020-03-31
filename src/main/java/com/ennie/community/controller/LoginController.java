package com.ennie.community.controller;

import com.ennie.community.Util.CommunityConstant;
import com.ennie.community.Util.CommunityUtil;
import com.ennie.community.Util.MailClient;
import com.ennie.community.Util.RedisKeyUtil;
import com.ennie.community.entity.User;
import com.ennie.community.service.UserService;
import com.google.code.kaptcha.Producer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Value("${server.servlet.context-path}")
    private String contextPath;



    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegister(){

        return "/site/register";
    }


    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage(){

        return "/site/login";
    }



    @RequestMapping(path="/register", method = RequestMethod.POST)
    public String register(Model model, User user){
        Map<String,Object> map = userService.register(user);
        if(map == null || map.isEmpty()){
            model.addAttribute("msg","您已经注册成功，请尽快前往邮箱激活");
            model.addAttribute("target","/index");
            return "site/operate-result";
        }else{
            //注册失败
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));

            return "/site/register";
        }
    }


    @RequestMapping(path = "/activation/{userId}/{code}",method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId,@PathVariable("code") String code){
        int result = userService.activation(userId,code);

        if (result == ACTIVATION_SUCCESS){

            model.addAttribute("msg","您已经激活成功，账号已经可以正常使用");
            model.addAttribute("target","/login");

        }else if(result == ACTIVATION_REPEAT){

            model.addAttribute("msg","无效的操作！该账号已经激活！");
            model.addAttribute("target","/index");

        }else {

            model.addAttribute("msg","激活失败");
            model.addAttribute("target","/index");

        }
        return "/site/operate-result";
    }



    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response/*, HttpSession session*/){

        //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

//        session.setAttribute("kaptcha",text);
        /**
         * 重构代码，验证码的结果不存放在session里面，改成存放在redis里面
         */

        //验证码的归属者
        //临时给客户端颁发一个凭证
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner",kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);

        //将验证码存入redis
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey,text,60, TimeUnit.SECONDS);

        //将图片输出给浏览器
        response.setContentType("image/png");

        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            logger.error("响应验证码失败："+e.getMessage());
        }

    }


    @RequestMapping(path = "/login",method = RequestMethod.POST)
    public String login(String username, String password, String code,
                        boolean rememberme,Model model, HttpSession session,
                        HttpServletResponse response,@CookieValue("kaptchaOwner") String kaptchaOwner){


        /**
         * //检查验证码
         *         String kaptcha = (String)session.getAttribute("kaptcha");
         *   重构方法，不从session里面取。
         *   改成从redis里面取
         */

        String kaptcha =null;
        if(StringUtils.isNotBlank(kaptchaOwner)){
            String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }



        if(StringUtils.isBlank(code)||StringUtils.isBlank(kaptcha)||!kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg","验证码不正确");

            return "site/login";
        }

        //检查账号密码
        long expiredSeconds = rememberme?REMEMBER_EXPIRED_SECOND:DEFAULT_EXPIRED_SECOND;
        Map<String,Object> map = userService.login(username,password,expiredSeconds);
        if(map.containsKey("ticket")){

            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);

            int maxage = (int)expiredSeconds;
            cookie.setMaxAge( maxage);

            response.addCookie(cookie);


            return "redirect:/index";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("usernameMsg",map.get("usernameMsg"));

            System.out.println(map.get("passwordMsg"));


            return "site/login";
        }
    }



    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout( @CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/login";
    }

















}
