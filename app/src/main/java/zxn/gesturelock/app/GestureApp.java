package zxn.gesturelock.app;


import android.app.Application;
import android.widget.Toast;

/**
 * Created by zxn on 2020/5/12.
 */
public class GestureApp extends Application {

    public void showToast(String msg) {
        //super.showToast(msg);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
