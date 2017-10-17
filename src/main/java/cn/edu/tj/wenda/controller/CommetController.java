package cn.edu.tj.wenda.controller;

import cn.edu.tj.wenda.async.EventModel;
import cn.edu.tj.wenda.async.EventProducer;
import cn.edu.tj.wenda.async.EventType;
import cn.edu.tj.wenda.model.Comment;
import cn.edu.tj.wenda.model.EntityType;
import cn.edu.tj.wenda.model.HostHolder;
import cn.edu.tj.wenda.service.CommentService;
import cn.edu.tj.wenda.service.QuestionService;
import cn.edu.tj.wenda.utils.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * Created by kang on 2017/10/17.
 */
@Controller
public class CommetController {
    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    QuestionService questionService;

    @Autowired
    EventProducer eventProducer;
    private static final Logger LOGGER = LoggerFactory.getLogger(CommetController.class);

    @RequestMapping(value = "/addComment" , method = RequestMethod.POST)
    public String addComment(@RequestParam("questionId") int questionId,@RequestParam("content") String content){
        try {
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setCreatedDate(new Date());
            comment.setEntityId(questionId);
            comment.setEntityType(EntityType.ENTITY_QUESTION);

            if(hostHolder.getUser()!=null){
                comment.setUserId(hostHolder.getUser().getId());
            }else {
                comment.setUserId(WendaUtil.ANONYMOUS_USERID);
            }

            commentService.addComment(comment);

            int count = commentService.getCommentCount(comment.getEntityId(),comment.getEntityType());
            questionService.updateCommentCount(comment.getEntityId(),count);
            //发送评论的新鲜事
            eventProducer.fireEvent(new EventModel(EventType.COMMENT)
                    .setActorId(hostHolder.getUser().getId()).setEntityType(EntityType.ENTITY_COMMENT)
                    .setEntityId(comment.getId()).setEntityOwnerId(comment.getUserId()).setType(EventType.COMMENT));

        }catch (Exception e){
            LOGGER.error("评论失败" + e.getMessage());
        }
        return "redirect:/question/" + questionId;
    }
}
