package com.yongyida.robot.navigation;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.yongyida.robot.navigation.bean.BaseTask;
import com.yongyida.robot.navigation.bean.TeamTask;
import com.yongyida.robot.navigation.bean.TimerTask;

import java.util.ArrayList;

/**
 * Create By HuangXiangXiang 2018/8/20
 *
 * 根据当前时间
 * 高仙运动控制接口
 * 1、加载地图（根据某个点【1.4.3】）
 * 2、根据时间和电量选择任务
 *          2.1、选择路线
 *          2.2、充电任务
 *
 *
 */
public class NavigationService extends Service {

    private static final String TAG = NavigationService.class.getSimpleName() ;

    public static void startService(Context context) {

        Intent service = new Intent(context, NavigationService.class) ;
        context.startService(service) ;
    }

    private TaskHandler mTaskHandler;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return new NavigationBinder() ;
    }

    public class NavigationBinder extends Binder{

        //1、选中的地图或者点
        public void changeSelectMapInfo(){

            mTaskHandler.loadMapAndStartPoint();
        }

        // 2、修改任务列表
        public void changeTaskInfos(){

            if(!mTaskHandler.loadMapAndStartPoint()){

                return;
            }
            mTaskHandler.loadData() ;
        }

        //3、手动控制
        public boolean controlTask(boolean isRun){

            if(!mTaskHandler.loadMapAndStartPoint()){

                return false ;
            }

            mTaskHandler.controlCurrTimerTask(isRun);
            return true ;
        }


        /**当前定时任务*/
        public TimerTask getCurrTimerTask(){

            if(!mTaskHandler.loadMapAndStartPoint()){

                return null ;
            }
            return mTaskHandler.getCurrTimerTask() ;

        }

        /**当前任务*/
        public BaseTask getCurrTask(){

            if(!mTaskHandler.loadMapAndStartPoint()){

                return null ;
            }
            return mTaskHandler.getCurrTask() ;
        }


        //
        public String getCurrTaskInfo(){

            return mTaskHandler.getCurrTaskInfo() ;
        }

        public void setOnTaskChangeListener(TaskHandler.OnTaskChangeListener onTaskChangeListener) {

            mTaskHandler.setOnTaskChangeListener(onTaskChangeListener);
        }

        /**获取列表*/
        public ArrayList<TeamTask> getTeamTask(){

            if(!mTaskHandler.loadMapAndStartPoint()){

                return null ;
            }

            return mTaskHandler.getTeamTasks() ;
        }

        /**取消收队名称*/
        public void startTeamTasks(ArrayList<String>teamNames, TeamTaskListener teamTaskListener){

            if(!mTaskHandler.loadMapAndStartPoint()){

                return  ;
            }
            mTaskHandler.startTeamTasks(false, teamNames, teamTaskListener) ;

        }

        /**取消收队名称*/
        public boolean cancelTeamTask(String teamName){

            if(!mTaskHandler.loadMapAndStartPoint()){

                return false ;
            }

            return mTaskHandler.cancelTeamTask(teamName);
        }

        public void setTimeChangedListener(TaskHandler.TimeChangedListener timeChangedListener) {

            mTaskHandler.setTimeChangedListener(timeChangedListener);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mTaskHandler = new TaskHandler(this) ;
//        mTaskHandler = TaskHandler.getInstance(this) ;
        mTaskHandler.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mTaskHandler.handleIntent(intent) ;

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {

        mTaskHandler.stop();
        super.onDestroy();
    }

}
