package zxn.gesturelock.app;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

//import com.zxn.presenter.GestureApp;
//import com.zxn.presenter.presenter.BasePresenter;
//import com.zxn.presenter.presenter.CreatePresenter;
//import com.zxn.presenter.presenter.IView;
//
//import org.greenrobot.eventbus.EventBus;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 继承此类后需要配置主题,主题继承GestureAppTheme
 * Created by zxn on 2019/3/28.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
//    public P mPresenter;
    protected Context mContext;
    private Unbinder mUnbinder;

    /***是否显示标题栏*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRequestedOrientation(isPortraitOrientation() ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if (getLayoutResId() != 0) {
            setContentView(getLayoutResId());
            mUnbinder = ButterKnife.bind(this);
        }
        mContext = this;

        initImmersionBar();

        /*if (createPresenter() != null) {
            mPresenter = createPresenter();
            mPresenter.attachView(this);
        }*/
        if (usedEventBus())
            regEventBus();
    }

    /**
     * 屏幕方向
     *
     * @return :true:竖屏,false:横向
     */
    public boolean isPortraitOrientation() {
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*if (mPresenter != null) {
            mPresenter.detachView();
        }*/
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        unRegEventBus();
    }


//    @Override
//    public void showLoading() {
//        setTheme(android.R.style.Theme);
//    }

    /*private P createPresenter() {
        CreatePresenter annotation = getClass().getAnnotation(CreatePresenter.class);
        Class<P> pClass = null;
        if (annotation != null) {
            pClass = (Class<P>) annotation.value();
        }
        try {
            return mPresenter = pClass.newInstance();
        } catch (Exception e) {
            Log.i(TAG, "Presenter创建失败!，检查是否声明了@CreatePresenter(xx.class)注解");
            return null;
        }
    }*/

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * 是否使用EventBus，如果需要使用子类重载此方法并返回true
     *
     * @return 是否启用
     */
    protected boolean usedEventBus() {
        return false;
    }

    protected void regEventBus() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    protected void unRegEventBus() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    protected abstract @LayoutRes
    int getLayoutResId();

    /**
     * 初始化沉浸式
     * Init immersion bar.
     */
    protected void initImmersionBar() {
        //设置共同沉浸式样式
        //navigationBarColor(R.color.colorPrimary)
//        ImmersionBar.with(this).init();
    }

//    public void showLoading() {
//        if (getApplication() instanceof GestureApp) {
//            GestureApp baseApp = (GestureApp) getApplication();
//            baseApp.showLoading();
//        }
//    }


//    public void showToast(int msg) {
//        if (getApplication() instanceof GestureApp) {
//            GestureApp baseApp = (GestureApp) getApplication();
//            baseApp.showToast(msg);
//        }
//    }


    public void showToast(String msg) {
        if (getApplication() instanceof GestureApp) {
            GestureApp baseApp = (GestureApp) getApplication();
            baseApp.showToast(msg);
        }
    }


//    public void showLoading(boolean cancelable) {
//        if (getApplication() instanceof GestureApp) {
//            GestureApp baseApp = (GestureApp) getApplication();
//            baseApp.showLoading(cancelable);
//        }
//    }


//    public void showLoading(String msg, boolean cancelable) {
//        if (getApplication() instanceof GestureApp) {
//            GestureApp baseApp = (GestureApp) getApplication();
//            baseApp.showLoading(msg,cancelable);
//        }
//    }


//    public void showLoading(String msg) {
//        if (getApplication() instanceof GestureApp) {
//            GestureApp baseApp = (GestureApp) getApplication();
//            baseApp.showLoading(msg);
//        }
//    }


//    public void showLoading(int msgResId) {
//        if (getApplication() instanceof GestureApp) {
//            GestureApp baseApp = (GestureApp) getApplication();
//            baseApp.showLoading(msgResId);
//        }
//    }

//    public void closeLoading() {
//        if (getApplication() instanceof GestureApp) {
//            GestureApp baseApp = (GestureApp) getApplication();
//            baseApp.closeLoading();
//        }
//    }
}
