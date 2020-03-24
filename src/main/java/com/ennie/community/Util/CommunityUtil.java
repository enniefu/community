package com.ennie.community.Util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

public class CommunityUtil {




    //生成一个随机字符串
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }


    //进行MD5加密
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }else{
            return DigestUtils.md5DigestAsHex(key.getBytes());
        }
    }


    //处理json字符串的方法
    public static String getJsonString(int code, String msg, Map<String,Object> map){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if(map!=null){
            for (String key : map.keySet()){
                json.put(key,map.get(key));
            }
        }
        return json.toJSONString();
    }


    public static String getJsonString(int code, String msg){
        return getJsonString(code,msg,null);
    }

    public static String getJsonString(int code){
        return getJsonString(code,null,null);
    }















}
