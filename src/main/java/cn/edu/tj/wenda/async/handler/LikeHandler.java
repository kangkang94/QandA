package cn.edu.tj.wenda.async.handler;

import cn.edu.tj.wenda.async.EventHandler;
import cn.edu.tj.wenda.async.EventModel;
import cn.edu.tj.wenda.async.EventType;
import cn.edu.tj.wenda.model.HostHolder;
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
public class LikeHandler implements EventHandler{
    @Autowired
     MessageService messageService;
     @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;


    @Override
    public void doHandle(EventModel model) {
        Message message = new Message();
        message.setFromId(WendaUtil.SYSTEM_USERID);
        message.setToId(model.getEntityOwnerId());
        message.setCreatedDate(new Date());

        User user = userService.getUser(model.getActorId());
        message.setContent("用户"+user.getName()+"赞了你的评论,http://localhost:8080/question" + model.getExt("question"));

        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
