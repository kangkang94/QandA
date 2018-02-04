package cn.edu.tj.wenda.service;


import cn.edu.tj.wenda.dao.FeedDao;
import cn.edu.tj.wenda.model.Feed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by kang on 2017/11/23.
 */
@Service
public class FeedService {
    @Autowired
    FeedDao feedDao;

    public Feed getById(int id){
        return feedDao.getFeedById(id);
    }

    public List<Feed> getUserFeeds(int maxId,List<Integer> userIds ,int count){
        return feedDao.selectUserFeeds(userIds,maxId,count);
    }

    public int addFeed(Feed feed){
        return feedDao.addFeed(feed) > 0 ? feed.getId() : 0;
    }

}
