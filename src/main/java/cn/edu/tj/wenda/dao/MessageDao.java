package cn.edu.tj.wenda.dao;

import cn.edu.tj.wenda.model.Comment;
import cn.edu.tj.wenda.model.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by kang on 2017/11/23.
 */
@Mapper
public interface MessageDao {
    //接口通过自动生成的代理类实现
    String TABLE_NAME = " message ";
    String INSERT_FIELDS = " from_id, to_id, content, created_date, has_read, conversation_id ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;


    @Insert({"insert into ", TABLE_NAME , "(" ,INSERT_FIELDS , ") values (#{fromId},#{toId},#{content},#{createdDate},#{hasRead},#{conversationId})"})
    int addMessage(Message message);

    @Select({"select ", SELECT_FIELDS ," from" , TABLE_NAME , "where conversation_id=#{conversationId} order by created_date desc limit #{offset},#{limit}"})
    List<Message> getConversationDetail(@Param("conversationId") String conversationId,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);


    // select message.*,tt.cnt
    // from message,(select conversation_id,count(id) as cnt from message group by conversation_id) tt
    // WHERE created_date in (select max(created_date) from message group by conversation_id) and message.conversation_id=tt.conversation_id
    // order by created_date DESC
    // limit #{offset},#{limit};
    //cnt无法映射到message model中，所以用id(id在list中没用)
    @Select({"select from_id, to_id, content, created_date, has_read, message.conversation_id, tt.id from message,(select conversation_id,count(id) as id from message group by conversation_id) tt WHERE created_date in (select max(created_date) from message group by conversation_id) and message.conversation_id=tt.conversation_id and (from_id=#{userId} or to_id=#{userId}) order by created_date desc limit #{offset},#{limit}"})
    List<Message> getConversationList(@Param("userId") int userId,
                                      @Param("offset") int offset,
                                      @Param("limit") int limit);

    @Select({"select count(id) from ", TABLE_NAME ,"where has_read = 0 and conversation_id=#{conversationId} and to_id = #{userId}"})
    int getConversationUnreadCount(@Param("userId") int userId,@Param("conversationId") String conversationId);

    @Update({"update ", TABLE_NAME ,"set has_read=1 where conversation_id=#{conversationId} and to_id = #{userId}"})
    int updateHasRead(@Param("userId") int userId,@Param("conversationId") String conversationId);
}
