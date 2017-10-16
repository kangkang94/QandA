package cn.edu.tj.wenda.dao;

import cn.edu.tj.wenda.model.Question;

import org.apache.ibatis.annotations.*;
import org.apache.struts.chain.commands.servlet.SelectForward;

import java.util.List;

/**
 * Created by kang on 2017/10/16.
 */
@Mapper
public interface QuestionDao {
    //接口通过自动生成的代理类实现
    String TABLE_NAME = " question ";
    String INSERT_FIELDS = " title, content, user_id, created_date, comment_count ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, " (", INSERT_FIELDS,
            ") values(#{title},#{content},#{userId},#{createdDate},#{commentCount})"})
    int addQuestion(Question question);

    //通过xml的方式
    List<Question> selectLatestQuestions(@Param("userId") int userId,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);

    @Select({"select", SELECT_FIELDS," from ",TABLE_NAME," where id = #{id} "})
    Question selectById(int id);

    @Update({"update ", TABLE_NAME ,"set comment_count=#{commentCount} where id=#{id}"})
    int updateCommentCount(@Param("id") int id,@Param("commentCount") int commentCount);
}