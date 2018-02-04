package cn.edu.tj.wenda.model;

import org.springframework.stereotype.Component;

/**
 * Created by kang on 2017/10/16.
 */
//注入到IoC中
@Component
public class HostHolder {
    //如果将User存储在该类中，并将该类注入IoC中，就可以在Service或其他层中调用
    //每个线程绑定不同的User
    private static ThreadLocal<User> users = new ThreadLocal<User>();

    public User getUser(){
        return users.get();
    }

    public void setUser(User user){
        users.set(user);
    }

    public void clear(){
        users.remove();
    }
}
