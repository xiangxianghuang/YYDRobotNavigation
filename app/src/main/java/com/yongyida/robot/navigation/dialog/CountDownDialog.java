package com.yongyida.robot.navigation.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yongyida.robot.navigation.R;
import com.yongyida.robot.navigation.view.CircularProgressView;
import com.yongyida.robot.util.Constant;

/**
 * Create By HuangXiangXiang 2018/8/23
 */
public class CountDownDialog extends Dialog {

    private RelativeLayout mMainRlt;
//    private ProgressBar mTimerPbr;
    private TextView mTimerTvw;
    private CircularProgressView mTimerCpv ;

    private int mCountDown = 3 ;

    public CountDownDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_count_down);
        this.mMainRlt = (RelativeLayout) findViewById(R.id.main_rlt);
//        this.mTimerPbr = (ProgressBar) findViewById(R.id.timer_pbr);
        this.mTimerTvw = (TextView) findViewById(R.id.timer_tvw);
        this.mTimerCpv = (CircularProgressView) findViewById(R.id.timer_cpv);

        ViewGroup.LayoutParams layoutParams = mMainRlt.getLayoutParams() ;
        layoutParams.width = Constant.getDialogWidth(getContext());
        layoutParams.height = Constant.getDialogHeight(getContext()) ;
        mMainRlt.setLayoutParams(layoutParams);

        mHandler.post(mRunnable) ;
    }


    private Handler mHandler = new Handler() ;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {

            if(mCountDown-- <= 0){

                if(mOnCountDownListener != null){

                    mOnCountDownListener.onCountDownEnd();
                }

                dismiss();

            }else {

                mHandler.postDelayed(mRunnable, 1000) ;

                mTimerTvw.setText(String.valueOf(mCountDown + 1 ));
                mTimerCpv.setProgress((mCountDown + 1)/3.0f);
            }
        }
    } ;



    private OnCountDownListener mOnCountDownListener ;

    public void setOnCountDownListener(OnCountDownListener onCountDownListener) {
        this.mOnCountDownListener = onCountDownListener;
    }
    public interface OnCountDownListener{

        void onCountDownEnd();

    }


}
