package com.yongyida.robot.navigation.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yongyida.robot.navigation.R;
import com.yongyida.robot.util.Constant;

/**
 * Create By HuangXiangXiang 2018/8/23
 * 弹出式对话框
 */
public class TipsDialog extends Dialog implements View.OnClickListener {

    private LinearLayout mMainLLt;
    private TextView mTipsTvw;
    private Button mConfirmBtn;

    private String mTips ;

    public TipsDialog(@NonNull Context context, String tips) {
        super(context);
        this.mTips = tips ;
    }


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_tips);
        this.mMainLLt = (LinearLayout) findViewById(R.id.main_llt);
        this.mTipsTvw = (TextView) findViewById(R.id.tips_tvw);
        this.mConfirmBtn = (Button) findViewById(R.id.confirm_btn);
        this.mConfirmBtn.setOnClickListener(this);


        ViewGroup.LayoutParams layoutParams = mMainLLt.getLayoutParams() ;
        layoutParams.width = Constant.getDialogWidth(getContext());
        layoutParams.height = Constant.getDialogHeight(getContext()) ;
        mMainLLt.setLayoutParams(layoutParams);

        if(this.mTips != null ){

            this.mTipsTvw.setText(mTips);
        }

    }

    @Override
    public void onClick(View v) {

        if(mOnConfirmListener != null){

            mOnConfirmListener.onConfirm();
        }
        dismiss();
    }

    public void setTips(String tips){

        this.mTips = tips ;
        if(this.mTipsTvw != null ){

            this.mTipsTvw.setText(tips);
        }
    }


    private OnConfirmListener mOnConfirmListener ;

    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {

        this.mOnConfirmListener = onConfirmListener;
    }

    public interface OnConfirmListener{

        void onConfirm() ;
    }

}
