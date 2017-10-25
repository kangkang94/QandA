package cn.edu.tj.wenda.service;

import cn.edu.tj.wenda.dao.LoginTicketDao;
import cn.edu.tj.wenda.dao.UserDao;
import cn.edu.tj.wenda.model.LoginTicket;
import cn.edu.tj.wenda.model.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.edu.tj.wenda.utils.WendaUtil;

import java.util.*;

/**
 * Created by kang on 2017/10/25.
 */
@Service
public class UserService {
    @Autowired
    UserDao userDao;

    @Autowired
    LoginTicketDao loginTicketDao;
    public User getUser(int id){
        return userDao.selectById(id);
    }

    public User selectUserByName(String name){
        return userDao.selectByName(name);
    }

    public Map<String,String> register(String username,String password){
        Map<String,String>  map = new HashMap<String,String>();
        if(StringUtils.isBlank(username)){
            map.put("msg","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("msg","密码不能为空");
            return map;
        }

        User user = userDao.selectByName(username);

        if(user != null){
            map.put("msg","用户名已被注册");
            return map;
        }

        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setPassword(WendaUtil.MD5(password + user.getSalt()));

        userDao.addUser(user);
        //为每个用户下发一个唯一的ticket，用于区分用户登录状态
        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }

    public Map<String,String> login(String username,String password) {
        Map<String,String>  map = new HashMap<String,String>();
        if(StringUtils.isBlank(username)){
            map.put("msg","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("msg","密码不能为空");
            return map;
        }

        User user = userDao.selectByName(username);

        if(user == null){
            map.put("msg","用户名不存在");
            return map;
        }
        if(!WendaUtil.MD5(password + user.getSalt()).equals(user.getPassword())){
            map.put("msg","密码错误");
            return map;
        }

        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;

    }

    public String addLoginTicket(int userId){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        loginTicket.setStatus(0);
        Date now = new Date();
        now.setTime(3600*24*100+now.getTime());
        loginTicket.setExpired(now);

        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));

        loginTicketDao.addTicket(loginTicket);
        return loginTicket.getTicket();
    }

    public void logout(String ticket){
        loginTicketDao.updateStatus(ticket,1);
    }

}
