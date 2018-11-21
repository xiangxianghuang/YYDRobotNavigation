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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.yongyida.robot.navigation.NavigationService;
import com.yongyida.robot.navigation.R;
import com.yongyida.robot.navigation.TeamTaskListener;
import com.yongyida.robot.navigation.adapter.TeamAdapter;
import com.yongyida.robot.navigation.bean.TeamTask;

import java.util.ArrayList;

/**
 * Create By HuangXiangXiang 2018/10/22
 * 收队任务
 */
public class TeamActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    /**
     * 返回
     */
    private Button mBackBtn;
    /**
     * 收队
     */
    private TextView mTitleTvw;
    private GridView mTeamsGvw;

    private ArrayList<TeamTask> mTeamTasks ;
    private TeamAdapter mTeamAdapter ;


    private NavigationService.NavigationBinder mNavigationBinder;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mNavigationBinder = (NavigationService.NavigationBinder) service;

            mTeamTasks = mNavigationBinder.getTeamTask() ;

            mTeamAdapter = new TeamAdapter(TeamActivity.this, mTeamTasks) ;
            mTeamsGvw.setAdapter(mTeamAdapter);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            mNavigationBinder = null ;
        }
    };

    private void bindService(){

        Intent intent = new Intent(this, NavigationService.class) ;
        bindService(intent,mServiceConnection, Context.BIND_AUTO_CREATE) ;
    }

    private void unBindService(){

        unbindService(mServiceConnection);
        mNavigationBinder = null ;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_team);
        initView();

        bindService() ;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unBindService();
    }

    private void initView() {
        mBackBtn = (Button) findViewById(R.id.back_btn);
        mBackBtn.setOnClickListener(this);
        mTitleTvw = (TextView) findViewById(R.id.title_tvw);
        mTeamsGvw = (GridView) findViewById(R.id.teams_gvw);

        mTeamsGvw.setOnItemClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.back_btn:

                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        TeamTask teamTask = mTeamTasks.get(position) ;
        String teamName = teamTask.getTeamName() ;

        TeamTask currTeamTask = mNavigationBinder.getCurrTeamTask() ;
        if(currTeamTask == teamTask){

            mTeamNames.clear();
            mNavigationBinder.cancelTeamTask(teamName);

            mTeamAdapter.setData(null);

        }else {

            mTeamNames.clear();
            mTeamNames.add(teamName) ;
            mNavigationBinder.startTeamTasks(mTeamNames, mTeamTaskListener);

            mTeamAdapter.setData(teamName);

        }

    }

    private ArrayList<String> mTeamNames = new ArrayList<>() ;
    private TeamTaskListener mTeamTaskListener = new TeamTaskListener() {
        @Override
        public void onStartTeamLine(final String teamName) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    mTeamAdapter.setData(teamName);
                }
            });

        }

        @Override
        public void onTeamTaskStart(final String teamName) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    mTeamAdapter.setData(teamName);
                }
            });
        }

        @Override
        public void onTeamTaskSchedule(final String teamName, final float percent) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    mTeamAdapter.setData(teamName, percent);
                }
            });
        }

        @Override
        public void onTeamTaskComplete(final String teamName) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    mTeamAdapter.setData(teamName);
                }
            });

        }

        @Override
        public void onFail(final String teamName, int failCode, String failMessage) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    mTeamAdapter.setData(teamName);
                }
            });
        }

        @Override
        public void onAllTeamTaskComplete() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    mTeamAdapter.setData(null);
                }
            });
        }
    } ;


}
