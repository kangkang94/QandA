package cn.edu.tj.wenda.async;

import cn.edu.tj.wenda.utils.JedisAdapter;
import cn.edu.tj.wenda.utils.RedisKeyUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by kang on 2017/10/26.
 */
@Service
public class EventProducer {

    @Autowired
    JedisAdapter jedisAdapter;

    public boolean fireEvent(EventModel model){
        try {
            String json = JSONObject.toJSONString(model);
            String key = RedisKeyUtil.getEventQueueKey();
            //将事件模型转换为json后放入一个阻塞队列
            jedisAdapter.lpush(key,json);
            return true;
        }catch (Exception e ){
            return false;
        }
    }
}
