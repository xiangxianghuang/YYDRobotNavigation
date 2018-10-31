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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yongyida.robot.data.MapInfoData;
import com.yongyida.robot.data.TaskInfoData;
import com.yongyida.robot.navigation.NavigationHelper;
import com.yongyida.robot.navigation.NavigationService;
import com.yongyida.robot.navigation.R;
import com.yongyida.robot.navigation.adapter.PathInfoAdapter;
import com.yongyida.robot.navigation.adapter.TaskInfoAdapter;
import com.yongyida.robot.navigation.bean.MapInfo;
import com.yongyida.robot.navigation.bean.PathInfo;
import com.yongyida.robot.navigation.bean.TimerTask;
import com.yongyida.robot.navigation.view.refresh.SwipyRefreshLayout;
import com.yongyida.robot.navigation.view.refresh.SwipyRefreshLayoutDirection;

import java.util.ArrayList;

/**
 * Create By HuangXiangXiang 2018/8/23
 */
public class TaskSettingActivity extends Activity implements View.OnClickListener {

    private static final String TAG = TaskSettingActivity.class.getSimpleName();

    /**
     * 巡线设置
     */
    private TextView mTextView;
    /**
     * 返回
     */
    private Button mBackBtn;
    private View mView;
    private ListView mTaskLvw;
    private ListView mPathLvw;
    /**
     * 保存
     */
    private Button mSaveBtn;
    /**
     * 添加
     */
    private Button mAddBtn;


    // 路径
    private ArrayList<PathInfo> mPathInfos;
    private PathInfoAdapter mPathInfoAdapter;
    private SwipyRefreshLayout mPathSrl;

    // 任务
    private ArrayList<TimerTask> mTaskInfos ;
    private TaskInfoAdapter mTaskInfoAdapter ;


    private MapInfo mSelectMapInfo ;


    private NavigationService.NavigationBinder mNavigationBinder;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mNavigationBinder = (NavigationService.NavigationBinder) service;

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
        bindService() ;

        mSelectMapInfo = MapInfoData.getSelectMapInfo(this) ;

        setContentView(R.layout.activity_task_setting);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unBindService() ;
    }

    private void initView() {
        mTextView = (TextView) findViewById(R.id.textView);
        mBackBtn = (Button) findViewById(R.id.back_btn);
        mBackBtn.setOnClickListener(this);
        mView = (View) findViewById(R.id.view);
        mTaskLvw = (ListView) findViewById(R.id.task_lvw);
        mPathLvw = (ListView) findViewById(R.id.path_lvw);
        mSaveBtn = (Button) findViewById(R.id.save_btn);
        mSaveBtn.setOnClickListener(this);
        mAddBtn = (Button) findViewById(R.id.add_btn);
        mAddBtn.setOnClickListener(this);
        mPathSrl = (SwipyRefreshLayout) findViewById(R.id.path_srl);

        mPathLvw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mPathInfoAdapter.setSelectIndex(position);
            }
        });
        mPathLvw.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                PathInfo pathInfo = mPathInfos.get(position) ;

                EditPathActivity.startEditPathActivity(TaskSettingActivity.this, pathInfo);

                return true;
            }
        });

        mPathInfoAdapter = new PathInfoAdapter(this);
        mPathLvw.setAdapter(mPathInfoAdapter);

        mPathSrl.setColorSchemeResources(R.color.colorPrimary);
        mPathSrl.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {

                // 刷新数据
                requestAllPathAndPoint() ;

            }
        });

        requestAllPathAndPoint() ;

        //获取选中地图的巡线任务
        mTaskInfos = TaskInfoData.getInstance(this).query(mSelectMapInfo.getMapName()) ;
        if(mTaskInfos == null){
            mTaskInfos = new ArrayList<>() ;
        }

        mTaskInfoAdapter = new TaskInfoAdapter(this, mTaskInfos) ;
        mTaskLvw.setAdapter(mTaskInfoAdapter);

    }

    private void requestAllPathAndPoint(){

        // 请求全部的路径和点
        NavigationHelper.requestAllPathAndPoint(mSelectMapInfo.getMapName(), new NavigationHelper.OnResponseAllPathAndPointListener() {
            @Override
            public void onSuccess(ArrayList<PathInfo> pathInfos) {
                mPathSrl.setRefreshing(false);

                mPathInfos = pathInfos;
                mPathInfoAdapter.setPathInfos(pathInfos);
            }

            @Override
            public void onFail(String message) {
                mPathSrl.setRefreshing(false);
            }

            @Override
            public void onError(String message) {
                mPathSrl.setRefreshing(false);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.back_btn:
                onBackPressed();

                break;
            case R.id.save_btn:


                TaskInfoData.getInstance(this).save(mSelectMapInfo.getMapName(), mTaskInfos);
                Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show();

                if(mNavigationBinder != null){

                    mNavigationBinder.changeTaskInfos();
                }

                break;
            case R.id.add_btn:

                int selectIndex = mPathInfoAdapter.getSelectIndex() ;
                if(selectIndex != -1){

                    PathInfo pathInfo = mPathInfos.get(selectIndex) ;

                    TimerTask taskInfo = new TimerTask() ;
                    taskInfo.setPathInfo(pathInfo);
                    mTaskInfos.add(taskInfo) ;

                    mTaskInfoAdapter.notifyDataSetChanged();

                    mPathInfoAdapter.setSelectIndex(-1);
                }

                break;

        }
    }


    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
}
