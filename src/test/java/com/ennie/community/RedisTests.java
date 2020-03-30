package com.ennie.community;

import org.apache.commons.lang3.builder.ToStringExclude;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTests {


    @Autowired
    private RedisTemplate redisTemplate;



    @Test
    public void testStrings(){
        String redisKey = "test:count";
        redisTemplate.opsForValue().set(redisKey,1);
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey,1));
    }



    //访问hash类型的数据
    @Test
    public void testHash(){
        String redisKey = "test:user";
        redisTemplate.opsForHash().put(redisKey,"username","zhangsan");
        redisTemplate.opsForHash().put(redisKey,"id",1);

        System.out.println(redisTemplate.opsForHash().get(redisKey,"username"));
        System.out.println(redisTemplate.opsForHash().get(redisKey,"id"));

    }


    //访问list类型的数据
    @Test
    public void testLIst(){
        String redisKey = "test:ids";

        redisTemplate.opsForList().leftPush(redisKey,101);
        redisTemplate.opsForList().leftPush(redisKey,102);
        redisTemplate.opsForList().leftPush(redisKey,103);

        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().index(redisKey,0));
        System.out.println(redisTemplate.opsForList().range(redisKey,0,2));
        System.out.println();

    }


    //访问set类型的数据
    @Test
    public void testSet(){
        String redisKey = "test:teacher";

        redisTemplate.opsForSet().add(redisKey,"张飞","关羽","赵云","刘备");
        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        System.out.println(redisTemplate.opsForSet().members(redisKey));
    }


    //绑定key，以方便多次访问同一个key
    @Test
    public void testBoundKey(){
        String redisKey = "test:count";
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
        operations.increment();
        System.out.println(operations.get());
    }

    //事务管理
    @Test
    public void testTransactional(  ){

        Object obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String redisKey = "test:tx";
                redisOperations.multi();
                redisOperations.opsForSet().add(redisKey,"张三");
                redisOperations.opsForSet().add(redisKey,"李四");

                return redisOperations.exec();
            }
        });
        System.out.println(obj);
    }






}
