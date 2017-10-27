package cn.edu.tj.wenda.async;


import cn.edu.tj.wenda.utils.JedisAdapter;
import cn.edu.tj.wenda.utils.RedisKeyUtil;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by kang on 2017/10/26.
 */
@Service
public class EventConsumer implements InitializingBean,ApplicationContextAware{
     private static final Logger LOGGER = LoggerFactory.getLogger(EventConsumer.class);
    private ApplicationContext applicationContext;
    private Map<EventType,List<EventHandler>> config = new HashMap<>();

    @Autowired
    JedisAdapter jedisAdapter;

    @Override
    public void afterPropertiesSet() throws Exception {
        //获得所有实现EventHandler接口的类，并获取它们监听的EventType，
        // 将它们放入config中对应EventType的List<EventHandler>中
        Map<String,EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if (beans!=null){
            for (Map.Entry<String,EventHandler> entry : beans.entrySet()){
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();
                for (EventType type : eventTypes){
                    if (!config.containsKey(type)){
                        config.put(type,new ArrayList<EventHandler>());
                    }
                    config.get(type).add(entry.getValue());
                }
            }
        }


        //开启线程，从阻塞队列中取出EventModel并处理
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //不断取出消息
                while (true){
                    String key = RedisKeyUtil.getEventQueueKey();
                    List<String> events = jedisAdapter.brpop(0,key);
                    for (String message : events){
                        if (message.equals(key)){//获取的是key，不是json，所以要continue
                            continue;
                        }

                        EventModel eventModel = JSON.parseObject(message,EventModel.class);

                        if (!config.containsKey(eventModel.getType())){
                            LOGGER.error("不能识别的事件");
                            continue;
                        }

                        for(EventHandler eventHandler : config.get(eventModel.getType())){
                            eventHandler.doHandle(eventModel);
                        }
                    }
                }
            }
        });
        thread.start();


    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
