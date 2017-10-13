package cn.edu.tj.wenda.dao;

import cn.edu.tj.wenda.model.User;
import org.apache.ibatis.annotations.*;

/**
 * Created by kang on 2017/10/13.
 */
@Mapper
public interface UserDao {
    //接口通过自动生成的代理类实现
    String TABLE_NAME =  " user ";
    String INSERT_FIELDS = " name, password, salt, head_url ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, " (", INSERT_FIELDS,
            ") values(#{name},#{password},#{salt},#{headUrl})"})
    int addUser(User user);

    @Select({"select" , SELECT_FIELDS, " from " , TABLE_NAME ,"where id=#{id}"})
    User selectById(int id);

    @Update({"update" , TABLE_NAME , "set password=#{password} where id=#{id}"})
    void updatePassword(User user);

    @Delete({"delete from ", TABLE_NAME ,"where id=#{id}"})
    void deleteById(int id);

    @Select({"select" , SELECT_FIELDS, " from " , TABLE_NAME ,"where name=#{name}"})
    User selectByName(String name);
}