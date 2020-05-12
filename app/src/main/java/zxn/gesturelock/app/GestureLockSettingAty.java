package zxn.gesturelock.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.zxn.gesturelock.CustomLockView;
import com.zxn.gesturelock.LockUtil;
import com.zxn.presenter.model.CommonEvent;
import com.zxn.presenter.model.CommonEventBus;
import com.zxn.presenter.view.BaseActivity;
import com.zxn.titleview.TitleView;
import com.zxn.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 手势密码引导设置!
 * CustomLockView的使用:
 * 1,xml中布局,代码中实例化.
 * 2,setOnCompleteListener
 */
public class GestureLockSettingAty extends BaseActivity {

    @BindView(R.id.title_common)
    TitleView titleCommon;
    @BindView(R.id.tvWarn)
    TextView tvWarn;
    @BindView(R.id.cl)
    CustomLockView cl;
    @BindView(R.id.mascot)
    ImageView mascot;
    int[] ivIds = {R.id.iva, R.id.ivb, R.id.ivc, R.id.ivd, R.id.ive, R.id.ivf, R.id.ivg, R.id.ivh, R.id.ivi};
    private List<ImageView> list = new ArrayList<>();
    private int times = 0;

    private int[] mIndexs = null;
    private int mType;
    /**
     * 本地密码.
     */
    private int[] mLocalIndexs;

    private boolean isInputNew;

    /**
     * 手势密码入口.
     *
     * @param context Context
     * @param type    0:创建,1:修改,2:清除,3:手势密码验证.
     */
    public static void jumpTo(Context context, int type) {
        Intent intent = new Intent(context, GestureLockSettingAty.class);
        //intent.putExtra("title", title);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    private void ignoreGesturePrompt(boolean flag) {
        LockUtil.setPwdStatus(mContext, flag);
        goHome();
    }

    private void goHome() {//判断是否为首次登陆
        finish();
    }

    protected void initContentView() {
        //初始化9个小圆
        for (int i = 0; i < ivIds.length; i++) {
            ImageView iv = (ImageView) findViewById(ivIds[i]);
            list.add(iv);
        }

        //设置是否显示箭头.
        cl.setShow(false);
        mLocalIndexs = LockUtil.getPwd(mContext);
        if (mType == 0) {
            cl.setOnCompleteListener(new CustomLockView.OnCompleteListener() {
                @Override
                public void onComplete(int[] indexs) {
                    mIndexs = indexs;
                    //创建手势密码.
                    //titleCommon.setTitleText("创建手势密码");
                    //将密码设置在本地.
                    LockUtil.setPwdToDisk(GestureLockSettingAty.this, mIndexs);
                    //开启密码.
                    LockUtil.setPwdStatus(GestureLockSettingAty.this, true);
                    //设置密码状态保存在本地.
                    finish();
                    showToast("设置成功");
                    CommonEventBus.post(new CommonEvent<>(IEventConstants.EVENT_CREATE_GESTURE_LOCK));
                /*if (times == 0) {
                    for (int i = 0; i < indexs.length; i++) {
                        list.get(indexs[i]).setImageDrawable(getResources().getDrawable(R.mipmap.gesturecirlebrownsmall));
                    }
                    GestureLockSettingAty.this.tvWarn.setText("再次绘制解锁图案");
                    GestureLockSettingAty.this.tvWarn.setTextColor(getResources().getColor(R.color.c_474747));
                    times++;
                    //showRightBtn("重新设置", onMenuItemClick);
                } else if (times == 1) {
                    //将密码设置在本地.
                    LockUtil.setPwdToDisk(GestureLockSettingAty.this, mIndexs);
                    //开启密码.
                    LockUtil.setPwdStatus(GestureLockSettingAty.this,true);
                    //设置密码状态保存在本地.
                    finish();
                }*/
                }

                @Override
                public void onError() {
                    GestureLockSettingAty.this.tvWarn.setText("与上一次绘制不一致，请重新绘制");
                    GestureLockSettingAty.this.tvWarn.setTextColor(getResources().getColor(R.color.c_ff4444));
                }
            });
        } else if (mType == 1) {//修改手势密码
            if (mLocalIndexs.length > 1) {
                cl.setIndexs(mLocalIndexs);
                cl.setStatus(1);
                cl.setErrorTimes(5);
                cl.setOnCompleteListener(new CustomLockView.OnCompleteListener() {
                    @Override
                    public void onComplete(int[] indexs) {
                        //LockUtil.clearPwd(mContext);
                        if (times == 0) {
                            showToast("请输入新的手势密码");
                            cl.clearCurrent();
                            cl.setStatus(0);
                            times++;
                        } else if (times == 1) {
                            //cl.setIndexs(indexs);
                            showToast("请再次输入新的手势密码");
                            times++;
                        } else if (times == 2) {
                            mIndexs = indexs;
                            //创建手势密码.
                            //titleCommon.setTitleText("创建手势密码");
                            //将密码设置在本地.
                            LockUtil.setPwdToDisk(GestureLockSettingAty.this, mIndexs);
                            //开启密码.
                            LockUtil.setPwdStatus(GestureLockSettingAty.this, true);
                            //设置密码状态保存在本地.
                            finish();
                            showToast("修改成功");
                            CommonEventBus.post(new CommonEvent<>(IEventConstants.EVENT_CREATE_GESTURE_LOCK));
                        }
                    }

                    @Override
                    public void onError() {
//                        if (cl.getErrorTimes() > 0) {
////                            tvWarn.setText("密码错误，还可以再输入" + cl.getErrorTimes() + "次");
////                            tvWarn.setTextColor(getResources().getColor(R.color.c_ff4444));
////                            //tvWarn.setAnimation(shakeAnimation(5));
////                        } else {
////                            //UIUtils.showMsg("请重新设置");
////                            LockUtil.clearPwd(mContext);
////                            LockUtil.setPwdStatus(mContext, false);
////                            finish();
////                        }
                    }
                });
            }
        } else if (mType == 2) {//清除手势密码
            if (mLocalIndexs.length > 1) {
                cl.setIndexs(mLocalIndexs);
                cl.setStatus(1);
                cl.setErrorTimes(5);
                cl.setOnCompleteListener(new CustomLockView.OnCompleteListener() {
                    @Override
                    public void onComplete(int[] indexs) {
                        LockUtil.clearPwd(mContext);
                        showToast("清除手势密码成功");
                        finish();
                        CommonEventBus.post(new CommonEvent<>(IEventConstants.EVENT_CLEAR_GESTURE_LOCK));
                    }

                    @Override
                    public void onError() {
//                        if (cl.getErrorTimes() > 0) {
////                            tvWarn.setText("密码错误，还可以再输入" + cl.getErrorTimes() + "次");
////                            tvWarn.setTextColor(getResources().getColor(R.color.c_ff4444));
////                            //tvWarn.setAnimation(shakeAnimation(5));
////                        } else {
////                            //UIUtils.showMsg("请重新设置");
////                            LockUtil.clearPwd(mContext);
////                            LockUtil.setPwdStatus(mContext, false);
////                            finish();
////                        }
                    }
                });
            }
        } else if (mType == 3) {//手势密码验证
            if (mLocalIndexs.length > 1) {
                cl.setIndexs(mLocalIndexs);
                cl.setStatus(1);
                cl.setErrorTimes(5);
                cl.setOnCompleteListener(new CustomLockView.OnCompleteListener() {
                    @Override
                    public void onComplete(int[] indexs) {
                        LockUtil.clearPwd(mContext);
                        showToast("验证手势密码成功");
                        finish();
                        //CommonEventBus.post(new CommonEvent<>(IEventConstants.EVENT_CLEAR_GESTURE_LOCK));
                    }

                    @Override
                    public void onError() {
//                        if (cl.getErrorTimes() > 0) {
////                            tvWarn.setText("密码错误，还可以再输入" + cl.getErrorTimes() + "次");
////                            tvWarn.setTextColor(getResources().getColor(R.color.c_ff4444));
////                            //tvWarn.setAnimation(shakeAnimation(5));
////                        } else {
////                            //UIUtils.showMsg("请重新设置");
////                            LockUtil.clearPwd(mContext);
////                            LockUtil.setPwdStatus(mContext, false);
////                            finish();
////                        }
                    }
                });
            }
        }
    }

    private void resetGestureLock() {
        times = 0;
        for (int i = 0; i < mIndexs.length; i++) {//unselected
            list.get(mIndexs[i]).setImageDrawable(getResources().getDrawable(R.mipmap.unselected));
        }
        cl.clearCurrent();
        tvWarn.setText("绘制解锁图案");
        tvWarn.setTextColor(UIUtils.getColor(R.color.c_474747));
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.content_gesture_lock;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mTitle = getIntent().getStringExtra("title");
        mType = getIntent().getIntExtra("type", 0);

        onInitTitle();

        initContentView();
    }

    private void onInitTitle() {
        //0:创建,1:修改,2:清除,3:手势密码验证.
        if (mType == 0) {
            titleCommon.setTitleText("创建手势密码");
        } else if (mType == 1) {
            titleCommon.setTitleText("修改手势密码");
        } else if (mType == 2) {
            titleCommon.setTitleText("清除手势密码");
        } else if (mType == 3) {
            titleCommon.setTitleText("手势密码验证");
        }
    }

   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //这里不需要执行父类的点击事件，所以直接return
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/
}


//    private int toGesturelock;
    /*private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                *//*case R.id.action_settings:
                    resetGestureLock();
                    break;*//*
            }
            return true;
        }
    };*/
   /* private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //关闭手势密码设置提示,进入首页.
            ignoreGesturePrompt(false);
            SPUtil.saveData(mContext, "set_gesturel_locked", false);
        }
    };
    private String mTitle;*/
