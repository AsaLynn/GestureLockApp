package com.zxn.gesturelock;

/**
 * 点位置
 * Created by apple on 4/11/15.
 */
public class Point {
    public static int STATE_NORMAL = 0; // 未选中
    public static int STATE_CHECK = 1; // 选中
    public static int STATE_CHECK_ERROR = 2; // 已经选中,但是输错误

    public float x;//横坐标.
    public float y;//纵坐标.
    public int state = 0;//定义选中状态.
    public int index = 0;// 下标

    public Point() {}

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
