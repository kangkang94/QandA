package cn.edu.tj.wenda.controller;

import cn.edu.tj.wenda.async.EventModel;
import cn.edu.tj.wenda.async.EventProducer;
import cn.edu.tj.wenda.async.EventType;
import cn.edu.tj.wenda.model.*;
import cn.edu.tj.wenda.service.*;
import cn.edu.tj.wenda.utils.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by kang on 2017/10/16.
 */
@Controller
public class QuestionController {
    @Autowired
    QuestionService questionService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;
    @Autowired
    LikeService likeService;
    @Autowired
    FollowService followService;
    @Autowired
    EventProducer eventProducer;

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionController.class);

    @RequestMapping(value = "/question/add",method = RequestMethod.POST)
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title,@RequestParam("content") String content){
        try{
            Question question = new Question();
            question.setTitle(title);
            question.setContent(content);
            question.setCreatedDate(new Date());
            question.setCommentCount(0);
            if(hostHolder.getUser() != null){
                question.setUserId(hostHolder.getUser().getId());
            }else{
                question.setUserId(WendaUtil.ANONYMOUS_USERID);
//                return WendaUtil.getJSONString(999);
            }

            if (questionService.addQuestion(question) > 0){
                eventProducer.fireEvent(new EventModel(EventType.ADD_QUESTION)
                        .setActorId(question.getUserId()).setEntityId(question.getId())
                        .setExt("title", question.getTitle()).setExt("content", question.getContent()));
                return WendaUtil.getJSONString(0);
            }

        }catch (Exception e){
            LOGGER.error("提问失败" + e.getMessage());
        }
        return WendaUtil.getJSONString(1,"失败");
    }

    @RequestMapping("/question/{qid}")
    public String questionDetail(Model model, @PathVariable("qid") int qid){
        Question question = questionService.getQuestion(qid);
        model.addAttribute("question",question);
//        model.addAttribute("user",userService.getUser(question.getUserId()));
        List<Comment> commentList = commentService.getCommentByEntity(qid, EntityType.ENTITY_QUESTION);
        List<ViewObject> comments = new ArrayList<>();
        for (Comment comment : commentList){
            ViewObject vo = new ViewObject();
            vo.set("comment",comment);
            vo.set("user",userService.getUser(comment.getUserId()));

            if (hostHolder.getUser() == null){
                vo.set("liked",0);//0表示不赞也不踩，1赞，-1踩
            }else {
                vo.set("liked",likeService.getLikeStatus(hostHolder.getUser().getId(),EntityType.ENTITY_COMMENT,comment.getId()));
            }
            vo.set("likeCount",likeService.getLikeCount(EntityType.ENTITY_COMMENT,comment.getId()));

            comments.add(vo);
        }
        model.addAttribute("comments",comments);

        //关注该问题的用户
        List<Integer> users = followService.getFollowers(EntityType.ENTITY_QUESTION,qid,0,10);
        List<ViewObject> followUsers = new ArrayList<>();

        for (int uid : users){
            ViewObject vo = new ViewObject();
            User user = userService.getUser(uid);
            if (user == null){
                continue;
            }
            vo.set("id",uid);
            vo.set("name",user.getName());
            vo.set("headUrl",user.getHeadUrl());
            followUsers.add(vo);
        }
        model.addAttribute("followUsers",followUsers);
        if (hostHolder.getUser()!=null){
            model.addAttribute("followed",followService.isFollower(hostHolder.getUser().getId(),EntityType.ENTITY_QUESTION,qid));
        }else{
            model.addAttribute("followed",false);
        }


        return "detail";
    }
}
