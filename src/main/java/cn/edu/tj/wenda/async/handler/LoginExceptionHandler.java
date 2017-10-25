package cn.edu.tj.wenda.async.handler;

import cn.edu.tj.wenda.async.EventHandler;
import cn.edu.tj.wenda.async.EventModel;
import cn.edu.tj.wenda.async.EventType;
import cn.edu.tj.wenda.utils.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kang on 2017/10/25.
 */
@Component
public class LoginExceptionHandler implements EventHandler{
    @Autowired
    MailSender mailSender;

    @Override
    public void doHandle(EventModel model) {
        boolean ipException = false;
//        boolean ipException = true;

        //判断发现登陆异常
        if(ipException){
            Map<String,Object> map = new HashMap<>();
            map.put("username",model.getExt("username"));
            mailSender.sendWithHTMLTemplate(model.getExt("email"),"登陆ip异常","mails/login_exception.html",map);
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
