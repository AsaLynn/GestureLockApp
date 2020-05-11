package com.zxn.gesturelock;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 绘制手势的控件.
 * 自定义view步骤:
 * 1,重写三个构造函数,其中两个参数的构造方法必须有
 * 2,重写onDraw()方法,是用来绘制View图像的方法,必须有
 * 3,重写onMeasure()方法,如果要改变View 的大小需要重写
 *4,重写onLayout()方法,如果要改变View在父控件中的位置，需要重写
 */
public class CustomLockView extends View {
    //圆初始状态时的图片
    private Bitmap locus_round_original;
    //圆点击时的图片
    private Bitmap locus_round_click;
    //错误
    private Bitmap locus_round_error;
    //方向箭头
    private Bitmap locus_arrow;
    //方向错误的箭头
    private Bitmap locus_arrow_error;


    //控件宽度
    private float w = 0;
    //控件高度
    private float h = 0;
    //是否已缓存
    private boolean isCache = false;
    //画笔
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //九宫格小圆点图标,二维数组存放9个对象.
    private Point[][] mPoints = new Point[3][3];
    //小圆点的半径
    private float r = 0;
    //选中圆的集合
    private List<Point> sPoints = new ArrayList<>();
    //判断是否正在绘制并且未到达下一个点
    private boolean movingNoPoint = false;
    //正在移动的x,y坐标
    float moveingX, moveingY;
    //是否可操作
    private boolean isTouch = true;
    //密码最小长度
    private int passwordMinLength = 4;
    //判断是否触摸屏幕
    private boolean checking = false;
    //刷新
    private TimerTask task = null;
    //计时器
    private Timer timer = new Timer();
    //监听
    private OnCompleteListener mCompleteListener;
    //清除痕迹的时间
    private long CLEAR_TIME = 0;
    //错误限制 默认为4次
    private int errorTimes = 5;
    //记录上一次滑动的密码
    private int[] mIndexs = null;
    //记录当前第几次触发 默认为0次
    private int showTimes = 0;
    //当前密码是否正确 默认为正确
    private boolean isCorrect = true;
    //是否显示滑动方向 默认为显示
    private boolean isShow = true;
    //验证或者设置 0:设置 1:验证
    private int status = 0;
    //用于执行清除界面
    private Handler handler = new Handler();
    //用于定时执行清除界面
    private Runnable run = new Runnable() {
        @Override
        public void run() {
            handler.removeCallbacks(run);
            reset();
            postInvalidate();
        }
    };
    private String errorStr = "";

    public CustomLockView(Context context) {
        super(context);
    }

    public CustomLockView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isCache) {
            initCache();
        }
        //绘制圆以及显示当前状态
        drawToCanvas(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 不可操作
//        if (!isTouch) {
//            return false;
//        }
        isCorrect = true;
        handler.removeCallbacks(run);
        movingNoPoint = false;
        float ex = event.getX();
        float ey = event.getY();
        boolean isFinish = false;
        Point p = null;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 点下
                // 如果正在清除密码,则取消
                if (task != null) {
                    task.cancel();
                    task = null;
                }
                // 删除之前的点
                reset();
                p = checkSelectPoint(ex, ey);
                if (p != null) {
                    checking = true;
                }
                break;
            case MotionEvent.ACTION_MOVE: // 移动
                if (checking) {
                    p = checkSelectPoint(ex, ey);
                    if (p == null) {
                        movingNoPoint = true;
                        moveingX = ex;
                        moveingY = ey;
                    }
                }
                break;
            case MotionEvent.ACTION_UP: // 提起
                p = checkSelectPoint(ex, ey);
                checking = false;
                isFinish = true;
                break;
            default:
                movingNoPoint = true;
                break;
        }
        if (!isFinish && checking && p != null) {
            int rk = crossPoint(p);
            if (rk == 2) {
                //与非最后一重叠
                movingNoPoint = true;
                moveingX = ex;
                moveingY = ey;
            } else if (rk == 0) {
                //一个新点
                p.state = Point.STATE_CHECK;
                addPoint(p);
            }
        }
        if (isFinish) {
            handler.postDelayed(run, 1500);
            if (this.sPoints.size() == 1) {
                this.reset();
            } else if (this.sPoints.size() < passwordMinLength
                    && this.sPoints.size() > 0) {
                clearPassword();
                Toast.makeText(this.getContext(), "密码太短,请重新输入!", Toast.LENGTH_SHORT).show();
            } else if (mCompleteListener != null) {
                if (this.sPoints.size() >= passwordMinLength) {
                    int[] indexs = new int[sPoints.size()];
                    for (int i = 0; i < sPoints.size(); i++) {
                        indexs[i] = sPoints.get(i).index;
                    }
                    if (status == 0) {
                        invalidatePass(indexs);
                    } else if (status == 1) {
                        invalidateOldPsw(indexs);
                    }
                }
            }
        }
        postInvalidate();
        return true;
    }

    /**
     * 初始化Cache信息
     */
    private void initCache() {
        w = this.getWidth();
        Log.i(this.getClass().getName(), "w-->" + w);
        h = this.getHeight();
        //定义第一个小圆点x坐标
        float x = 0;
        //定义第一个小圆点y坐标.
        float y = 0;
        // 以最小的为准
        // 纵屏
        x = (w - h) / 2;
        w = h;
        locus_round_original = BitmapFactory.decodeResource(getResources(), R.mipmap.unselected);
        locus_round_click = BitmapFactory.decodeResource(getResources(), R.mipmap.selected);
        locus_arrow = BitmapFactory.decodeResource(getResources(), R.mipmap.gesturetrianglebrown);
        locus_round_error = BitmapFactory.decodeResource(getResources(), R.mipmap.selected_error);
        locus_arrow_error = BitmapFactory.decodeResource(getResources(), R.mipmap.gesturetrianglebrownerror);
        // 定义控件宽度的最小值.
        float canvasMinW = w;
        if (w > h) {
            canvasMinW = h;
        }
        //定义小圆圈的最小直径.
        float roundMinW = canvasMinW / 8.0f * 2;
        //定义小圆圈半径
        float roundW = roundMinW / 2.0f;
        //计算偏差.
        float deviation = canvasMinW % (8 * 2) / 2;
        Log.i(this.getClass().getName(), "deviation-->" + deviation);
        x += deviation;
        if (locus_round_original != null) {

            if (locus_round_original.getWidth() > roundMinW) {
                roundW = locus_round_original.getWidth() / 2;
            }
            //第1行第1列圆点坐标.
            mPoints[0][0] = new Point(roundW, y + 0 + roundW);
            //第1行第2列圆点坐标(整体想左边偏移5个单位)
            mPoints[0][1] = new Point(x + w / 2 - 5, y + 0 + roundW);
            //第1行第3列圆点坐标
            mPoints[0][2] = new Point(x + w - roundW - 5, y + 0 + roundW);
            //第2行的3个圆点坐标.
            mPoints[1][0] = new Point(roundW, y + h / 2);
            mPoints[1][1] = new Point(x + w / 2 - 5, y + h / 2);
            mPoints[1][2] = new Point(x + w - roundW - 5, y + h / 2);
            //第3行的3个圆点坐标.
            mPoints[2][0] = new Point(roundW, y + h - roundW);
            mPoints[2][1] = new Point(x + w / 2 - 5, y + h - roundW);
            mPoints[2][2] = new Point(x + w - roundW - 5, y + h - roundW);
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    Log.i(this.getClass().getName(), "-->x=" + mPoints[i][j].x + "y=" + mPoints[i][j].y);
                }
            }
            int k = 0;
            for (Point[] ps : mPoints) {
                for (Point p : ps) {
                    p.index = k;
                    k++;
                }
            }
            //获得小圆点的半径
            r = locus_round_original.getHeight() / 2;
            isCache = true;
        }
    }

    /**
     * 图像绘制
     *
     * @param canvas
     */
    private void drawToCanvas(Canvas canvas) {
        //在Android中，目前，有两种出现锯齿的情况。
        //1当我们用Canvas绘制位图的时候，如果对位图进行了选择，则位图会出现锯齿。
        //2在用View的RotateAnimation做动画时候，如果View当中包含有大量的图形，也会出现锯齿。我们分别以这两种情况加以考虑。
        //用Canvas绘制位图的情况。在用Canvas绘制位图时，一般地，我们使用drawBitmap函数家族，
        // 在这些函数中，都有一个Paint参数，要做到防止锯齿，我们就要使用到这个参数。
        // 如下：首先在你的构造函数中，需要创建一个Paint。 Paint mPaint = new Paint（）；
        // 然后，您需要设置两个参数: 1)mPaint.setAntiAlias(); 2)mPaint.setBitmapFilter(true)。
        // 第一个函数是用来防止边缘的锯齿，第二个函数是用来对位图进行滤波处理。
        // 最后，在画图的时候，调用drawBitmap函数，只需要将整个Paint传入即可。
        //有时候，当你做RotateAnimation时，你会发现，讨厌的锯齿又出现了。这个时候，由于你不能控制位图的绘制，
        // 只能用其他方法来实现防止锯齿。另外，如果你画的位图很多。不想每个位图的绘制都传入一个Paint。
        // 还有的时候，你不可能控制每个窗口的绘制的时候，您就需要用下面的方法来处理——对整个Canvas进行处理。 1）
        // 在您的构造函数中，创建一个Paint滤波器。
        // PaintFlagsDrawFilter mSetfil = new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG);
        // 第一个参数是你要清除的标志位，第二个参数是你要设置的标志位。此处设置为对位图进行滤波。
        // 2）当你在画图的时候，如果是View则在onDraw当中，如果是ViewGroup则在dispatchDraw中调用如下函数。
        // canvas.setDrawFilter( mSetfil );
        //最后，另外，在Drawable类及其子类中，也有函数setFilterBitmap可以用来对Bitmap进行滤波处理，
        // 这样，当你选择Drawable时，会有抗锯齿的效果。
        //画图时，设置画布抗锯齿
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        // 画连线
        if (sPoints.size() > 0) {
            Point tp = sPoints.get(0);
            for (int i = 1; i < sPoints.size(); i++) {
                //根据移动的方向绘制线
                Point p = sPoints.get(i);
                if (isCorrect) {
                    //手势正确画蓝线.
                    drawLine(canvas, tp, p, R.color.c_3b8ed4);
                } else {
                    //手势错误画红线.
                    drawLine(canvas, tp, p, R.color.c_ff4444);
                    //drawErrorLine(canvas, tp, p);
                }
                tp = p;
            }
            if (this.movingNoPoint) {
                //到达下一个点停止移动绘制固定的方向
                drawLine(canvas, tp, new Point((int) moveingX + 20, (int) moveingY));
            }
        }
        // 画所有点
        for (int i = 0; i < mPoints.length; i++) {
            for (int j = 0; j < mPoints[i].length; j++) {
                Point p = mPoints[i][j];
                if (p != null) {
                    if (p.state == Point.STATE_CHECK) {
                        //画选中的圆圈,因安卓坐标都是在左上角,所以向左边,向右边偏移r.
                        //1.Bitmap图片对象，2.float:偏移左边的位置，3.float： 偏移顶部的位置4.Paint笔.
                        canvas.drawBitmap(locus_round_click, p.x - r, p.y - r, mPaint);
                    } else if (p.state == Point.STATE_CHECK_ERROR) {
                        //画选错的圆圈.
                        canvas.drawBitmap(locus_round_error, p.x - r, p.y - r, mPaint);
                    } else {
                        //画正常的圆圈.
                        canvas.drawBitmap(locus_round_original, p.x - r, p.y - r, mPaint);
                    }
                }
            }
        }
        if (isShow) {
            // 绘制方向图标
            if (sPoints.size() > 0) {
                Point tp = sPoints.get(0);
                for (int i = 1; i < sPoints.size(); i++) {
                    //根据移动的方向绘制方向图标
                    Point p = sPoints.get(i);
                    if (isCorrect) {
                        drawDirection(canvas, tp, p);
                    } else {
                        drawErrorDirection(canvas, tp, p);
                    }
                    tp = p;
                }
            }
        }
    }


    /**
     * 向选中点集合中添加一个点
     *
     * @param point
     */
    private void addPoint(Point point) {
        this.sPoints.add(point);
    }

    /**
     * 检查点是否被选择
     *
     * @param x
     * @param y
     * @return
     */
    private Point checkSelectPoint(float x, float y) {
        for (int i = 0; i < mPoints.length; i++) {
            for (int j = 0; j < mPoints[i].length; j++) {
                Point p = mPoints[i][j];
                if (LockUtil.checkInRound(p.x, p.y, r, (int) x, (int) y)) {
                    return p;
                }
            }
        }
        return null;
    }

    /**
     * 判断点是否有交叉 返回 0,新点 ,1 与上一点重叠 2,与非最后一点重叠
     *
     * @param p
     * @return
     */
    private int crossPoint(Point p) {
        // 重叠的不最后一个则 reset
        if (sPoints.contains(p)) {
            if (sPoints.size() > 2) {
                // 与非最后一点重叠
                if (sPoints.get(sPoints.size() - 1).index != p.index) {
                    return 2;
                }
            }
            return 1; // 与最后一点重叠
        } else {
            return 0; // 新点
        }
    }

    /**
     * 重置点状态
     */
    public void reset() {
        for (Point p : sPoints) {
            p.state = Point.STATE_NORMAL;
        }
        sPoints.clear();
    }

    /**
     * 清空当前信息
     */
    public void clearCurrent() {
        showTimes = 0;
        errorTimes = 4;
        isCorrect = true;
        reset();
        postInvalidate();
    }

    /**
     * 画两点的连接
     *
     * @param canvas
     * @param a
     * @param b
     */
    private void drawLine(Canvas canvas, Point a, Point b, int colorId) {
        mPaint.setColor(getResources().getColor(colorId));
        mPaint.setStrokeWidth(3);
        canvas.drawLine(a.x, a.y, b.x, b.y, mPaint);
    }

    private void drawLine(Canvas canvas, Point a, Point b) {
        mPaint.setColor(getResources().getColor(R.color.c_3b8ed4));
        mPaint.setStrokeWidth(3);
        canvas.drawLine(a.x, a.y, b.x, b.y, mPaint);
    }

//    /**
//     * 错误线
//     *
//     * @param canvas
//     * @param a
//     * @param b
//     */
//    private void drawErrorLine(Canvas canvas, Point a, Point b) {
//        mPaint.setColor(getResources().getColor(R.color.red));
//        mPaint.setStrokeWidth(3);
//        canvas.drawLine(a.x, a.y, b.x, b.y, mPaint);
//    }

    /**
     * 绘制方向图标
     *
     * @param canvas
     * @param a
     * @param b
     */
    private void drawDirection(Canvas canvas, Point a, Point b) {
        //获取角度
        float degrees = LockUtil.getDegrees(a, b);
        //根据两点方向旋转
        canvas.rotate(degrees, a.x, a.y);
        float x = a.x + r / 2;
        float y = a.y - locus_arrow.getHeight() / 2.0f;
        if (degrees == 270) {
            y = a.y - locus_arrow.getHeight() / 2.0f;
        }
        //绘制箭头
        canvas.drawBitmap(locus_arrow, x, y, mPaint);
        //旋转方向
        canvas.rotate(-degrees, a.x, a.y);
    }

    /**
     * 错误方向
     *
     * @param canvas
     * @param a
     * @param b
     */
    private void drawErrorDirection(Canvas canvas, Point a, Point b) {
        //获取角度
        float degrees = LockUtil.getDegrees(a, b);
        //根据两点方向旋转
        canvas.rotate(degrees, a.x, a.y);
        float x = a.x + r / 2;
        float y = a.y - locus_arrow.getHeight() / 2.0f;
        if (degrees == 270) {
            y = a.y - locus_arrow.getHeight() / 2.0f;
        }
        //绘制箭头
        canvas.drawBitmap(locus_arrow_error, x, y, mPaint);
        //旋转方向
        canvas.rotate(-degrees, a.x, a.y);
    }

    /**
     * 清除密码
     */
    private void clearPassword() {
        clearPassword(CLEAR_TIME);
    }

    /**
     * 清除密码
     */
    private void clearPassword(final long time) {
        if (time > 1) {
            if (task != null) {
                task.cancel();
            }
            postInvalidate();
            task = new TimerTask() {
                public void run() {
                    reset();
                    postInvalidate();
                }
            };
            timer.schedule(task, time);
        } else {
            reset();
            postInvalidate();
        }
    }

    /**
     * 设置已经选中的为错误
     */
    private void error() {
        for (Point p : sPoints) {
            p.state = Point.STATE_CHECK_ERROR;
        }
    }

    /**
     * 验证设置密码，滑动两次密码是否相同
     *
     * @param indexs
     */
    private void invalidatePass(int[] indexs) {
        if (showTimes == 0) {
            mIndexs = indexs;
            mCompleteListener.onComplete(indexs);
            showTimes++;
            reset();
        } else if (showTimes == 1) {
            if (mIndexs.length == indexs.length) {
                for (int i = 0; i < mIndexs.length; i++) {
                    if (mIndexs[i] != indexs[i]) {
                        isCorrect = false;
                        break;
                    }
                }
            } else {
                isCorrect = false;
            }
            if (!isCorrect) {
                error();
                mCompleteListener.onError();
                postInvalidate();
            } else {
                mCompleteListener.onComplete(indexs);
            }
        }
    }

    /**
     * 验证本地密码与当前滑动密码是否相同
     *
     * @param indexs
     */
    private void invalidateOldPsw(int[] indexs) {
        if (mIndexs.length == indexs.length) {
            for (int i = 0; i < mIndexs.length; i++) {
                if (mIndexs[i] != indexs[i]) {
                    isCorrect = false;
                    break;
                }
            }
        } else {
            isCorrect = false;
        }
        if (!isCorrect) {
            errorTimes--;
            error();
            mCompleteListener.onError();
            postInvalidate();
        } else {
            mCompleteListener.onComplete(indexs);
        }
    }

    /**
     * 设置监听
     *
     * @param mCompleteListener
     */
    public void setOnCompleteListener(OnCompleteListener mCompleteListener) {
        this.mCompleteListener = mCompleteListener;
    }

    /**
     * 轨迹球画完监听事件
     */
    public interface OnCompleteListener {
        /**
         * 画完了
         */
        public void onComplete(int[] indexs);

        /**
         * 绘制错误
         */
        public void onError();
    }

    public int getErrorTimes() {
        return errorTimes;
    }

    public void setErrorTimes(int errorTimes) {
        this.errorTimes = errorTimes;
    }

    public int[] getmIndexs() {
        return mIndexs;
    }

    public void setmIndexs(int[] mIndexs) {
        this.mIndexs = mIndexs;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean isShow) {
        this.isShow = isShow;
    }
}
