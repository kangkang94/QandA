package cn.edu.tj.wenda.controller;

import cn.edu.tj.wenda.model.HostHolder;
import cn.edu.tj.wenda.model.Message;
import cn.edu.tj.wenda.model.User;
import cn.edu.tj.wenda.model.ViewObject;
import cn.edu.tj.wenda.service.MessageService;
import cn.edu.tj.wenda.service.UserService;
import cn.edu.tj.wenda.utils.WendaUtil;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.nio.cs.ext.MS874;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by kang on 2017/11/18.
 */
@Controller
public class MessageController {
    @Autowired
    HostHolder hostHolder;
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageController.class);

    @RequestMapping(value = "/msg/addMessage", method = RequestMethod.POST)
    @ResponseBody
    public String addMessage(@RequestParam("toName") String toName, @RequestParam("content") String content) {
        try {
            if (hostHolder.getUser() == null) {
                return WendaUtil.getJSONString(999, "未登陆");
            }

            User user = userService.selectUserByName(toName);
            if (user == null) {
                return WendaUtil.getJSONString(1, "用户不存在");
            }

            Message message = new Message();
            message.setContent(content);
            message.setCreatedDate(new Date());
            message.setFromId(hostHolder.getUser().getId());
            message.setToId(user.getId());

            messageService.addMessage(message);
            return WendaUtil.getJSONString(0);

        } catch (Exception e) {
            LOGGER.error("发送消息失败" + e.getMessage());
            return WendaUtil.getJSONString(1, "发送消息失败");
        }

    }

    @RequestMapping(value = "/msg/list", method = RequestMethod.GET)
    public String getConversationList(Model model) {
        try {
            if (hostHolder.getUser() == null){
                return "redirect:/reglogin";
            }
            int localUserId = hostHolder.getUser().getId();
            List<Message> conversationList = messageService.getConversationList(localUserId,0,10);
            List<ViewObject> conversations = new ArrayList<>();
            for (Message message : conversationList){
                ViewObject vo = new ViewObject();
                vo.set("conversation",message);
                int targetId = message.getFromId()==localUserId ? message.getToId() : message.getFromId();
                vo.set("user",userService.getUser(targetId));
                vo.set("unread",messageService.getConversationUnreadCount(localUserId,message.getConversationId()));
                conversations.add(vo);
            }
            model.addAttribute("conversations",conversations);

        }catch (Exception e){
            LOGGER.error("消息列表获取失败" + e.getMessage());
        }



        return "letter";

    }

    @RequestMapping(value = "/msg/detail", method = RequestMethod.GET)
    public String getConversationDetail(Model model, @RequestParam("conversationId") String conversationId) {

        try {
            List<Message> messageList = messageService.getConversationDetail(conversationId, 0, 10);
            List<ViewObject> messages = new ArrayList<>();
            for (Message message : messageList) {
                ViewObject vo = new ViewObject();
                vo.set("message", message);
                vo.set("user", userService.getUser(message.getFromId()));
                int localUserId = hostHolder.getUser().getId();
                if (message.getToId() == localUserId){
                    //当前用户为toId时，将消息设为已读
                    messageService.updateHasRead(message.getToId(),conversationId);
                }
                messages.add(vo);
            }
            model.addAttribute("messages", messages);

        } catch (Exception e) {
            LOGGER.error("消息详情打开失败" + e.getMessage());
        }


        return "letterDetail";

    }

}
