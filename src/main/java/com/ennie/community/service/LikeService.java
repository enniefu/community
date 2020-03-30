package com.ennie.community.service;

import com.ennie.community.Util.RedisKeyUtil;
import com.ennie.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    DiscussPostService discussPostService;


    //点赞
    public void like(int userId, int entityType, int entityId, int entityUserId){

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                //判断当前用户时候点过赞
                boolean isMember = redisOperations.opsForSet().isMember(entityLikeKey, userId);

                redisOperations.multi();

                //给帖子加赞同
                //给用户加赞
                if(isMember){
                    redisOperations.opsForSet().remove(entityLikeKey,userId);
                    redisOperations.opsForValue().decrement(userLikeKey);
                }else{
                    redisOperations.opsForSet().add(entityLikeKey,userId);
                    redisOperations.opsForValue().increment(userLikeKey);
                }

                redisOperations.exec();
                return null;
            }
        });

    }

    //统计点赞数量
    public long findEntityLikeCount( int entityType, int entityId ){
        String key = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().size(key);
    }


    //判断点赞状态
    public int findEntityLikeStatus(int userId, int entityType, int entityId){
        String key = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        boolean isMember = redisTemplate.opsForSet().isMember(key, userId);
        return isMember?1:0;
    }


    //统计用户被赞的数量
    public int findUserLikeCount(int userId){
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
            return count==null  ?0:count.intValue();

    }









}
