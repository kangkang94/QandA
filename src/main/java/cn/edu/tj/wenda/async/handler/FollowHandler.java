package cn.edu.tj.wenda.async.handler;

import cn.edu.tj.wenda.async.EventHandler;
import cn.edu.tj.wenda.async.EventModel;
import cn.edu.tj.wenda.async.EventType;
import cn.edu.tj.wenda.model.EntityType;
import cn.edu.tj.wenda.model.Message;
import cn.edu.tj.wenda.model.User;
import cn.edu.tj.wenda.service.MessageService;
import cn.edu.tj.wenda.service.UserService;
import cn.edu.tj.wenda.utils.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by kang on 2017/10/27.
 */
@Component
public class FollowHandler implements EventHandler{
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel model) {
        Message message = new Message();
        message.setFromId(WendaUtil.SYSTEM_USERID);
        message.setToId(model.getEntityOwnerId());
        message.setCreatedDate(new Date());

        User user = userService.getUser(model.getActorId());

        if (model.getEntityType() == EntityType.ENTITY_QUESTION){
            message.setContent("用户"+user.getName()+"关注了你的问题,http://localhost:8080/question" + model.getEntityId());
        }else if (model.getEntityType() == EntityType.ENTITY_USER){
            message.setContent("用户"+user.getName()+"关注了你,http://localhost:8080/user" + model.getActorId());
        }

        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.FOLLOW/*,EventType.UNFOLLOW*/);
    }
}
