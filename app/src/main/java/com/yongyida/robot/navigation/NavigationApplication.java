package com.yongyida.robot.navigation;

import android.app.Application;

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
}
