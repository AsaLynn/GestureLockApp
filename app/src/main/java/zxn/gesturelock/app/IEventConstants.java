package zxn.gesturelock.app;

/**
 * Created by zxn on 2020/5/12.
 */
public interface IEventConstants {

    /**
     * 创建手势密码成功
     */
    String EVENT_CREATE_GESTURE_LOCK = "event_create_gesture_lock";

    /**
     * 清除密码.
     */
    String EVENT_CLEAR_GESTURE_LOCK = "event_clear_gesture_lock";

    /**
     * 创建pin密码成功
     */
    String EVENT_CREATE_PIN = "event_create_pin";

    /**
     * 清除pin密码成功
     */
    String EVENT_CLEAR_PIN = "event_clear_pin";

//    String EVENT_UPDATE_PIN = "event_update_pin";
}
