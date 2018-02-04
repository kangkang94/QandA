package cn.edu.tj.wenda.controller;

import cn.edu.tj.wenda.model.EntityType;
import cn.edu.tj.wenda.model.Question;
import cn.edu.tj.wenda.model.ViewObject;
import cn.edu.tj.wenda.service.FollowService;
import cn.edu.tj.wenda.service.QuestionService;
import cn.edu.tj.wenda.service.SearchService;
import cn.edu.tj.wenda.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kang on 2017/11/19.
 */
@Controller
public class SearchController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);
    @Autowired
    SearchService searchService;
    @Autowired
    FollowService followService;
    @Autowired
    UserService userService;
    @Autowired
    QuestionService questionService;


    //必须要开启solr，E:\solr-6.5.1\bin>solr start
    @RequestMapping(path = {"/search"}, method = {RequestMethod.GET})
    public String search(Model model, @RequestParam("q") String keyword,
                         @RequestParam(value = "offset", defaultValue = "0") int offset,
                         @RequestParam(value = "count", defaultValue = "10") int count) {
        try {
            List<Question> questionList = searchService.searchQuestion(keyword, offset, count,
                    /*"<em>", "</em>"*/"<mark>", "</mark>");
            List<ViewObject> vos = new ArrayList<>();
            for (Question question : questionList) {
                Question q = questionService.getQuestion(question.getId());
                ViewObject vo = new ViewObject();
                if (question.getContent() != null) {
                    q.setContent(question.getContent());
                }
                if (question.getTitle() != null) {
                    q.setTitle(question.getTitle());
                }
                vo.set("question", q);
                vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
                vo.set("user", userService.getUser(q.getUserId()));
                vos.add(vo);
            }            model.addAttribute("vos", vos);
            model.addAttribute("keyword", keyword);
        } catch (Exception e) {
            LOGGER.error("搜索评论失败" + e.getMessage());
        }
        return "result";
    }
}
