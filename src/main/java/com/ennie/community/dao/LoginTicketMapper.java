package com.ennie.community.dao;


import com.ennie.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
@Deprecated
public interface LoginTicketMapper {
    //插入
    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    //查询
    @Select({
            "select id,user_id,ticket,status,expired from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    //修改凭证状态
    @Update({

            "update login_ticket set status=#{status} where ticket = #{ticket}"
    })
    int updateStatus(String ticket,int status);

}
