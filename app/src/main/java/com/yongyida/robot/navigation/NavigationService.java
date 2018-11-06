package com.yongyida.robot.navigation;

import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gs.gsnavlibrary.api.GsApi;
import com.gs.gsnavlibrary.listener.ResponseListener;
import com.yongyida.robot.data.MapInfoData;
import com.yongyida.robot.json.Base;
import com.yongyida.robot.json.TransferData;
import com.yongyida.robot.json.request.RequestCancelCloseTeam;
import com.yongyida.robot.json.request.RequestCloseTeam;
import com.yongyida.robot.json.request.RequestCloseTeamNames;
import com.yongyida.robot.json.request.RequestRobotInfo;
import com.yongyida.robot.json.response.BaseResponse;
import com.yongyida.robot.json.response.ResponseCancelCloseTeam;
import com.yongyida.robot.json.response.ResponseCloseTeam;
import com.yongyida.robot.json.response.ResponseRobotInfo;
import com.yongyida.robot.navigation.activity.MapActivity;
import com.yongyida.robot.navigation.bean.BaseTask;
import com.yongyida.robot.navigation.bean.MapInfo;
import com.yongyida.robot.navigation.bean.TeamTask;
import com.yongyida.robot.navigation.bean.TimerTask;
import com.yongyida.robot.util.LogHelper;
import com.yongyida.robot.util.MainServiceInfo;

import java.util.ArrayList;
import java.util.Locale;

import okhttp3.Response;

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


    private TimeChangedListener mTimeChangedListener ;
    public interface TimeChangedListener{

        void onTimeChanged() ;
    }


    private TaskHandler mTaskHandler;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return new NavigationBinder() ;
    }

    public class NavigationBinder extends Binder{

        //1、选中的地图
        public void changeSelectMapInfo(){

            initMap();
        }

        // 2、修改任务列表
        public void changeTaskInfos(){

            if(!initMap()){

                mTaskHandler.init(mSelectMapInfo) ;
            }

        }

        //3、手动控制
        public boolean controlTask(boolean isRun){

            if(!initMap()){

                mTaskHandler.controlCurrTimerTask(isRun);
                return true ;
            }
            return false ;
        }


        /**当前定时任务*/
        public TimerTask getCurrTimerTask(){

            return mTaskHandler.getCurrTimerTask() ;
        }

        /**当前任务*/
        public BaseTask getCurrTask(){

            return mTaskHandler.getCurrTask() ;
        }

        public void setTimeChangedListener(TimeChangedListener timeChangedListener){

            mTimeChangedListener = timeChangedListener ;
        }

        /**获取列表*/
        public ArrayList<TeamTask> getTeamTask(){

            return mTaskHandler.getTeamTasks() ;
        }

        /**取消收队名称*/
        public void startTeamTasks(ArrayList<String>teamNames){

            if(isInitMap){
                mTaskHandler.startTeamTasks(teamNames, mTeamTaskListener) ;
            }

        }

        /**取消收队名称*/
        public boolean cancelTeamTask(String teamName){

            if(isInitMap){
                return mTaskHandler.cancelTeamTask(teamName);
            }
            return false ;
        }

    }


    private TeamTaskListener mTeamTaskListener = new TeamTaskListener(){

        private ResponseCloseTeam responseCloseTeam = new ResponseCloseTeam();
        private ResponseCloseTeam.Fail fail = new ResponseCloseTeam.Fail() ;
        private ResponseCloseTeam.Schedule schedule = new ResponseCloseTeam.Schedule();

        @Override
        public void onStartTeamLine(String teamName) {

            responseCloseTeam.setActionAndTeamName(ResponseCloseTeam.ACTION_GO_TO_START_POINT_BY_AUXILIARY_LINE, teamName);
            MainServiceInfo.response(NavigationService.this, responseCloseTeam);
        }

        @Override
        public void onTeamTaskStart(String teamName) {

            responseCloseTeam.setActionAndTeamName(ResponseCloseTeam.ACTION_START_CLOSE_TEAM, teamName);
            MainServiceInfo.response(NavigationService.this, responseCloseTeam);
        }

        @Override
        public void onTeamTaskSchedule(String teamName, float percent) {

            schedule.setPercent(percent);
            responseCloseTeam.closingTeam(teamName, schedule);
            MainServiceInfo.response(NavigationService.this, responseCloseTeam);
        }

        @Override
        public void onTeamTaskComplete(String teamName) {

            responseCloseTeam.setActionAndTeamName(ResponseCloseTeam.ACTION_COMPLETE_CLOSE_TEAM, teamName);
            MainServiceInfo.response(NavigationService.this, responseCloseTeam);
        }

        @Override
        public void onFail(String teamName, int failCode, String failMessage) {

            fail.setFailCode(failCode);
            fail.setFailMessage(failMessage);
            responseCloseTeam.failCloseTeam(teamName, fail);
            MainServiceInfo.response(NavigationService.this, responseCloseTeam);
        }

        @Override
        public void onAllTeamTaskComplete() {

            responseCloseTeam.completeAllCloseTeam();
            MainServiceInfo.response(NavigationService.this, responseCloseTeam);
        }
    } ;


    @Override
    public void onCreate() {
        super.onCreate();

        initMap();

        mTaskHandler = TaskHandler.getInstance(this) ;
        registerReceiver();

    }

    private static final String DATA = "data" ;
    private static final Gson GSON = new Gson() ;
    private ResponseRobotInfo responseRobotInfo = new ResponseRobotInfo();


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent != null){

            String json = intent.getStringExtra(DATA) ;

            TransferData transferData = null ;
            try{
                transferData = GSON.fromJson(json, TransferData.class) ;
            }catch (Exception e){
            }
            if(transferData != null){

                switch (transferData.getType()){

                    case Base.TYPE_REQUEST_ROBOT_INFO : // 请求机器信息

                        RequestRobotInfo requestRobotInfo = null ;
                        try{
                            requestRobotInfo = GSON.fromJson(transferData.getJsonData(),RequestRobotInfo.class) ;

                        }catch (Exception e){

                            e.printStackTrace();
                        }
                        if(requestRobotInfo != null){
                            MainServiceInfo.response(NavigationService.this, responseRobotInfo);    // 响应机器信息
                        }

                        break;
                    case Base.TYPE_REQUEST_CLOSE_TEAM : // 请求收队信息

                        RequestCloseTeam requestCloseTeam = null ;
                        try{
                            requestCloseTeam = GSON.fromJson(transferData.getJsonData(),RequestCloseTeam.class) ;

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        if(requestCloseTeam != null){

                            ResponseCloseTeam responseCloseTeam = new ResponseCloseTeam() ;
                            responseCloseTeam.setInitMap(isInitMap);
                            if (isInitMap){

                                mTaskHandler.startTeamTasks(requestCloseTeam.getTeamNames(),mTeamTaskListener);

                            }else {

                                MainServiceInfo.response(NavigationService.this, responseCloseTeam);
                            }

                        }

                        break;
                    case Base.TYPE_REQUEST_CANCEL_CLOSE_TEAM: // 请求取消收队

                        RequestCancelCloseTeam requestCancelCloseTeam = null ;
                        try{
                            requestCancelCloseTeam = GSON.fromJson(transferData.getJsonData(),RequestCancelCloseTeam.class) ;

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        if(requestCancelCloseTeam != null){

                            ResponseCancelCloseTeam responseCancelCloseTeam = new ResponseCancelCloseTeam() ;
                            responseCancelCloseTeam.setInitMap(isInitMap);

                            if(isInitMap){ // 如果未初始化

                                String teamName = requestCancelCloseTeam.getTeamName();
                                responseCancelCloseTeam.setTeamName(teamName);
                                boolean isSuccessCancel = mTaskHandler.cancelTeamTask(teamName) ;
                                responseCancelCloseTeam.setSuccessCancel(isSuccessCancel);
                            }

                            MainServiceInfo.response(NavigationService.this, responseCancelCloseTeam);
                        }

                        break;
//                    case Base.TYPE_REQUEST_CLOSE_TEAM_NAMES: // 请求收队信息
//
//                        RequestCloseTeamNames requestCloseTeamNames = null ;
//                        try{
//                            requestCloseTeamNames = GSON.fromJson(transferData.getJsonData(),RequestCloseTeamNames.class) ;
//
//                        }catch (Exception e){
//
//                        }
//                        if(requestCloseTeamNames != null){
//
//
//
//
//                        }
//
//                        break;
                }
            }

        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 是否初始化点图
     * */
    private boolean isInitMap = false ;
    private MapInfo mSelectMapInfo ;
    private boolean initMap(){

        final MapInfo mapInfo = MapInfoData.getSelectMapInfo(NavigationService.this) ;
       if(mapInfo.equals(mSelectMapInfo) && isInitMap){

            return false ;
        }
        isInitMap = false ;

        final String mapName = mapInfo.getMapName() ;
        final String startPointName = mapInfo.getStartPointName() ;

        if(TextUtils.isEmpty(mapName) || TextUtils.isEmpty(startPointName) ){

            Toast.makeText(this, "没有选择地图或者起始点！！！",Toast.LENGTH_SHORT).show() ;
            Intent intent = new Intent(this,MapActivity.class ) ;
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
            startActivity(intent);

        }else{

            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if(which == DialogInterface.BUTTON_POSITIVE){

                        GsApi.useMap(new ResponseListener<String>() {
                            @Override
                            public void success(String s) {
                                LogHelper.i(TAG, LogHelper.__TAG__() + ",s : " + s);
                                loadMap(mapInfo, mapName, startPointName);
                            }

                            @Override
                            public void faild(String s, String s1) {

                                LogHelper.w(TAG, LogHelper.__TAG__() + ",s : " + s);
                            }

                            @Override
                            public void error(Throwable throwable) {

                            }
                        }, mapName) ;

                    }
                }
            };

            AlertDialog.Builder builder= new AlertDialog.Builder(this);
            builder
                    .setTitle("加载地图").
                    setMessage(String.format(Locale.CHINA, "是否使用《%s》地图的《%s》起始点加载地图？" ,mapInfo.getMapName(), mapInfo.getStartPointName()))
                    .setPositiveButton("确定", onClickListener)
                    .setNegativeButton("取消",null);

            AlertDialog alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);
//            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
            alertDialog.show();
        }

        return true ;

    }


    private void loadMap(final MapInfo mapInfo, final String mapName, final String startPointName){

        mTaskHandler.queryTeamTasks(mapName);

        // 加载地图
        ResponseListener<String> responseListener = new ResponseListener<String>() {
            @Override
            public void success(String s) {

                LogHelper.i(TAG, LogHelper.__TAG__() + ",s : " + s);

                isInitMap = true ;
                mSelectMapInfo = mapInfo ;
                mTaskHandler.init(mapInfo);
            }

            @Override
            public void faild(String s, String s1) {

                LogHelper.w(TAG, LogHelper.__TAG__() + ",s : " + s);
            }

            @Override
            public void error(Throwable throwable) {

            }
        };
        GsApi.requestInitialize(responseListener,mapName, startPointName) ;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver();

    }

    public static final String ACTION_BATTERY_CHANGE = "com.yongyida.robot.BATTERY_CHANGE" ;

    public static final String KEY_IS_CHARGING      = "isCharging" ;
    public static final String KEY_LEVEL            = "level" ;
    public static final String KEY_STATE            = "state" ;

    private void registerReceiver(){

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);        //每分钟变化
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED); //设置了系统时区
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);     //设置了系统时间
        intentFilter.addAction(ACTION_BATTERY_CHANGE);
//        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);

        registerReceiver(mTimeChangeReceiver, intentFilter);
    }

    private void unregisterReceiver(){

        unregisterReceiver(mTimeChangeReceiver);
    }

    private BroadcastReceiver mTimeChangeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction() ;
            LogHelper.i(TAG, LogHelper.__TAG__()  +  ", currentThread : " + Thread.currentThread().getName() +  ", this : " + this + ", action : " + action );

            if (Intent.ACTION_TIME_TICK.equals(action) || Intent.ACTION_TIMEZONE_CHANGED.equals(action) ||
                    Intent.ACTION_TIME_CHANGED.equals(action)){

                if(mTimeChangedListener != null){

                    mTimeChangedListener.onTimeChanged();
                }


                if(isInitMap){
                    mTaskHandler.changeTime();
                }

            } if(ACTION_BATTERY_CHANGE.equals(action)){

                boolean isCharging = intent.getBooleanExtra(KEY_IS_CHARGING , false) ;  //是否处于充电状态
                int level = intent.getIntExtra(KEY_LEVEL , -1) ;    //电量水平
//                int state = intent.getIntExtra(KEY_STATE , -1) ;    //电量状态

                refreshBatteryInfo(isCharging, level) ;

            }else if(Intent.ACTION_BATTERY_CHANGED.equals(action)){

                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL/*当前电量*/, 0);
                int state = intent.getIntExtra(BatteryManager.EXTRA_STATUS/*电池状态*/,0);

                boolean isCharging = BatteryManager.BATTERY_STATUS_CHARGING == state ;

                refreshBatteryInfo(isCharging, level) ;
            }

        }

    } ;

    private void refreshBatteryInfo(boolean isCharging, int level) {

        LogHelper.i(TAG, LogHelper.__TAG__()  +  ", isCharging : " + isCharging +  ", level : " + level ) ;

        if(isInitMap){
            mTaskHandler.batteryChange(level);
        }
    }

}
