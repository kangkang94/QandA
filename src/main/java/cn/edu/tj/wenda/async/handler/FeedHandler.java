package cn.edu.tj.wenda.async.handler;

import cn.edu.tj.wenda.async.EventHandler;
import cn.edu.tj.wenda.async.EventModel;
import cn.edu.tj.wenda.async.EventType;
import cn.edu.tj.wenda.model.*;
import cn.edu.tj.wenda.service.*;
import cn.edu.tj.wenda.utils.JedisAdapter;
import cn.edu.tj.wenda.utils.RedisKeyUtil;
import cn.edu.tj.wenda.utils.WendaUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by mao on 2017/10/15.
 */
@Component
public class FeedHandler implements EventHandler{
    @Autowired
    FeedService feedService;
    @Autowired
    UserService userService;
    @Autowired
    QuestionService questionService;
    @Autowired
    CommentService commentService;
    @Autowired
    FollowService followService;
    @Autowired
    JedisAdapter jedisAdapter;

    @Override
    public void doHandle(EventModel model) {
        //构造新鲜事
        Feed feed = new Feed();
        feed.setCreatedDate(new Date());
        feed.setType(model.getType().getValue());
        feed.setUserId(model.getActorId());
        feed.setData(buildFeedData(model));
        if (feed.getData() == null){
            //用户不存在或问题不存在
            return;
        }
        //拉模式,拉用mysql
        feedService.addFeed(feed);

        //推模式，推用redis（底层的feed还是存储在mysql中，只不过是把feedid推到redis中）
        //获取所有的粉丝
        List<Integer> followers = followService.getFollowers(EntityType.ENTITY_USER,model.getActorId(),Integer.MAX_VALUE);
//        // 系统队列
//        followers.add(0);
        //将产生的新鲜事推给所有的粉丝
        for (int follower : followers){
            String timelineKey = RedisKeyUtil.getTimelineKey(follower);
            jedisAdapter.lpush(timelineKey,String.valueOf(feed.getId()));
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(new EventType[]{EventType.FOLLOW,EventType.COMMENT});
    }

    private String buildFeedData(EventModel model){
        Map<String,String> map = new HashMap<>();
        //获取产生新鲜事的用户
        User actor = userService.getUser(model.getActorId());
        if (actor == null){
            return null;
        }
        map.put("userId",String.valueOf(model.getActorId()));
        map.put("userName",actor.getName());
        map.put("userHead",actor.getHeadUrl());

        if (model.getType() == EventType.COMMENT ||(model.getType() ==EventType.FOLLOW && model.getEntityType() == EntityType.ENTITY_QUESTION)){
            Question q;
            if (model.getType() == EventType.COMMENT){
                int qid = commentService.getCommentById(model.getEntityId()).getEntityId();
                q = questionService.getQuestion(qid);
            }else {
                q = questionService.getQuestion(model.getEntityId());
            }
            if (q==null){
                return null;

            }
            map.put("questionId",String.valueOf(q.getId()));
            map.put("questionTitle",q.getTitle());

            return JSONObject.toJSONString(map);
        }

        return null;


    }
}
