package cn.edu.tj.wenda.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kang on 2017/10/13.
 */
public class ViewObject {
    //用于在velocity和controller之间传递数据
    private Map<String,Object> objs =  new HashMap<String,Object>();

    public void set(String key,Object value){
        objs.put(key,value);
    }
    public Object get(String key){
        return objs.get(key);
    }
}
