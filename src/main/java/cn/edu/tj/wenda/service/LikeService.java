package cn.edu.tj.wenda.service;

import cn.edu.tj.wenda.model.EntityType;
import cn.edu.tj.wenda.utils.JedisAdapter;
import cn.edu.tj.wenda.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by kang on 2017/11/24.
 */
@Service
public class LikeService {
    @Autowired
    JedisAdapter jedisAdapter;
    @Autowired
    CommentService commentService;

    public long like(int userId,int entityType,int entityId){
        String likeKey = RedisKeyUtil.getLikeKey(entityType,entityId);
        jedisAdapter.sadd(likeKey,String.valueOf(userId));
        //不能同时又赞了，又踩
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType,entityId);
        jedisAdapter.srem(disLikeKey,String.valueOf(userId));
        //返回赞的人数
        return jedisAdapter.scard(likeKey);
    }
    public long dislike(int userId,int entityType,int entityId){
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType,entityId);
        jedisAdapter.sadd(disLikeKey,String.valueOf(userId));
        //不能同时又赞了，又踩
        String likeKey = RedisKeyUtil.getLikeKey(entityType,entityId);
        jedisAdapter.srem(likeKey,String.valueOf(userId));
        //返回赞的人数
        return jedisAdapter.scard(likeKey);
    }

    public long getLikeCount(int entityType,int entityId){
        String likeKey = RedisKeyUtil.getLikeKey(entityType,entityId);
        return jedisAdapter.scard(likeKey);
    }

    public int getLikeStatus(int userId,int entityType,int entityId){
        String likeKey = RedisKeyUtil.getLikeKey(entityType,entityId);
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType,entityId);
        if (jedisAdapter.sismember(likeKey,String.valueOf(userId))){
            return 1;
        }else if(jedisAdapter.sismember(disLikeKey,String.valueOf(userId))){
            return -1;
        }
        return 0;
    }

    public long getUserLikedCount(int entityType,int userId){
        if (entityType == EntityType.ENTITY_COMMENT){
            List<Integer> commentIds = commentService.getUserComment(userId);
            long userLikedCount = 0;
            for (int commentId : commentIds){
                userLikedCount += getLikeCount(entityType,commentId);
            }
            return userLikedCount;
        }
        return 0;
    }
}
