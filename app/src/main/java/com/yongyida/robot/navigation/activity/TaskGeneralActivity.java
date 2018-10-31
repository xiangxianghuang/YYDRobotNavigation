package com.yongyida.robot.navigation.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yongyida.robot.navigation.NavigationService;
import com.yongyida.robot.navigation.R;
import com.yongyida.robot.navigation.bean.BaseTask;
import com.yongyida.robot.navigation.bean.TimerTask;
import com.yongyida.robot.navigation.dialog.CurrentTaskStateDialog;
import com.yongyida.robot.util.LockScreenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Create By HuangXiangXiang 2018/8/23
 * 导航任务总概
 */
public class TaskGeneralActivity extends Activity implements View.OnClickListener {


    /**
     * 当前任务状态
     */
    private Button mCurrentTaskStateBtn;
    /**
     * 巡线任务设置
     */
    private Button mTaskSettingBtn;
    private Button mMapSettingBtn;

    /**
     * 11:15
     */
    private TextView mTimeTvw;


    private NavigationService.TimeChangedListener mTimeChangedListener = new NavigationService.TimeChangedListener() {
        @Override
        public void onTimeChanged() {

            refreshTime();
        }
    };


    private NavigationService.NavigationBinder mNavigationBinder;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mNavigationBinder = (NavigationService.NavigationBinder) service;
            mNavigationBinder.setTimeChangedListener(mTimeChangedListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            mNavigationBinder.setTimeChangedListener(null);
            mNavigationBinder = null;
        }
    };
    /**
     * 收队任务
     */
    private Button mTeamBtn;


    private void bindService() {

        Intent intent = new Intent(this, NavigationService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void unBindService() {

        unbindService(mServiceConnection);
        mNavigationBinder = null;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindService();

        setContentView(R.layout.activity_task_general);
        initView();

        refreshTime();
    }

    @Override
    protected void onResume() {
        super.onResume();

        LockScreenHelper.getInstance(this).startLockScreenTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();

        LockScreenHelper.getInstance(this).cancelLockScreenTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unBindService();
    }

    private void initView() {
        mCurrentTaskStateBtn = (Button) findViewById(R.id.current_task_state_btn);
        mCurrentTaskStateBtn.setOnClickListener(this);
        mTaskSettingBtn = (Button) findViewById(R.id.task_setting_btn);
        mTaskSettingBtn.setOnClickListener(this);
        mMapSettingBtn = findViewById(R.id.map_setting_btn);
        mMapSettingBtn.setOnClickListener(this);
        mTimeTvw = (TextView) findViewById(R.id.time_tvw);
        mTeamBtn = (Button) findViewById(R.id.team_btn);
        mTeamBtn.setOnClickListener(this);
    }

    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("HH:mm");

    /**
     * 更新时间
     */
    private void refreshTime() {

        String time = mSimpleDateFormat.format(new Date());
        mTimeTvw.setText(time);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.current_task_state_btn:

                if (mNavigationBinder != null) {

                    TimerTask currTimerTask = mNavigationBinder.getCurrTimerTask();

                    showCurrentTaskStateDialog(currTimerTask);

                }

                break;
            case R.id.task_setting_btn:

                Intent intent = new Intent(this, TaskSettingActivity.class);
                startActivity(intent);

                break;
            case R.id.map_setting_btn:

                intent = new Intent(this, MapActivity.class);
                startActivity(intent);

                break;
            case R.id.team_btn:

                intent = new Intent(this, TeamActivity.class);
                startActivity(intent);

                break;
        }
    }

    private CurrentTaskStateDialog mCurrentTaskStateDialog;
    private CurrentTaskStateDialog.ToggleTaskListener mToggleTaskListener = new CurrentTaskStateDialog.ToggleTaskListener() {
        @Override
        public boolean toggleTask(boolean isTaskRun) {

            return mNavigationBinder.controlTask(isTaskRun);
        }
    };

    private void showCurrentTaskStateDialog(TimerTask currTaskInfo) {

        if (mCurrentTaskStateDialog == null) {

            mCurrentTaskStateDialog = new CurrentTaskStateDialog(this,currTaskInfo);
            mCurrentTaskStateDialog.setToggleTaskListener(mToggleTaskListener);

        } else {

            mCurrentTaskStateDialog.setTask(currTaskInfo);
        }

        mCurrentTaskStateDialog.show();

    }

}
