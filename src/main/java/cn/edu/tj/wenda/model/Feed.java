package cn.edu.tj.wenda.model;

import com.alibaba.fastjson.JSONObject;

import java.util.Date;

/**
 * Created by kang on 2017/10/15.
 */
public class Feed {
    private int id;
    //新鲜事类型
    private int type;
    //新鲜事的产生者
    private int userId;
    private Date createdDate;
    private String data;
    private JSONObject dataJSON = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        dataJSON = JSONObject.parseObject(data);
    }

    //根据转成json的对象的属性名（key）取对应的值
    public String get(String key){
        return dataJSON == null ? null : dataJSON.getString(key);
    }
}
