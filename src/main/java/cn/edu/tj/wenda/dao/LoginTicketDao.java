package cn.edu.tj.wenda.dao;

import cn.edu.tj.wenda.model.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * Created by kang on 2017/10/25.
 */
@Mapper
public interface LoginTicketDao {
     String TABLE_NAME = " login_ticket ";
     String INSERT_FIELDS = " user_id, expired, status, ticket ";
     String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, " (", INSERT_FIELDS,
            ") values(#{userId},#{expired},#{status},#{ticket})"})
    int addTicket(LoginTicket ticket);

    @Select({"select" , SELECT_FIELDS, " from " , TABLE_NAME ,"where ticket=#{ticket}"})
    LoginTicket selectByTicket(String ticket);

    @Update({"update" , TABLE_NAME , "set status=#{status} where ticket=#{ticket}"})
    void updateStatus(@Param("ticket") String ticket,@Param("status") int status);
}
