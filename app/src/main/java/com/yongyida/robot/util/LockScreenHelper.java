package com.yongyida.robot.util;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

/**
 * Create By HuangXiangXiang 2018/9/6
 */
public class LockScreenHelper {

    private static final String TAG = LockScreenHelper.class.getSimpleName() ;

    private static final int DELAY_TIME = 10*1000 ;    // 无操作10秒钟

    private static LockScreenHelper mInstance ;
    public static LockScreenHelper getInstance(Context context){

        if(mInstance == null) {
            mInstance = new LockScreenHelper(context.getApplicationContext()) ;
        }
        return mInstance ;
    }

    private Context mContext ;
    private LockScreenHelper(Context context) {

        this.mContext = context ;
    }


    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {

            startLockScreen() ;
        }
    } ;


    /**
     * 开启表情
     * */
    private void startLockScreen(){

        LogHelper.i(TAG, LogHelper.__TAG__());
//        adb shell am broadcast -a com.yydrobot.emotion.CHAT --es emoji "init"

        Intent intent = new Intent("com.yydrobot.emotion.CHAT") ;
        intent.putExtra("emoji" , "init") ;
        mContext.sendBroadcast(intent);

    }


    /**
     * 开启表情计时
     * */
    public void startLockScreenTimer(){
        LogHelper.i(TAG, LogHelper.__TAG__());

        cancelLockScreenTimer() ;

        mHandler.postDelayed(mRunnable, DELAY_TIME) ;
    }


    /**
     * 取消表情计时
     * */
    public void cancelLockScreenTimer(){
        LogHelper.i(TAG, LogHelper.__TAG__());

        mHandler.removeCallbacks(mRunnable);
    }


}
