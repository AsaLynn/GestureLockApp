package zxn.gesturelock.app;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.zxn.gesturelock.LockUtil;
import com.zxn.iconitemview.IconItemView;
import com.zxn.presenter.model.CommonEvent;
import com.zxn.presenter.view.BaseActivity;
import com.zxn.titleview.TitleView;
import com.zxn.widget.SwitchButton;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.OnClick;

import static zxn.gesturelock.app.IEventConstants.EVENT_CREATE_GESTURE_LOCK;

/**
 * Created by zxn on 2020/5/12.
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.title_common)
    TitleView titleCommon;
    @BindView(R.id.iiv_my_wallet)
    IconItemView iivMyWallet;
    @BindView(R.id.sb_pin)
    SwitchButton sbPin;
    @BindView(R.id.rl_pin_pw)
    RelativeLayout rlPinPw;
    @BindView(R.id.sb_lock)
    SwitchButton sbLock;
    @BindView(R.id.rl_lock)
    RelativeLayout rlLock;
    @BindView(R.id.iiv_update_lock)
    IconItemView iivUpdateLock;
    @BindView(R.id.iiv_clear_lock)
    IconItemView iivClearLock;
    @BindView(R.id.iiv_create_lock)
    IconItemView iivCreateLock;
    @BindView(R.id.iiv_pin)
    IconItemView iivPin;
    @BindView(R.id.iiv_pin_update)
    IconItemView iivPinUpdate;
    @BindView(R.id.iiv_pin_clear)
    IconItemView iivPinClear;
    @BindView(R.id.iiv_open_lock)
    IconItemView iivOpenLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sbLock.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    if (LockUtil.isPwdCreated(mContext)) {
                        iivCreateLock.setVisibility(View.GONE);
                        iivUpdateLock.setVisibility(View.VISIBLE);
                        iivClearLock.setVisibility(View.VISIBLE);
                    } else {
                        iivCreateLock.setVisibility(View.VISIBLE);
                        iivUpdateLock.setVisibility(View.GONE);
                        iivClearLock.setVisibility(View.GONE);
                    }
                } else {
                    iivCreateLock.setVisibility(View.GONE);
                    iivUpdateLock.setVisibility(View.GONE);
                    iivClearLock.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTouchCheckedChanged(SwitchButton view, boolean isChecked) {

            }
        });

        if (sbLock.isChecked()) {
            if (LockUtil.isPwdCreated(mContext)) {
                iivCreateLock.setVisibility(View.GONE);
                iivUpdateLock.setVisibility(View.VISIBLE);
                iivClearLock.setVisibility(View.VISIBLE);
            } else {
                iivCreateLock.setVisibility(View.VISIBLE);
                iivUpdateLock.setVisibility(View.GONE);
                iivClearLock.setVisibility(View.GONE);
            }
        } else {
            iivCreateLock.setVisibility(View.GONE);
            iivUpdateLock.setVisibility(View.GONE);
            iivClearLock.setVisibility(View.GONE);
        }


        sbPin.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (sbPin.isChecked()) {
                    iivPin.setVisibility(View.VISIBLE);
                } else {
                    iivPin.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTouchCheckedChanged(SwitchButton view, boolean isChecked) {

            }
        });
        if (sbPin.isChecked()) {
            iivPin.setVisibility(View.VISIBLE);
        } else {
            iivPin.setVisibility(View.GONE);
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @OnClick({R.id.iiv_update_lock,
            R.id.iiv_clear_lock,
            R.id.iiv_create_lock,
            R.id.iiv_pin,
            R.id.iiv_open_lock,
            R.id.iiv_pin_update,
            R.id.iiv_pin_clear,
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iiv_create_lock:
                GestureLockSettingAty.jumpTo(mContext, 0);
                break;
            case R.id.iiv_update_lock:
                GestureLockSettingAty.jumpTo(mContext, 1);
                break;
            case R.id.iiv_clear_lock:
                GestureLockSettingAty.jumpTo(mContext, 2);
                break;
            case R.id.iiv_open_lock:
                GestureLockSettingAty.jumpTo(mContext, 3);
                break;
            case R.id.iiv_pin:
                PinSettingActivity.jumpTo(mContext);
                break;
            case R.id.iiv_pin_update:
                PinSettingActivity.jumpTo(mContext,1);
                break;
            case R.id.iiv_pin_clear:
                PinSettingActivity.jumpTo(mContext,2);
                break;
        }
    }

    @Subscribe
    public void onEvent(CommonEvent event) {
        if (event.equals(EVENT_CREATE_GESTURE_LOCK)) {
            //iivUpdateLock.setLeftText("修改手势密码");
            iivUpdateLock.setVisibility(sbLock.isChecked() ? View.VISIBLE : View.GONE);
            iivClearLock.setVisibility(sbLock.isChecked() ? View.VISIBLE : View.GONE);
            iivCreateLock.setVisibility(View.GONE);
        } else if (event.equals(IEventConstants.EVENT_CLEAR_GESTURE_LOCK)) {
            if (sbLock.isChecked()) {
                if (LockUtil.isPwdCreated(mContext)) {
                    iivCreateLock.setVisibility(View.GONE);
                    iivUpdateLock.setVisibility(View.VISIBLE);
                    iivClearLock.setVisibility(View.VISIBLE);
                } else {
                    iivCreateLock.setVisibility(View.VISIBLE);
                    iivUpdateLock.setVisibility(View.GONE);
                    iivClearLock.setVisibility(View.GONE);
                }
            } else {
                iivCreateLock.setVisibility(View.GONE);
                iivUpdateLock.setVisibility(View.GONE);
                iivClearLock.setVisibility(View.GONE);
            }
        } else if (event.equals(IEventConstants.EVENT_CREATE_PIN)) {
            iivPinUpdate.setVisibility(sbPin.isChecked() ? View.VISIBLE : View.GONE);
            iivPinClear.setVisibility(sbPin.isChecked() ? View.VISIBLE : View.GONE);
            iivPin.setVisibility(View.GONE);
        }else if (event.equals(IEventConstants.EVENT_CLEAR_PIN)) {
            if (sbPin.isChecked()) {

                iivPin.setVisibility(View.VISIBLE);
                iivPinUpdate.setVisibility(View.GONE);
                iivPinClear.setVisibility(View.GONE);

                /*if (LockUtil.isPwdCreated(mContext)) {
                    iivCreateLock.setVisibility(View.GONE);
                    iivUpdateLock.setVisibility(View.VISIBLE);
                    iivClearLock.setVisibility(View.VISIBLE);
                } else {
                    iivCreateLock.setVisibility(View.VISIBLE);
                    iivUpdateLock.setVisibility(View.GONE);
                    iivClearLock.setVisibility(View.GONE);
                }*/
            } else {
                iivPin.setVisibility(View.GONE);
                iivPinUpdate.setVisibility(View.GONE);
                iivPinClear.setVisibility(View.GONE);
            }
        }
    }



    @Override
    protected boolean usedEventBus() {
        return true;
    }
}
