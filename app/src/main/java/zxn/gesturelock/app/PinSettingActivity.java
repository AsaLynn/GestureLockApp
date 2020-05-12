package zxn.gesturelock.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.jyn.vcview.VerificationCodeView;
import com.zxn.gesturelock.LockUtil;
import com.zxn.gesturelock.SPUtil;
import com.zxn.presenter.model.CommonEvent;
import com.zxn.presenter.model.CommonEventBus;
import com.zxn.presenter.view.BaseActivity;
import com.zxn.titleview.TitleView;

import butterknife.BindView;

/**
 * Created by zxn on 2020-5-12 13:48:55.
 */
public class PinSettingActivity extends BaseActivity {

    private static final String ARG_PARAM1 = "param1";
    @BindView(R.id.title_view)
    TitleView titleView;
    @BindView(R.id.vcv_code)
    VerificationCodeView vcvCode;
    @BindView(R.id.mascot)
    ImageView mascot;
    private int mParam1;
    private boolean isInputNew;
    private int times;

    public static void jumpTo(Context context) {
        Intent intent = new Intent(context, PinSettingActivity.class);
        context.startActivity(intent);
    }

    /**
     * @param context
     * @param type    0,1,2,3
     */
    public static void jumpTo(Context context, int type) {
        Intent intent = new Intent(context, PinSettingActivity.class);
        intent.putExtra(ARG_PARAM1, type);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_pin_setting;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParam1 = getIntent().getIntExtra(ARG_PARAM1, 0);

        onInitTitle();

        onInitContent();

    }

    private void onInitContent() {


        if (mParam1 == 0) {
            //titleView.setTitleText("创建PIN密码");
            vcvCode.setOnCodeFinishListener(new VerificationCodeView.OnCodeFinishListener() {
                @Override
                public void onTextChange(View view, String content) {

                }

                @Override
                public void onComplete(View view, String content) {
                    showToast("创建完成");
                    finish();
                    SPUtil.saveData(mContext, "pin", content);
                    CommonEventBus.post(new CommonEvent<>(IEventConstants.EVENT_CREATE_PIN));
                }
            });
        } else if (mParam1 == 1) {
            //titleView.setTitleText("修改PIN密码");

            vcvCode.setOnCodeFinishListener(new VerificationCodeView.OnCodeFinishListener() {
                @Override
                public void onTextChange(View view, String content) {

                }

                @Override
                public void onComplete(View view, String content) {
                    if (times == 0) {
                        String pin = SPUtil.getData(mContext, "pin", "").toString();
                        if (content.equals(pin)) {
                            showToast("请输入新的PIN密码");
                            vcvCode.setEmpty();
                            times++;
                        }else {
                            showToast("PIN密码验证错误");
                        }
                    } else if (times == 1) {
                        showToast("请再次输入新的手势密码");
                        vcvCode.setEmpty();
                        times++;
                    } else if (times == 2) {
                        SPUtil.saveData(mContext, "pin", content);
                        CommonEventBus.post(new CommonEvent<>(IEventConstants.EVENT_CREATE_PIN));
                        finish();
                    }
                }
            });
        } else if (mParam1 == 2) {
            //titleView.setTitleText("清除PIN密码");
            vcvCode.setOnCodeFinishListener(new VerificationCodeView.OnCodeFinishListener() {
                @Override
                public void onTextChange(View view, String content) {

                }

                @Override
                public void onComplete(View view, String content) {
                    String pin = SPUtil.getData(mContext, "pin", "").toString();
                    if (content.equals(pin)) {
                        showToast("清除pin密码成功");
                        SPUtil.saveData(mContext, "pin", "");
                    }
                    CommonEventBus.post(new CommonEvent<>(IEventConstants.EVENT_CLEAR_PIN));
                    finish();
                }
            });
        } else if (mParam1 == 3) {
            titleView.setTitleText("验证PIN密码");
        }
    }

    private void onInitTitle() {
        if (mParam1 == 0) {
            titleView.setTitleText("创建PIN密码");
        } else if (mParam1 == 1) {
            titleView.setTitleText("修改PIN密码");
        } else if (mParam1 == 2) {
            titleView.setTitleText("清除PIN密码");
        } else if (mParam1 == 3) {
            titleView.setTitleText("验证PIN密码");
        }
    }

}
