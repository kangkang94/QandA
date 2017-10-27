package cn.edu.tj.wenda.async;

import java.util.List;

/**
 * Created by kang on 2017/10/26.
 */
public interface EventHandler {
    void doHandle(EventModel model);
    List<EventType> getSupportEventTypes();
}
