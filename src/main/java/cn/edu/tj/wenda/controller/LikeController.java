package cn.edu.tj.wenda.controller;

import cn.edu.tj.wenda.async.EventModel;
import cn.edu.tj.wenda.async.EventProducer;
import cn.edu.tj.wenda.async.EventType;
import cn.edu.tj.wenda.model.Comment;
import cn.edu.tj.wenda.model.EntityType;
import cn.edu.tj.wenda.model.HostHolder;
import cn.edu.tj.wenda.service.CommentService;
import cn.edu.tj.wenda.service.LikeService;
import cn.edu.tj.wenda.utils.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by  kang on 2017/11/10.
 */
@Controller
public class LikeController {
    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    CommentService commentService;

    @RequestMapping(value = "/like",method = RequestMethod.POST)
    @ResponseBody
    public String like(@RequestParam("commentId") int commentId){
        if (hostHolder.getUser() == null){
            return WendaUtil.getJSONString(999);
            //999表示用户未登陆
        }

        Comment comment = commentService.getCommentById(commentId);
        eventProducer.fireEvent(new EventModel(EventType.LIKE).setActorId(hostHolder.getUser().getId())
                                .setEntityId(commentId).setEntityType(EntityType.ENTITY_COMMENT)
                                .setEntityOwnerId(comment.getUserId()).setExt("question",String .valueOf(comment.getEntityId())));


        //likeService.like()方法传入的entityId是comment的id，与数据库中comment表的entityId不同
        long likeCount = likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT,commentId);

        return WendaUtil.getJSONString(0,String.valueOf(likeCount));//0表示成功
    }

    @RequestMapping(value = "/dislike",method = RequestMethod.POST)
    @ResponseBody
    public String disLike(@RequestParam("commentId") int commentId){
        if (hostHolder.getUser() == null){
            return WendaUtil.getJSONString(999);
            //999表示用户未登陆
        }

        long likeCount = likeService.dislike(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT,commentId);

        return WendaUtil.getJSONString(0,String.valueOf(likeCount));//0表示成功
    }


}
