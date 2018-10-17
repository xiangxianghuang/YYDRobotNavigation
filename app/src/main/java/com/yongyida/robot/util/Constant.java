package com.yongyida.robot.util;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * Create By HuangXiangXiang 2018/8/23
 */
public class Constant {

    private static int dialogWidth = -1;
    private static int dialogHeight = -1;

    public static int getDialogWidth(Context context) {

        if(dialogWidth == -1){

            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics() ;

            dialogWidth = displayMetrics.widthPixels * 2 / 3 ;
            dialogHeight = displayMetrics.heightPixels * 2 / 3 ;
        }

        return dialogWidth;
    }


    public static int getDialogHeight(Context context) {

        if(dialogHeight == -1){

            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics() ;

            dialogWidth = displayMetrics.widthPixels * 2 / 3 ;
            dialogHeight = displayMetrics.heightPixels * 2 / 3 ;
        }

        return dialogHeight;
    }



    /***/
    public static void initDialogSize(Dialog dialog){

        Window window = dialog.getWindow() ;
        Context context = dialog.getContext() ;

        WindowManager.LayoutParams layoutParams = window.getAttributes();

        layoutParams.width = Constant.getDialogWidth(context);
        layoutParams.height = Constant.getDialogHeight(context) ;
        window.setAttributes(layoutParams);
    }


    public static void initFullScreenDialog(Dialog dialog){

        Window window = dialog.getWindow() ;
        Context context = dialog.getContext() ;

        WindowManager.LayoutParams layoutParams = window.getAttributes();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics() ;
        layoutParams.width = displayMetrics.widthPixels ;
        layoutParams.height = displayMetrics.heightPixels;
        window.setAttributes(layoutParams);
    }
}
