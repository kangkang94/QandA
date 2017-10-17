package cn.edu.tj.wenda.dao;

import cn.edu.tj.wenda.model.Comment;
import com.google.common.collect.Table;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by kang on 2017/10/17.
 */
@Mapper
public interface CommentDao {
    //接口通过自动生成的代理类实现
    String TABLE_NAME = " comment ";
    String INSERT_FIELDS = " user_id, content, entity_id, created_date, entity_type, status ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;


    @Insert({"insert into ", TABLE_NAME , "(" ,INSERT_FIELDS , ") values (#{userId},#{content},#{entityId},#{createdDate},#{entityType},#{status})"})
    int addComment(Comment comment);

    @Select({"select ", SELECT_FIELDS ," from" , TABLE_NAME , "where entity_id=#{entityId} and entity_type=#{entityType} order by created_date desc"})
    List<Comment> selectCommentByEntity(@Param("entityId") int entityId,@Param("entityType") int entityType);

    @Select({"select count(id) from" , TABLE_NAME , "where entity_id=#{entityId} and entity_type=#{entityType}"})
    int getCommentCount(@Param("entityId") int entityId,@Param("entityType") int entityType);

    @Update({"update" , TABLE_NAME ," set status=#{status} where id=#{id}"})
    int updateStatus(@Param("id") int id,@Param("status") int status);


    @Select({"select ", SELECT_FIELDS ," from" , TABLE_NAME , "where id=#{id}"})
    Comment selectCommentById(@Param("id") int id);

    @Select({"select count(id) from" , TABLE_NAME , "where user_id=#{userId}"})
    int getUserCommentCount(@Param("userId") int userId);

    @Select({"select id from" , TABLE_NAME , "where user_id=#{userId}"})
    List<Integer> getUserComment(@Param("userId") int userId);
}
