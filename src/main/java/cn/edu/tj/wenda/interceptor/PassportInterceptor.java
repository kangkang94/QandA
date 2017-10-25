package cn.edu.tj.wenda.interceptor;

import cn.edu.tj.wenda.dao.LoginTicketDao;
import cn.edu.tj.wenda.dao.UserDao;
import cn.edu.tj.wenda.model.HostHolder;
import cn.edu.tj.wenda.model.LoginTicket;
import cn.edu.tj.wenda.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by kang on 2017/10/25.
 */
@Component
public class PassportInterceptor implements HandlerInterceptor{
    @Autowired
    HostHolder hostHolder;
    @Autowired
    LoginTicketDao loginTicketDao;
    @Autowired
    UserDao userDao;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String ticket = null;
        if(httpServletRequest.getCookies()!=null){
            for(Cookie cookie : httpServletRequest.getCookies()){
                if(cookie.getName().equals("ticket")){
                    ticket = cookie.getValue();
                    break;
                }
            }
        }

        LoginTicket loginTicket = loginTicketDao.selectByTicket(ticket);
        if(loginTicket == null || loginTicket.getStatus() == 1 || loginTicket.getExpired().before(new Date())){//过期时间
            return true;
        }
        //将当前线程得到的user放入HostHolder
        User user = userDao.selectById(loginTicket.getUserId());
        hostHolder.setUser(user);
        return true;//放行
    }
    //页面渲染前
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        if(modelAndView!=null && hostHolder.getUser()!=null)
            modelAndView.addObject("user",hostHolder.getUser());
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        hostHolder.clear();
    }
}
