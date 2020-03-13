package com.ennie.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration
public class AlphaConfig {
    //该方法返回的对象将会被装配到容器里
    @Bean
    public SimpleDateFormat simpleDateFormat(){
        return new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
    }
}
