package cn.edu.tj.wenda.controller;

import cn.edu.tj.wenda.model.EntityType;
import cn.edu.tj.wenda.model.Feed;
import cn.edu.tj.wenda.model.HostHolder;
import cn.edu.tj.wenda.service.FeedService;
import cn.edu.tj.wenda.service.FollowService;
import cn.edu.tj.wenda.utils.JedisAdapter;
import cn.edu.tj.wenda.utils.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kang on 2017/10/15.
 */
@Controller
public class FeedController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeedController.class);
    @Autowired
    FollowService followService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    FeedService feedService;
    @Autowired
    JedisAdapter jedisAdapter;

    //拉模式
    @RequestMapping("/pullfeeds")
    public String getPullFeeds(Model model){


        int localUserId = hostHolder.getUser() == null ? 0 : hostHolder.getUser().getId();
        List<Integer> followees = new ArrayList<>();
        if (localUserId != 0){
            //获取我关注的用户
            followees = followService.getFollowees(localUserId, EntityType.ENTITY_USER,Integer.MAX_VALUE);

        }

        List<Feed> feeds = feedService.getUserFeeds(Integer.MAX_VALUE,followees,10);
        model.addAttribute("feeds",feeds);
        return "feeds";

    }

    //推模式
    @RequestMapping("/pushfeeds")
    public String getPushFeeds(Model model){
        int localUserId = hostHolder.getUser() == null ? 0 : hostHolder.getUser().getId();
        //从redis中取出推给我的新鲜事
        List<String> feedIds = jedisAdapter.lrange(RedisKeyUtil.getTimelineKey(localUserId),0,10);
        List<Feed> feeds = new ArrayList<>();
        //必须是我关注的人，防止关注再取消关注后，还推送他的新鲜事。
        List<Integer> followees = followService.getFollowees(localUserId,EntityType.ENTITY_USER,Integer.MAX_VALUE);
        for (String feedId : feedIds){
            Feed feed =feedService.getById(Integer.parseInt(feedId));
            if (feed != null && followees.contains(feed.getUserId()))
                feeds.add(feed);
        }
        model.addAttribute("feeds",feeds);
        return "feeds";

    }
}
