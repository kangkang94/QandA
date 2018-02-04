package cn.edu.tj.wenda.dao;


import cn.edu.tj.wenda.model.Feed;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by kang on 2017/10/15.
 */
@Mapper
public interface FeedDao {
    String TABLE_NAME = " feed ";
    String INSERT_FIELDS = " user_id, created_date, data, type ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;


    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS, ") values (#{userId},#{createdDate},#{data},#{type})"})
    int addFeed(Feed feed);

    @Select({"select ", SELECT_FIELDS ," from" , TABLE_NAME , "where id=#{id}"})
    Feed getFeedById(@Param("id") int id);

    //查询用户们产生的新鲜事,maxId一次的增量
    List<Feed> selectUserFeeds(@Param("userIds") List<Integer> userIds,@Param("maxId") int maxId,@Param("count") int count);
}
