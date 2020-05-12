package zxn.gesturelock.app;

import org.greenrobot.eventbus.EventBus;

/**
 * 通用的CommonEventBus
 * Created by zxn on 2019/4/9.
 */
public class CommonEventBus {

    /**
     * 注册订阅
     *
     * @param subscriber 订阅者.
     */
    public static void register(Object subscriber) {
        EventBus.getDefault().register(subscriber);
    }

    /**
     * 反注册订阅
     *
     * @param subscriber 订阅者.
     */
    public static void unregister(Object subscriber) {
        EventBus.getDefault().unregister(subscriber);
    }

    /**
     * EventBus发送消息.
     *
     * @param event 通用的CommonEvent
     */
    public static void post(CommonEvent event) {
        EventBus.getDefault().post(event);
    }

    /**
     * Posts the given event to the event bus and holds on to the event (because it is sticky).
     * The most recent sticky event of an event's type is kept in memory for future access by subscribers using Subscribe#sticky.
     *
     * @param event 通用的CommonEvent
     */
    public static void postSticky(CommonEvent event) {
        EventBus.getDefault().postSticky(event);
    }

}
