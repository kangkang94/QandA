package cn.edu.tj.wenda;

import cn.edu.tj.wenda.model.User;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by kang on 2017/10/24.
 */
public class JSONTest {

    public static void mainx(String[] args) {
        User user = new User();
        user.setName("mao");
        user.setPassword("xixi");
        String data = JSON.toJSONString(user);
        System.out.println(data);



        JSONObject dataJSON = null;
        dataJSON = JSONObject.parseObject(data);

        String key = "User.class";
        String a = dataJSON.getString("aaaaa");
        System.out.println(dataJSON.getString("password"));

    }
}
