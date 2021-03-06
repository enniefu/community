package com.ennie.community.controller;

import com.ennie.community.Util.CommunityConstant;
import com.ennie.community.Util.CommunityUtil;
import com.ennie.community.Util.HostHolder;
import com.ennie.community.annotation.LoginRequired;
import com.ennie.community.entity.User;
import com.ennie.community.service.FollowService;
import com.ennie.community.service.LikeService;
import com.ennie.community.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Random;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    @Autowired
    FollowService followService;




    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }




    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage==null){
            model.addAttribute("error","您还未选择图片");
            return "/site/setting";
        }

        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);

        if(suffix.equals(fileName)){
            model.addAttribute("error","文件的格式不正确");
            return "/site/setting";
        }

        fileName = CommunityUtil.generateUUID()+suffix;
        File dest = new File(uploadPath+"/"+fileName);

        try {
            //存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败："+e.getMessage());
            throw new RuntimeException("上传文件失败。服务器发生异常。"+e);
        }

        //web访问路径
        //localhost:8089/community/user/header/xxx.png
        User user = hostHolder.getUser();
        userService.updateHeader(user.getId(),domain+contextPath+"/user/header/"+fileName);

        return "redirect:/index";
    }


    //通过流手动输出图片
    @RequestMapping(path="/header/{fileName}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //服务器本地路径
        fileName = uploadPath+"/"+fileName;

        //声明输出文件的格式
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);

        //响应图片
        response.setContentType("image/"+suffix);

        try (
                FileInputStream fis = new FileInputStream(fileName);
                ){
            OutputStream os = response.getOutputStream();

            byte[] buffer = new byte[1024];
            int b=0;
            while ((b= fis.read(buffer)) !=-1){
                os.write(buffer,0,b);
            }



        } catch (IOException e) {
            logger.error("读取头像失败："+e.getMessage());
        }


    }




    //前往忘记密码
    @RequestMapping(path = "/getForgetPassword", method = RequestMethod.GET)
    public String getForgetPassword(){
        return "/site/forget";
    }


    //获得激活码
    @RequestMapping(path = "/getPasswordCode", method = RequestMethod.GET)
    public String getPasswordCode(String email, HttpSession session,Model model){
        System.out.println(email);
        Map<String, Object> map = userService.forgetPassword(email);
        if(map!=null){
            System.out.println("邮箱错误");
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/forget";
        }

        Random random = new Random();

        String passwordCode = random.nextInt(9999)+"";
        System.out.println(passwordCode);
        session.setAttribute("passwordCode",passwordCode);
        model.addAttribute("passwordCode",passwordCode);

        //发送邮件
        ///////

        return "redirect:/site/forget";
    }

    //判断激活码是否正确并修改密码
    @RequestMapping(path = "/updatePassword", method = RequestMethod.POST)
    public String updatePassword(String passwordCode, String password,String email,Model model,HttpSession session){

        String realPasswordCode = (String) session.getAttribute("passwordCode");

        if(!realPasswordCode.equals(passwordCode)){
            model.addAttribute("passwordCodeMsg","验证码填写错误");
            return "redirect:/site/forget";
        }

        int result = userService.updatePassword(email,password);
        return "/index";
    }



    //在设置页面更新页码
    @RequestMapping(path = "/changePassword", method = RequestMethod.POST)
    public String changePassword(String newPassword, String oldPassword,Model model){
        System.out.println("oldPassword:   "+oldPassword);
        System.out.println("newPassword:   "+newPassword);
        User user = hostHolder.getUser();
        System.out.println("userController  user:   "+user);
        oldPassword = CommunityUtil.md5(oldPassword+user.getSalt());
        if (!oldPassword.equals(user.getPassword())){
            System.out.println("旧密码输入错误");
            model.addAttribute("passwordMsg","旧密码输入错误");
            return "/site/setting";
        }

        newPassword = CommunityUtil.md5(newPassword+user.getSalt());
        int result = userService.updatePassword(user.getEmail(),newPassword);

        return "redirect:/logout";
    }

    //个人主页的点赞数量
    @RequestMapping(path = "/profile/{userId}",method = RequestMethod.GET)
    public String getProfile(@PathVariable("userId") int userId, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在");
        }
        //用户
        model.addAttribute("user",user);

        //点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        System.out.println("likeCount:"+likeCount);
        model.addAttribute("likeCount",likeCount);



        //关注数量
        long followeeCount = followService.findFolloweeCount(userId,ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);
        //粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount",followerCount);
        //是否被当前用户关注
        boolean hasFollowed = false;

        //判断一下是否登陆
        if(hostHolder.getUser()!=null){
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
        }

        model.addAttribute("hasFollowed",hasFollowed);


        return "/site/profile";
    }



}
