package com.ennie.community.service;


import com.ennie.community.Util.CommunityConstant;
import com.ennie.community.Util.RedisKeyUtil;
import com.ennie.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class FollowService implements CommunityConstant {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    UserService userService;

    //关注
    public void follow(int userId, int entityType, int entityId){

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                //某个用户关注的实体
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
                //某个实体的粉丝
                String followerkey = RedisKeyUtil.getFollowerKey(entityType,entityId);

                redisOperations.multi();
                //用户关注的实体
                redisOperations.opsForZSet().add(followeeKey,entityId, System.currentTimeMillis());
                //某实体的粉丝
                redisOperations.opsForZSet().add(followerkey,userId, System.currentTimeMillis());

                return redisOperations.exec();
            }
        });

    }




    //取消关注
    public void unfollow(int userId, int entityType, int entityId){

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                //某个用户关注的实体
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
                //某个实体的粉丝
                String followerkey = RedisKeyUtil.getFollowerKey(entityType,entityId);

                redisOperations.multi();
                //用户关注的实体
                redisOperations.opsForZSet().remove(followeeKey,entityId);
                //某实体的粉丝
                redisOperations.opsForZSet().remove(followerkey,userId);

                return redisOperations.exec();
            }
        });

    }


    //查询某用户关注的实体的数量
    public long findFolloweeCount(int userId, int entityType){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }




    //查询某实体的粉丝数量
    public long findFollowerCount(int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }




    //查询当前用户是否关注该实体
    public boolean hasFollowed(int userId, int entityType, int entityId){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().score(followeeKey,entityId)!=null;

    }



    //查询某个用户关注的人
    public List<Map<String,Object>> findFollowees(int userId, int offset, int limit){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,ENTITY_TYPE_USER);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey,offset,offset+limit-1);

        //判断数据是否为空
        if(targetIds == null){
            return null;
        }

        List<Map<String,Object>> list = new ArrayList<>();

        for(Integer targetId:targetIds){
            Map<String,Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user",user);

            long followTime = redisTemplate.opsForZSet().score(followeeKey,targetId).longValue();
            map.put("followTime",new Date(followTime));

            list.add(map);
        }

        return list;

    }




    //查询某用户的粉丝
    public List<Map<String,Object>> findFollowers(int userId, int offset, int limit){
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER,userId);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey,offset,offset+limit-1);

        //判断数据是否为空
        if(targetIds == null){
            return null;
        }

        List<Map<String,Object>> list = new ArrayList<>();

        for(Integer targetId:targetIds){
            Map<String,Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user",user);

            long followTime = redisTemplate.opsForZSet().score(followerKey,targetId).longValue();
            map.put("followTime",new Date(followTime));

            list.add(map);
        }

        return list;

    }

}
