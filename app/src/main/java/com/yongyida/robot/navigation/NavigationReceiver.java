package com.yongyida.robot.navigation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yongyida.robot.util.LogHelper;

/**
 * Create By HuangXiangXiang 2018/9/7
 */
public class NavigationReceiver extends BroadcastReceiver {

    private static final String TAG = NavigationReceiver.class.getSimpleName() ;


    public static final String ACTION_BATTERY_CHANGE = "com.yongyida.robot.BATTERY_CHANGE" ;

    public static final String KEY_IS_CHARGING      = "isCharging" ;
    public static final String KEY_LEVEL            = "level" ;


    @Override
    public void onReceive(Context context, Intent intent) {

        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){

            LogHelper.i(TAG, LogHelper.__TAG__());

//            intent = new Intent(ACTION_BATTERY_CHANGE);
//            intent.putExtra(KEY_IS_CHARGING , false) ;
//            intent.putExtra(KEY_LEVEL , 95) ;
//            context.sendBroadcast(intent);
        }
    }
}
