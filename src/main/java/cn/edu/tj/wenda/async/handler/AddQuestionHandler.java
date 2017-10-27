package cn.edu.tj.wenda.async.handler;

import cn.edu.tj.wenda.async.EventHandler;
import cn.edu.tj.wenda.async.EventModel;
import cn.edu.tj.wenda.async.EventType;
import cn.edu.tj.wenda.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by kang on 2017/10/27.
 */
@Component
public class AddQuestionHandler implements EventHandler{
     private static final Logger LOGGER = LoggerFactory.getLogger(AddQuestionHandler.class);
    @Autowired
    SearchService searchService;

    @Override
    public void doHandle(EventModel model) {
        try {//添加问题后，加入到solr中
            searchService.indexQuestion(model.getEntityId(), model.getExt("title"), model.getExt("content"));
        } catch (Exception e) {
            LOGGER.error("增加题目索引失败" + e.getMessage());
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.ADD_QUESTION);
    }
}
