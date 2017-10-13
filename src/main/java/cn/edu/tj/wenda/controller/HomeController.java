package cn.edu.tj.wenda.controller;

import cn.edu.tj.wenda.model.*;
import cn.edu.tj.wenda.service.*;
import cn.edu.tj.wenda.utils.JedisAdapter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by kang on 2017/10/13.
 */
@Controller
public class HomeController {
    private static final Logger  LOGGER = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    UserService userService;
    @Autowired
    QuestionService questionService;
    @Autowired
    CommentService commentService;
    @Autowired
    FollowService followService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    LikeService likeService;
    @Autowired
    JedisAdapter jedisAdapter;

    @RequestMapping(path = {"/user/{userId}"},method = {RequestMethod.GET})
    public String userIndex(Model model, @PathVariable("userId") int userId){
        List<ViewObject> vos = getQuestions(userId,0,10);
        model.addAttribute("vos",vos);

        User user = userService.getUser(userId);
        ViewObject vo = new ViewObject();
        vo.set("user", user);
        vo.set("commentCount", commentService.getUserCommentCount(userId));
        vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        vo.set("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
        //添加用户获得的总赞数
        vo.set("totalLiked",likeService.getUserLikedCount(EntityType.ENTITY_COMMENT,userId));
        if (hostHolder.getUser() != null) {
            vo.set("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId));
        } else {
            vo.set("followed", false);
        }

        model.addAttribute("profileUser",vo);
        return "profile";
    }


    @RequestMapping(path = {"/","/index"},method = {RequestMethod.GET})
    public String index(Model model){
        List<ViewObject> vos = getQuestions(0,0,10);
//        List<ViewObject> vos = getQuestionsFromRedis();
//        List<ViewObject> vos = getQuestions();
        model.addAttribute("vos",vos);
        return "index";

    }

    private List<ViewObject> getQuestions(int userId,int offset,int limit) {
        List<Question> questionList = questionService.getLatestQuestions(userId,offset,limit);
        List<ViewObject> vos = new ArrayList<ViewObject>();
        for(Question question : questionList){
            ViewObject vo = new ViewObject();
            //首页不要让问题显示的太长
            if(question.getContent().length() > 300){
                question.setContent(question.getContent().substring(0,300)+"......");
            }
            vo.set("question",question);
            vo.set("user",userService.getUser(question.getUserId()));
            vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
            vos.add(vo);
        }
        return vos;
    }

/*********************************测试******************************************/
/*    //JMeter测试不访问数据库
    private List<Question> getQuestionList(){
        List<Question> questionList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Question q = new Question();
            q.setContent("content" + i);
            q.setTitle("title" + i);
            q.setUserId(i);
            q.setCreatedDate(new Date());
            q.setCommentCount(i);
            questionList.add(q);
        }
        return questionList;
    }
    //JMeter测试不访问数据库
    private List<ViewObject> getQuestions() {
        List<Question> questionList = getQuestionList();
        List<ViewObject> vos = new ArrayList<ViewObject>();
        for(Question question : questionList){
            ViewObject vo = new ViewObject();
            vo.set("question",question);
//            vo.set("user",userService.getUser(question.getUserId()));
//            vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
            vos.add(vo);
        }
        return vos;
    }

    //JMeter测试使用Redis
    private List<ViewObject> getQuestionsFromRedis() {
        //控制变量，与不访问数据库进行相同的操作
        getQuestionList();

        List<String> questions = jedisAdapter.lrange("question",0,9);
        List<Question> questionList = new ArrayList<>();
        for (String str : questions){
            questionList.add(JSON.parseObject(str,Question.class));
        }
        List<ViewObject> vos = new ArrayList<ViewObject>();
        for(Question question : questionList){
            ViewObject vo = new ViewObject();
            vo.set("question",question);
            vos.add(vo);
        }
        return vos;
    }
    //数据存入Redis
    @RequestMapping(path = {"/setQuestionToReids"},method = {RequestMethod.GET})
    @ResponseBody
    public String setQuestionToReids(){
        for (int i = 0; i < 10; i++) {
            Question q = new Question();
            q.setContent("content Redis" + i);
            q.setTitle("title Redis" + i);
            q.setUserId(i);
            q.setCreatedDate(new Date());
            q.setCommentCount(i);
            //向redis中存入数据
            jedisAdapter.lpush("question", JSONObject.toJSONString(q));
        }
        return "<h1>setQuestionToReids success</h1>";
    }

//    @RequestMapping(path = {"/"},method = {RequestMethod.GET})
//    public String indexNoSQL(){
//        return "indexNoSQL";
//    }*/

}
