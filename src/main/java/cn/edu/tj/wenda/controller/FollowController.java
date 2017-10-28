package cn.edu.tj.wenda.controller;

import cn.edu.tj.wenda.async.EventModel;
import cn.edu.tj.wenda.async.EventProducer;
import cn.edu.tj.wenda.async.EventType;
import cn.edu.tj.wenda.model.*;
import cn.edu.tj.wenda.service.CommentService;
import cn.edu.tj.wenda.service.FollowService;
import cn.edu.tj.wenda.service.QuestionService;
import cn.edu.tj.wenda.service.UserService;
import cn.edu.tj.wenda.utils.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kang on 2017/10/28.
 */
@Controller
public class FollowController {
    @Autowired
    FollowService followService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    QuestionService questionService;
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;


    //关注用户
    @RequestMapping(value = "/followUser",method = RequestMethod.POST)
    @ResponseBody
    public String followUser(@RequestParam("userId") int userId){
        //未登陆
        if (hostHolder.getUser() == null){
            return WendaUtil.getJSONString(999);
        }
        //我去关注某一用户实体
        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_USER,userId);

        //异步发送站内信
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUser().getId()).setEntityType(EntityType.ENTITY_USER)
                .setEntityId(userId).setEntityOwnerId(userId));
        //返回我关注的人数
        return WendaUtil.getJSONString(ret ? 0 : 1,String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(),EntityType.ENTITY_USER)));
    }

    //取消关注用户
    @RequestMapping(value = "/unfollowUser",method = RequestMethod.POST)
    @ResponseBody
    public String unfollowUser(@RequestParam("userId") int userId){
        //未登陆
        if (hostHolder.getUser() == null){
            return WendaUtil.getJSONString(999);
        }
        //我取消关注某一用户实体
        boolean ret = followService.unFollow(hostHolder.getUser().getId(), EntityType.ENTITY_USER,userId);

        /*//异步发送站内信
        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUser().getId()).setEntityType(EntityType.ENTITY_USER)
                .setEntityId(userId).setEntityOwnerId(userId));*/
        //返回我关注的人数
        return WendaUtil.getJSONString(ret ? 0 : 1,String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(),EntityType.ENTITY_USER)));
    }


    //关注问题
    @RequestMapping(value = "/followQuestion",method = RequestMethod.POST)
    @ResponseBody
    public String followQuestion(@RequestParam("questionId") int questionId){
        //未登陆
        if (hostHolder.getUser() == null){
            return WendaUtil.getJSONString(999);
        }

        Question q= questionService.getQuestion(questionId);
        if (q == null){
            return WendaUtil.getJSONString(1,"问题不存在");
        }


        //我去关注某一问题实体
        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION,questionId);

        //异步发送站内信
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUser().getId()).setEntityType(EntityType.ENTITY_QUESTION)
                .setEntityId(questionId).setEntityOwnerId(q.getUserId()));


        Map<String,Object> info = new HashMap<>();
        //因为是我关注的问题，所以返回我的相关信息
        info.put("id",hostHolder.getUser().getId());
        info.put("name",hostHolder.getUser().getName());
        info.put("headUrl",hostHolder.getUser().getHeadUrl());
        //该问题的粉丝有多少
        info.put("count",followService.getFollowerCount(EntityType.ENTITY_QUESTION,questionId));
        return WendaUtil.getJSONString(ret ? 0 : 1,info);
    }


    //取消关注问题
    @RequestMapping(value = "/unfollowQuestion",method = RequestMethod.POST)
    @ResponseBody
    public String unfollowQuestion(@RequestParam("questionId") int questionId){
        //未登陆
        if (hostHolder.getUser() == null){
            return WendaUtil.getJSONString(999);
        }

        Question q= questionService.getQuestion(questionId);
        if (q == null){
            return WendaUtil.getJSONString(1,"问题不存在");
        }


        //我取消关注某一问题实体
        boolean ret = followService.unFollow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION,questionId);

        /*//异步发送站内信
        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUser().getId()).setEntityType(EntityType.ENTITY_QUESTION)
                .setEntityId(questionId).setEntityOwnerId(q.getUserId()));*/


        Map<String,Object> info = new HashMap<>();
        //因为是我取消关注的问题，所以返回我的相关信息
        info.put("id",hostHolder.getUser().getId());
        info.put("name",hostHolder.getUser().getName());
        info.put("headUrl",hostHolder.getUser().getHeadUrl());
        //该问题的粉丝有多少
        info.put("count",followService.getFollowerCount(EntityType.ENTITY_QUESTION,questionId));
        return WendaUtil.getJSONString(ret ? 0 : 1,info);
    }



    private List<ViewObject> getUsersInfo(int localUserId,List<Integer> userIds){
        List<ViewObject> userInfos = new ArrayList<>();
        for (int uid : userIds){
            User user = userService.getUser(uid);
            if (user == null){
                continue;
            }
            ViewObject vo = new ViewObject();
            vo.set("user",user);
            vo.set("commentCount",commentService.getUserCommentCount(uid));
            //获取我的粉丝
            vo.set("followerCount",followService.getFollowerCount(EntityType.ENTITY_USER,uid));
            //获取我关注的人数
            vo.set("followeeCount",followService.getFolloweeCount(uid,EntityType.ENTITY_USER));
            if (localUserId !=0){
                //我是否已经关注了该用户
                vo.set("followed",followService.isFollower(localUserId,EntityType.ENTITY_USER,uid));
            }else {
                vo.set("followed",false);
            }
            userInfos.add(vo);
        }
        return userInfos;
    }


    //我的粉丝
    @RequestMapping(value = "/user/{uid}/followers",method = RequestMethod.GET)
    public String followers(Model model,@PathVariable("uid") int userId){


        List<Integer> followerIds = followService.getFollowers(EntityType.ENTITY_USER,userId,0,10);
        if (hostHolder.getUser()!=null){
            model.addAttribute("followers",getUsersInfo(hostHolder.getUser().getId(),followerIds));
        }else {
            model.addAttribute("followers",getUsersInfo(0,followerIds));
        }

        model.addAttribute("curUser",userService.getUser(userId));
        model.addAttribute("followerCount",followService.getFollowerCount(EntityType.ENTITY_USER,userId));
        return "followers";
    }

    //我关注的人
    @RequestMapping(value = "/user/{uid}/followees",method = RequestMethod.GET)
    public String followees(Model model,@PathVariable("uid") int userId){
        List<Integer> followeeIds = followService.getFollowees(userId,EntityType.ENTITY_USER,0,10);
        if (hostHolder.getUser()!=null){
            model.addAttribute("followees",getUsersInfo(hostHolder.getUser().getId(),followeeIds));
        }else {
            model.addAttribute("followees",getUsersInfo(0,followeeIds));
        }

        model.addAttribute("curUser",userService.getUser(userId));
        model.addAttribute("followeeCount",followService.getFolloweeCount(userId,EntityType.ENTITY_USER));
        return "followees";
    }
}
