package cn.edu.tj.wenda.service;

import cn.edu.tj.wenda.utils.JedisAdapter;
import cn.edu.tj.wenda.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by kang on 2017/11/22.
 */
@Service
public class FollowService {
    @Autowired
    JedisAdapter jedisAdapter;


    //关注功能
    public boolean follow(int userId,int entityType,int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);

        Date date = new Date();
        Jedis jedis = jedisAdapter.getJedis();
        //开启事务
        Transaction transaction = jedisAdapter.multi(jedis);
        //该实体的粉丝里添加我
        transaction.zadd(followerKey,date.getTime(),String.valueOf(userId));
        //我关注的实体里添加该实体
        transaction.zadd(followeeKey,date.getTime(),String.valueOf(entityId));

        List<Object> ret = jedisAdapter.exec(transaction,jedis);

        return ret.size()==2 && (Long)ret.get(0)>0 && (Long)ret.get(1)>0;
    }


    //取消关注功能
    public boolean unFollow(int userId,int entityType,int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);

        Date date = new Date();
        Jedis jedis = jedisAdapter.getJedis();
        //开启事务
        Transaction transaction = jedisAdapter.multi(jedis);
        //该实体的粉丝里删除我
        transaction.zrem(followerKey,String.valueOf(userId));
        //我关注的实体里删除该实体
        transaction.zrem(followeeKey,String.valueOf(entityId));

        List<Object> ret = jedisAdapter.exec(transaction,jedis);

        return ret.size()==2 && (Long)ret.get(0)>0 && (Long)ret.get(1)>0;
    }


    //获取某实体的粉丝list
    public List<Integer> getFollowers(int entityType,int entityId,int count){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followerKey,0,count));

    }
    public List<Integer> getFollowers(int entityType,int entityId,int offset,int count){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followerKey,offset,offset+count));

    }


    //获取我关注的实体list
    public List<Integer> getFollowees(int userId,int entityType,int count){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey,0,count));

    }
    public List<Integer> getFollowees(int userId,int entityType,int offset,int count){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey,offset,offset+count));

    }


    //获取某实体的粉丝数量
    public long getFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zcard(followerKey);
    }


    //获取我关注的实体数量
    public long getFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return jedisAdapter.zcard(followeeKey);
    }


    //从string的set转为integer的list
    private List<Integer> getIdsFromSet(Set<String> idSet){
        List<Integer> ids = new ArrayList<>();
        for (String str : idSet){
            ids.add(Integer.parseInt(str));
        }

        return ids;
    }


    //判断某用户是否关注了某一实体
    public boolean isFollower(int userId,int entityType,int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zscore(followerKey,String.valueOf(userId))!=null;
    }
}
