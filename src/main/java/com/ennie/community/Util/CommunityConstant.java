package com.ennie.community.Util;

public interface CommunityConstant {

    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 2;

    /**
     * 默认登陆凭证超时时间
     */
    long DEFAULT_EXPIRED_SECOND=3600*12;

    /**
     * 记住我时的登陆超时时间
     */
    long REMEMBER_EXPIRED_SECOND = 3600*12*100;

    /**
     * 实体类型：帖子
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * 实体类型：评论
     */
    int ENTITY_TYPE_COMMENT = 2;

}
