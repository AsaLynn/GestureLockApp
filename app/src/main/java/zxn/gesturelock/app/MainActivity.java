package zxn.gesturelock.app;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.zxn.iconitemview.IconItemView;
import com.zxn.presenter.view.BaseActivity;
import com.zxn.titleview.TitleView;
import com.zxn.widget.SwitchButton;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * com.zxn.gesturelock
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        iivUpdateLock.setLeftText("创建手势密码");
        sbLock.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                iivUpdateLock.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                //iivClearLock.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onTouchCheckedChanged(SwitchButton view, boolean isChecked) {

            }
        });

        iivUpdateLock.setVisibility(sbLock.isChecked() ? View.VISIBLE : View.GONE);
        //iivClearLock.setVisibility(sbLock.isChecked() ? View.VISIBLE : View.GONE);

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @OnClick({R.id.iiv_update_lock, R.id.iiv_clear_lock})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iiv_update_lock:
                GestureLockSettingAty.jumpTo(mContext);
                break;
            case R.id.iiv_clear_lock:

                break;
        }
    }
}
