package com.yongyida.robot.navigation;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.yongyida.robot.util.LogHelper;

/**
 * Create By HuangXiangXiang 2018/8/20
 */
public class NavigationApplication extends Application {

    private static final String TAG = NavigationApplication.class.getSimpleName() ;

    @Override
    public void onCreate() {
        super.onCreate();

        LogHelper.i(TAG, LogHelper.__TAG__());

        NavigationService.startService(this);
    }



    private static final int WHAT_TOAST = 0x01 ;    // toast信息
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case WHAT_TOAST:

                    String text = (String) msg.obj;
                    showToastInUIThread(text) ;

                    break;
            }
        }
    };

    private static boolean isMainThread() {

        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }
    /**
     * 不在主线程显示toast 信息
     * */
    public void showToast(String text){

        if(isMainThread()){

            showToastInUIThread(text) ;
        }else {

            Message message = mHandler.obtainMessage(WHAT_TOAST) ;
            message.obj = text ;
            mHandler.sendMessage(message) ;
        }
    }

    private Toast mToast ;
    private void showToastInUIThread(String text){

        if(mToast == null){

            mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT) ;
            mToast.show();

        }else {

            mToast.setText(text);
            mToast.show();
        }
    }

}
