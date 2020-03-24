package com.ennie.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.ennie.community.Util.CommunityUtil;
import com.ennie.community.service.AlphaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired
    AlphaService alphaService;

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "hello spring boot";
    }


    @RequestMapping("/data")
    @ResponseBody
    public String getData(){
        return alphaService.find();
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse reponse){
        //通过request对象，获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> emuneration = request.getHeaderNames();
        while(emuneration.hasMoreElements()){
            String name = emuneration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name+":  "+value);
        }
        System.out.println(request.getParameter("code"));


        //使用response对象
        //返回响应数据
        //设置返回数据对象的类型
        reponse.setContentType("text/html;charset=utf-8");
        try (PrintWriter writer = reponse.getWriter();){
            writer.write("<h1>牛客网</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //获取请求中的参数的方法
    //在函数中传入参数，参数名和url中包含的参数一致，即可得到
    //可以用@RequestParam做进一步的限制
    @RequestMapping(path = "/students",method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,
            @RequestParam(name = "current", required = false, defaultValue = "10") int limit){

        System.out.println(current);
        System.out.println(limit);

        return "some students";
    }



    //另一种获取request中的参数的方法
    @RequestMapping(path = "/student/{id}",method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id){
        return "a student"+ " "+ id;
    }



    //获取Post请求数据的方法
    //更简单，和Get类似
    //参数名 == 表单名 就可直接传入
    @RequestMapping(path = "/student",method = RequestMethod.POST)
    @ResponseBody
    public String addStudent(String name, int age){
        return "success     :" + name;
    }




    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView saveTeacher(){
        ModelAndView mov = new ModelAndView();
        mov.addObject("name","ennie");
        mov.addObject("age",18);
        mov.setViewName("/view/teacher.html");
        return mov;
    }




    //另一种返回动态页面的方式
    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String school(Model model){
        model.addAttribute("name","北京大学");
        model.addAttribute("age",80);
        return "/view/teacher";
    }



    //试验返回json格式的数据
    //看到responsebody后会自动转换
    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getEmp(){
        Map<String,Object> map = new HashMap<>();
        map.put("name","张三");
        map.put("age",8);
        return map;
    }



    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getEmps(){
        List <Map<String, Object>> list = new ArrayList<>();



        Map<String,Object> map = new HashMap<>();
        map.put("name","张三");
        map.put("age",8);
        list.add(map);

        map = new HashMap<>();
        map.put("name","王五");
        map.put("age",99);
        list.add(map);



        return list;
    }



    //cookie相关示例
    @RequestMapping(path = "/cookie/set", method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        //设置作用范围
        cookie.setPath("/community/alpha");
        //设置生效时间   单位是秒
        cookie.setMaxAge(60*10);
        response.addCookie(cookie);
        return "set cookie";
    }


    @RequestMapping(path = "/cookie/get", method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String cookie){
        return cookie;
    }



    //Session相关demo
    @RequestMapping(path = "/session/set", method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session){
        session.setAttribute("name","test");
        session.setAttribute("id",1);
        return "set Session";
    }


    @RequestMapping(path = "/session/get", method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session){
        String name = (String)session.getAttribute("name");
        int id = (int)session.getAttribute("id");
        System.out.println(name);
        System.out.println(id);
        return "get Session";
    }

    //ajax示例
    @RequestMapping(path = "/ajax", method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name , int age){
        System.out.println(name);
        System.out.println(age);

        return CommunityUtil.getJsonString(0,"接收成功");
    }



    @RequestMapping(path = "/testerror500")
    public String testerror500(){
        Integer.valueOf("abc");
        return "/";
    }





}
