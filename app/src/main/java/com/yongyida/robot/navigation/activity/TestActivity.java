package com.yongyida.robot.navigation.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gs.gsnavlibrary.api.GsApi;
import com.gs.gsnavlibrary.bean.common.NowPosition;
import com.gs.gsnavlibrary.bean.common.RotateTo;
import com.gs.gsnavlibrary.listener.ResponseListener;
import com.yongyida.robot.navigation.NavigationHelper;
import com.yongyida.robot.navigation.R;
import com.yongyida.robot.util.LogHelper;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Create By HuangXiangXiang 2018/9/4
 */
public class TestActivity extends Activity implements View.OnClickListener {

    private static final String TAG = TestActivity.class.getSimpleName();

    private EditText mAngleEdt;
    /**
     * 转动到指定位置
     */
    private Button mExecuteBtn;
    /**
     * 20
     */
    private TextView mCurrentTvw;
    /**
     * 查询当前角度
     */
    private Button mQueryAngleBtn;
    /**
     * 自动就近路径充电
     */
    private Button mGoToNearbyPathChargingBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);
        initView();
    }

    private void initView() {
        mAngleEdt = (EditText) findViewById(R.id.angle_edt);
        mExecuteBtn = (Button) findViewById(R.id.execute_btn);
        mExecuteBtn.setOnClickListener(this);
        mCurrentTvw = (TextView) findViewById(R.id.current_tvw);
        mQueryAngleBtn = (Button) findViewById(R.id.query_angle_btn);
        mQueryAngleBtn.setOnClickListener(this);
        mGoToNearbyPathChargingBtn = (Button) findViewById(R.id.go_to_nearby_path_charging_btn);
        mGoToNearbyPathChargingBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.execute_btn:

//                PlayVideoActivity.startActivity(this, "", 2, new VideoHelper.PlayVideoListener() {
//                    @Override
//                    public void onEnd() {
//                        LogHelper.i(TAG, LogHelper.__TAG__()) ;
//                    }
//
//                    @Override
//                    public void onError() {
//
//                        LogHelper.i(TAG, LogHelper.__TAG__()) ;
//                    }
//                });


                String data = mAngleEdt.getText().toString();
                int angle = 90;
                try {
                    angle = Integer.parseInt(data);

                } catch (Exception e) {

                    mAngleEdt.setText(String.valueOf(angle));
                }

                turnToAngle(angle);

                break;

            case R.id.query_angle_btn:

                GsApi.getNowPosition(new ResponseListener<NowPosition>() {
                    @Override
                    public void success(NowPosition nowPosition) {

                        int currAngle = (int) nowPosition.angle;
                        if (currAngle < 0) {

                            currAngle += 360;
                        }


                        LogHelper.i(TAG, LogHelper.__TAG__() + ",当前 angle : " + currAngle);
                        mCurrentTvw.setText("当前 angle : " + currAngle);

                    }

                    @Override
                    public void faild(String s, String s1) {

                    }

                    @Override
                    public void error(Throwable throwable) {

                    }
                });


                break;
            case R.id.go_to_nearby_path_charging_btn:

                NavigationHelper.goToNearbyPathCharging(mapName,pathName, pointName);
                break;
        }
    }

    String mapName = "YYD2B";
    String pathName = "zdhc";       // 充电路径
    String pointName = "zdhcd";     // 充电点

    /**
     * 转至到角度
     */
    private void turnToAngle(final int angle) {

        LogHelper.i(TAG, LogHelper.__TAG__() + ", angle : " + angle);

        // 由于没有指定转至指定角度的接口，先查询当前的角度
        GsApi.getNowPosition(new ResponseListener<NowPosition>() {
            @Override
            public void success(NowPosition nowPosition) {

                int currAngle = (int) nowPosition.angle;
                if (currAngle < 0) {

                    currAngle += 360;
                }

                LogHelper.i(TAG, LogHelper.__TAG__() + ",当前 angle : " + currAngle);

                int nowAngle = (int) (angle - currAngle);
                LogHelper.i(TAG, LogHelper.__TAG__() + ",转动 angle : " + nowAngle);
                RotateTo rotateTo = new RotateTo();
                rotateTo.rotateAngle = nowAngle;
                rotateTo.rotateSpeed = 0.5f;

                GsApi.requestRotateTo(new ResponseListener<String>() {
                    @Override
                    public void success(String s) {

                    }

                    @Override
                    public void faild(String s, String s1) {

                    }

                    @Override
                    public void error(Throwable throwable) {

                    }
                }, rotateTo);
            }

            @Override
            public void faild(String s, String s1) {

            }

            @Override
            public void error(Throwable throwable) {

            }
        });

        startListener();



    }


    private Timer mTurnToAngleTimer;
    private TimerTask mTurnToAngleTask;

    private void startListener() {
        stopListener();

        mTurnToAngleTimer = new Timer();
        mTurnToAngleTask = new TimerTask() {
            @Override
            public void run() {

                GsApi.isRotateFinishedListener(responseListener);
            }
        };
        mTurnToAngleTimer.schedule(mTurnToAngleTask, 1000);
    }


    private void stopListener() {

        if (mTurnToAngleTimer != null) {

            mTurnToAngleTimer.cancel();
            mTurnToAngleTimer = null;
        }

        if (mTurnToAngleTask != null) {

            mTurnToAngleTask.cancel();
            mTurnToAngleTask = null;
        }
    }


    private ResponseListener<Boolean> responseListener = new ResponseListener<Boolean>() {
        @Override
        public void success(Boolean aBoolean) {

            LogHelper.i(TAG, LogHelper.__TAG__() + ", aBoolean : " + aBoolean);
            if (aBoolean) {

                stopListener();
            }
        }

        @Override
        public void faild(String s, String s1) {
        }

        @Override
        public void error(Throwable throwable) {
        }
    };

}
