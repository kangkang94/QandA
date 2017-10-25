package cn.edu.tj.wenda.controller;

import cn.edu.tj.wenda.async.EventModel;
import cn.edu.tj.wenda.async.EventProducer;
import cn.edu.tj.wenda.async.EventType;
import cn.edu.tj.wenda.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by kang on 2017/10/25.
 */
@Controller
public class LoginController {
    @Autowired
    UserService userService;

    @Autowired
    EventProducer eventProducer;

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping(path = "/reg/",method = RequestMethod.POST)
    public String reg(Model model,
                      HttpServletResponse response,
                      @RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam(value = "next" ,required = false) String next){
        try {
            Map<String,String> map = userService.register(username,password);
            if(map.containsKey("ticket")){
                Cookie cookie = new Cookie("ticket",map.get("ticket"));
                cookie.setPath("/");
                response.addCookie(cookie);
                //如果有next参数，即从其他页面跳转
                if(StringUtils.isNotBlank(next)){
                    return "redirect:" + next;
                }
                //如果没有出错，返回首页
                return "redirect:/";
            }else{
                model.addAttribute("msg",map.get("msg"));
                return "login";
            }
        }catch (Exception e){
            LOGGER.error("注册异常" + e.getMessage());
            return "login";
        }
    }



    @RequestMapping("/reglogin")
    public String reg(Model model,@RequestParam(value = "next" ,required = false) String next){
        model.addAttribute("next",next);
        return "login";
    }


    @RequestMapping(path = "/login/",method = RequestMethod.POST)
    public String login(Model model,
                        HttpServletResponse response,
                        @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "next" ,required = false) String next,
                        @RequestParam(value = "rememberme",defaultValue ="false") boolean rememberme){

        try {
            Map<String,String> map = userService.login(username,password);
            if(map.containsKey("ticket")){
                Cookie cookie = new Cookie("ticket",map.get("ticket"));
                cookie.setPath("/");
                response.addCookie(cookie);

                //登陆异常发送邮件
                eventProducer.fireEvent(new EventModel(EventType.LOGIN)
                        .setExt("email","523421291@qq.com").setExt("username",username)
                        .setActorId(userService.selectUserByName(username).getId()));


                //如果有next参数，即从其他页面跳转
                if(StringUtils.isNotBlank(next)){
                    return "redirect:" + next;
                }
                //如果没有出错，返回首页
                return "redirect:/";
            }else{
                model.addAttribute("msg",map.get("msg"));
                return "login";
            }
        }catch (Exception e){
            LOGGER.error("注册异常" + e.getMessage());
            return "login";
        }
    }



    @RequestMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/";
    }
}
