package com.zxn.gesturelock;


import android.content.Context;
import android.text.TextUtils;


/**
 * 手势密码工具类
 * Created by apple on 4/11/15.
 */
public class LockUtil {

    /**
     * @param x float
     * @param y float
     * @return float
     */
    public static float switchDegrees(float x, float y) {
        return (float) MathUtil.pointTotoDegrees(x, y);
    }

    /**
     * 获取角度
     *
     * @param a Point
     * @param b Point
     * @return float
     */
    public static float getDegrees(Point a, Point b) {
        float ax = a.x;// a.index % 3;
        float ay = a.y;// a.index / 3;
        float bx = b.x;// b.index % 3;
        float by = b.y;// b.index / 3;
        float degrees = 0;

        if (bx == ax) {// 若x轴相等 90度或270
            if (by > ay) {// 若b在y轴的下边 90
                degrees = 90;
            } else if (by < ay) {// 若b在y轴的上边 270
                degrees = 270;
            }
        } else if (by == ay) {// 若y轴相等 0度或180
            if (bx > ax) {// 若b在x轴的右边 0
                degrees = 0;
            } else if (bx < ax) {// 若b在x轴的左边 180
                degrees = 180;
            }
        } else {//否则x轴,y轴都不相等.
            if (bx > ax) {// 若在x轴的右边 270~90
                if (by > ay) {// 在y轴的下边 0 - 90
                    degrees = 0;
                    degrees = degrees + switchDegrees(Math.abs(by - ay), Math.abs(bx - ax));
                } else if (by < ay) {// 在y轴的上边 270~0
                    degrees = 360;
                    degrees = degrees - switchDegrees(Math.abs(by - ay), Math.abs(bx - ax));
                }

            } else if (bx < ax) // 在y轴的左边 90~270
            {
                if (by > ay) // 在y轴的下边 180 ~ 270
                {
                    degrees = 90;
                    degrees = degrees + switchDegrees(Math.abs(bx - ax), Math.abs(by - ay));
                } else if (by < ay) // 在y轴的上边 90 ~ 180
                {
                    degrees = 270;
                    degrees = degrees - switchDegrees(Math.abs(bx - ax), Math.abs(by - ay));
                }

            }

        }
        return degrees;
    }

    /**
     * 判断一个点是否在圆内
     *
     * @param sx float
     * @param sy float
     * @param r  float
     * @param x  float
     * @param y  float
     * @return boolean
     */
    public static boolean checkInRound(float sx, float sy, float r, float x, float y) {
        //Math.sqrt正确舍入的一个double值的正平方根
        return Math.sqrt((sx - x) * (sx - x) + (sy - y) * (sy - y)) < r;
    }

    /**
     * 清空本地密码
     *
     * @param context Context
     */
    public static void clearPwd(Context context) {
        SPUtil.saveData(context, "handpswd", "");
    }

    /**
     * 获取本地密码
     *
     * @param context Context
     * @return 密码数组.
     */
    public static int[] getPwd(Context context) {
        String str = (String) SPUtil.getData(context, "handpswd", "");
        if (str != null) {
            String[] s = str.split(",");
            int[] indexs = new int[s.length];
            if (s.length > 1) {
                for (int i = 0; i < s.length; i++) {
                    indexs[i] = Integer.valueOf(s[i]);
                }
            }
            return indexs;
        }
        return new int[]{};
    }


    /**
     * 设置是否开启手势密码
     *
     * @param context Context
     * @param flag    true:开启 false:关闭
     */
    public static void setPwdStatus(Context context, boolean flag) {
        SPUtil.saveData(context, "isopenpwd", flag);
    }


    /**
     * 取当前是否开启手势密码
     *
     * @param context Context
     * @return true:开启 false:关闭
     */
    public static boolean getPwdStatus(Context context) {
        return (boolean) SPUtil.getData(context, "isopenpwd", false);
    }

    /**
     * 将密码设置到本地
     *
     * @param context Context
     * @param mIndexs 密码数字.
     */
    public static void setPwdToDisk(Context context, int[] mIndexs) {
        String str = "";
        for (int i : mIndexs) {
            str += i + ",";
        }
        SPUtil.saveData(context, "handpswd", str);
    }

    /**
     * 判断是否已经创建了密码.
     *
     * @param context Context
     * @return boolean:true:已经创建了密码.false:还未创建密码.
     */
    public static boolean isPwdCreated(Context context) {
        String str = (String) SPUtil.getData(context, "handpswd", "");
        return !TextUtils.isEmpty(str);
    }
}