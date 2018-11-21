package com.yongyida.robot.navigation;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gs.gsnavlibrary.api.GsApi;
import com.gs.gsnavlibrary.api.GsNav;
import com.gs.gsnavlibrary.api.TaskQueueApi;
import com.gs.gsnavlibrary.base.GsNavStatus;
import com.gs.gsnavlibrary.bean.common.NowPosition;
import com.gs.gsnavlibrary.bean.common.RotateTo;
import com.gs.gsnavlibrary.listener.ConnectListener;
import com.gs.gsnavlibrary.listener.GsStatusListener;
import com.gs.gsnavlibrary.listener.ResponseListener;
import com.yongyida.robot.communicate.app.common.send.SendClient;
import com.yongyida.robot.communicate.app.common.send.SendResponseListener;
import com.yongyida.robot.communicate.app.hardware.motion.response.data.MotionSystem;
import com.yongyida.robot.communicate.app.hardware.motion.send.data.ArmControl;
import com.yongyida.robot.communicate.app.hardware.motion.send.data.FingerControl;
import com.yongyida.robot.communicate.app.hardware.motion.send.data.FootControl;
import com.yongyida.robot.communicate.app.hardware.motion.send.data.HandControl;
import com.yongyida.robot.communicate.app.hardware.motion.send.data.HeadControl;
import com.yongyida.robot.communicate.app.hardware.motion.send.data.QueryMotionSystemControl;
import com.yongyida.robot.communicate.app.hardware.motion.send.data.SteeringControl;
import com.yongyida.robot.communicate.app.hardware.motion.send.data.constant.Direction;
import com.yongyida.robot.data.MapInfoData;
import com.yongyida.robot.data.MediaData;
import com.yongyida.robot.data.PathDetailInfoData;
import com.yongyida.robot.data.TaskInfoData;
import com.yongyida.robot.json.Base;
import com.yongyida.robot.json.TransferData;
import com.yongyida.robot.json.request.RequestCancelCloseTeam;
import com.yongyida.robot.json.request.RequestCloseTeam;
import com.yongyida.robot.json.request.RequestCloseTeamNames;
import com.yongyida.robot.json.request.RequestRobotInfo;
import com.yongyida.robot.json.response.ResponseCancelCloseTeam;
import com.yongyida.robot.json.response.ResponseCloseTeam;
import com.yongyida.robot.json.response.ResponseCloseTeamNames;
import com.yongyida.robot.json.response.ResponseRobotInfo;
import com.yongyida.robot.navigation.activity.MapActivity;
import com.yongyida.robot.navigation.bean.BaseTask;
import com.yongyida.robot.navigation.bean.ChargingTask;
import com.yongyida.robot.navigation.bean.MapInfo;
import com.yongyida.robot.navigation.bean.PathStatus;
import com.yongyida.robot.navigation.bean.PointActionInfo;
import com.yongyida.robot.navigation.bean.TeamTask;
import com.yongyida.robot.navigation.bean.TimerTask;
import com.yongyida.robot.navigation.bean.path.ActionData;
import com.yongyida.robot.navigation.bean.path.BaseAction;
import com.yongyida.robot.navigation.bean.path.PlayAction;
import com.yongyida.robot.navigation.bean.path.StopAction;
import com.yongyida.robot.util.LogHelper;
import com.yongyida.robot.util.MainServiceInfo;
import com.yongyida.robot.voice.CloseTeamHelper;
import com.yongyida.robot.voice.PlayerHelper;
import com.yongyida.robot.voice.VideoHelper;
import com.yongyida.robot.voice.VoiceHelper;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;

/**
 * Create By HuangXiangXiang 2018/8/31
 * 任务优先级：数字越小，优先级越高
 *
 * 定时任务（优先级3）
 *      触发条件
 *       1、控制当前定时任务
 *       2、时间变化
 *       3、电量变化
 *
 *  充电任务（优先级2）
 *      触发条件
 *      1、用户主动触发（没有）
 *      2、低于指定电量
 *      3、没有其他任务
 *
 *  收队任务（优先级1）
 *      触发条件
 *      1、用户主动触发
 *
 * 在每一次触发条件时，
 *
 */
public class TaskHandler {

    private static final String TAG = TaskHandler.class.getSimpleName() ;

//    private static TaskHandler mInstance ;
//    public static TaskHandler getInstance(Context context){
//
//        if(mInstance == null) {
//            mInstance = new TaskHandler(context.getApplicationContext()) ;
//        }
//        return mInstance ;
//    }

    private Context mContext ;

    // 当前任务
    private BaseTask mCurrTask ;

    // 定时任务
    private TimerTask mCurrTimerTask ;                                  // 当前定时任务
    private ArrayList<TimerTask> mTimerTasks ;                          // 全部的定时任务

    // 充电任务
    private ChargingTask mChargingTask = new ChargingTask();            // 充电任务

    // 收队任务
    private TeamTask mCurrTeamTask ;                                        // 当前收队任务
    private ArrayList<TeamTask> mTeamTasks = new ArrayList<>();         // 全部的收队任务


    private int hour ;                                                  // 当前小时
    private int minute ;                                                // 当前分钟
    private int charger = 0;                                            // 充电状态，0：未充电，3：自动充电，4：手动充电，5：自动充电下电池充满
    private int batteryLevel = 100 ;                                    // 初始化高 否则机器一开始去充电
//    private boolean isChargingForward = false ;                       // 是否尝试向前冲，离开充电桩
    private ResponseRobotInfo responseRobotInfo = new ResponseRobotInfo();//机器信息
    /**
     * 是否初始化点图
     * */
    private boolean isInitMap = false ;
    /**
     * 选中的地图信息
     * */
    private MapInfo mSelectMapInfo ;


    /**
     * 是否在工作区域
     *          true    在工作区
     *          false   在充电区
     * */
    private boolean isInWorkSpace = true ;


    TaskHandler(Context context) {

        mContext = context;

        PlayerHelper.getInstance() ;
        VideoHelper.getInstance(context) ;
        VoiceHelper.getInstance(context) ;
        CloseTeamHelper.getInstance(context);
    }


    /**
     * 开始
     * */
    public void start(){

        refreshTime() ;
        startListen() ;
        registerReceiver() ;
        startListenerUnderPanState() ;

    }


    /**
     * 停止
     * */
    public void stop(){

        unregisterReceiver();
        stopListenerUnderPanState() ;
    }


    private static final int WHAT_TOAST                             = 0x10000 ;
    private static final int WHAT_USE_MAP_SUCCESS                   = 0x20001 ;
    private static final int WHAT_USE_MAP_FAIL                      = 0x20002 ;
    private static final int WHAT_USE_MAP_ERROR                     = 0x20003 ;
    private static final int WHAT_REQUEST_INITIALIZE_SUCCESS        = 0x20101 ;
    private static final int WHAT_REQUEST_INITIALIZE_FAIL           = 0x20102 ;
    private static final int WHAT_REQUEST_INITIALIZE_ERROR          = 0x20103 ;
    private static final int WHAT_DOOR_IN                           = 0x21001 ;
    private static final int WHAT_DOOR_OUT                          = 0x21002 ;
    private static final int WHAT_START_PLAY_VIDEO                  = 0x30001 ;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case WHAT_TOAST:

                    String text = (String) msg.obj;
                    showToastInUIThread(text) ;

                    break;

                case WHAT_USE_MAP_SUCCESS:
                    text = (String) msg.obj;
                    showToast(text);

                    showProgress("加载"+ mSelectMapInfo.getMapName() +"地图成功，开始转圈初始化" + mSelectMapInfo.getStartPointName() + "起始点") ;
                    setStartPoint( mSelectMapInfo.getMapName(), mSelectMapInfo.getStartPointName());
                    break;
                case WHAT_USE_MAP_FAIL:

                    text = (String) msg.obj;
                    showToast(text);

                    loadMapAndStartPoint() ;
                    break;

                case WHAT_USE_MAP_ERROR:

                    text = (String) msg.obj;
                    showToast(text);

                    loadMapAndStartPoint() ;
                    break;

                case WHAT_REQUEST_INITIALIZE_SUCCESS:

                    text = (String) msg.obj;
                    showToast(text);

                    isInitMap = true ;

                    isInWorkSpace = !(NavigationHelper.POINT_IN_AUTO_CHARGING_NAME.equals(mSelectMapInfo.getStartPointName()));

                    loadData();
                    dismissProgress();

                    break;
                case WHAT_REQUEST_INITIALIZE_FAIL:

                    text = (String) msg.obj;
                    showToast(text);

                    loadMapAndStartPoint() ;
                    break;
                case WHAT_REQUEST_INITIALIZE_ERROR:

                    text = (String) msg.obj;
                    showToast(text);

                    loadMapAndStartPoint() ;
                    break;

                case WHAT_DOOR_IN:

                    onDoorIn() ;
                    break;

                case WHAT_DOOR_OUT:

                    onDoorOut() ;
                    break;

                case WHAT_START_PLAY_VIDEO:

                    if(mExecuteActionThread!=null){

                        mExecuteActionThread.startPlayVideo();
                    }
                    break;
            }

        }
    } ;


    /**
     * 判断当前是否是主(UI)线程
     * */
    private static boolean isMainThread() {

        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }
    /**
     * 不管是否在主线程显示toast 信息
     * */
    private void showToast(String text){
//
//        if(isMainThread()){
//
//            showToastInUIThread(text) ;
//        }else {
//
//            Message message = mHandler.obtainMessage(WHAT_TOAST) ;
//            message.obj = text ;
//            mHandler.sendMessage(message) ;
//        }
    }

    private Toast mToast ;
    /**
     * 在主线程显示toast 信息
     * */
    private void showToastInUIThread(String text){

        if(mToast == null){

            mToast = Toast.makeText(mContext, text, Toast.LENGTH_LONG) ;
            mToast.show();

        }else {
            mToast.setText(text);
            mToast.show();
        }
    }


    /**
     * 获取当前的任务
     * */
    public BaseTask getCurrTask() {

        return mCurrTask;
    }

    /**
     * 获取当前的定时任务
     * */
    public TimerTask getCurrTimerTask() {

        return mCurrTimerTask;
    }

    /**
     * 判断是否加载地图和起始点
     *  如果已经加载返回true; 没有加载返回false,并且加载
     * @return
     *      true 表示已经加载了地图
     *      false 正在加载地图
     * */
    public boolean loadMapAndStartPoint(){

        dismissProgress() ;

        final MapInfo mapInfo = MapInfoData.getSelectMapInfo(mContext) ;
        if(mapInfo.equals(mSelectMapInfo) && isInitMap){

//            return isInitMap ;
            return true ;
        }

        mSelectMapInfo = mapInfo ;
        isInitMap = false ;


        final String mapName = mapInfo.getMapName() ;
        final String startPointName = mapInfo.getStartPointName() ;

        if(TextUtils.isEmpty(mapName) || TextUtils.isEmpty(startPointName) ){

            showToast("没有选择地图或者起始点,请选择地图和起始点！！！");

            Intent intent = new Intent(mContext, MapActivity.class ) ;
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
            mContext.startActivity(intent);

        }else{

            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if(which == DialogInterface.BUTTON_POSITIVE){

                        // 加载地图
                        GsApi.useMap(new ResponseListener<String>() {
                            @Override
                            public void success(String s) {
                                LogHelper.i(TAG, LogHelper.__TAG__() + ",s : " + s);

                                Message message = mHandler.obtainMessage(WHAT_USE_MAP_SUCCESS) ;
                                message.obj = "加载地图成功" ;
                                mHandler.sendMessage(message) ;

                            }

                            @Override
                            public void faild(String s, String s1) {

                                LogHelper.w(TAG, LogHelper.__TAG__() + ",s : " + s);
                                Message message = mHandler.obtainMessage(WHAT_USE_MAP_FAIL) ;
                                message.obj = "失败s：" + s + ",s1 : " + s1 ;
                                mHandler.sendMessage(message) ;

                            }

                            @Override
                            public void error(Throwable throwable) {

                                LogHelper.e(TAG, LogHelper.__TAG__() + ",throwable : " + throwable.getMessage());
                                Message message = mHandler.obtainMessage(WHAT_USE_MAP_ERROR) ;
                                message.obj = "异常：" + throwable.getMessage() ;
                                mHandler.sendMessage(message) ;

                            }
                        }, mapName) ;

                        showProgress("正在加载"+ mapName +"地图") ;

                    }
                }
            };

            AlertDialog.Builder builder= new AlertDialog.Builder(mContext);
            builder
                    .setTitle("加载地图").
                    setMessage(String.format(Locale.CHINA, "是否使用《%s》地图的《%s》起始点加载地图？" ,mapInfo.getMapName(), mapInfo.getStartPointName()))
                    .setPositiveButton("确定", onClickListener)
                    .setNegativeButton("取消",null);

            AlertDialog alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
            alertDialog.show();
        }
        return isInitMap ;
    }

    private ProgressDialog mProgressDialog ;
    /**
     * 显示进度条
     * */
    private void showProgress(String text){

        if(mProgressDialog == null){

            mProgressDialog = new ProgressDialog(mContext) ;
            mProgressDialog.setTitle("加载地图");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        }

        mProgressDialog.setMessage(text);
        mProgressDialog.show();

    }

    /**隐藏对话框*/
    private void dismissProgress(){

        if(mProgressDialog != null && mProgressDialog.isShowing()){

            mProgressDialog.dismiss();
        }
    }

    /**设置起始点*/
    private void setStartPoint(final String mapName, final String startPointName){

        // 加载地图
        ResponseListener<String> responseListener = new ResponseListener<String>() {
            @Override
            public void success(String s) {

                LogHelper.i(TAG, LogHelper.__TAG__() + ",s : " + s);

                Message message = mHandler.obtainMessage(WHAT_REQUEST_INITIALIZE_SUCCESS) ;
                message.obj = "设置起始点成功" ;
                mHandler.sendMessage(message) ;
            }

            @Override
            public void faild(String s, String s1) {

                LogHelper.w(TAG, LogHelper.__TAG__() + ",s : " + s);

                Message message = mHandler.obtainMessage(WHAT_REQUEST_INITIALIZE_FAIL) ;
                message.obj = "失败s：" + s + ",s1 : " + s1 ;
                mHandler.sendMessage(message) ;
            }

            @Override
            public void error(Throwable throwable) {

                LogHelper.e(TAG, LogHelper.__TAG__() + ",throwable : " + throwable.getMessage());

                Message message = mHandler.obtainMessage(WHAT_REQUEST_INITIALIZE_ERROR) ;
                message.obj = "异常：" + throwable.getMessage() ;
                mHandler.sendMessage(message) ;

            }
        };
        GsApi.requestInitialize(responseListener,mapName, startPointName) ;
    }


    private QueryMotionSystemControl mQueryMotionSystemControl = new QueryMotionSystemControl();
    private SendResponseListener mSendResponseListener = new SendResponseListener<MotionSystem>(){

        private String getFaultInfo(MotionSystem motionSystem){

            if(motionSystem!= null){
                ArrayList<MotionSystem.Item> items = motionSystem.getItems() ;
                if(items != null){
                    final int size = items.size() ;
                    for (int i = 0; i < size; i ++ ){

                        MotionSystem.Item item = items.get(i) ;
                        if("运动故障信息".equals(item.title)){

                            return item.info ;
                        }
                    }
                }
            }
            return null ;
        }

        @Override
        public void onSuccess(MotionSystem motionSystem) {

            String faultInfo = getFaultInfo(motionSystem);
            if(!TextUtils.isEmpty(faultInfo)){

                boolean isChange = responseRobotInfo.setUnderPanState(1);
                if(isChange){
                    onRobotInfoChanged() ;
                }
            }

        }

        @Override
        public void onFail(int result, String message) {

        }
    } ;

    private void startListenerUnderPanState(){

        //查询错误信息
        SendClient.getInstance(mContext).send(mContext, mQueryMotionSystemControl, mSendResponseListener );
    }

    private void stopListenerUnderPanState(){

        //查询错误信息
        SendClient.getInstance(mContext).send(mContext, mQueryMotionSystemControl, null );
    }

    private static final String ACTION_BATTERY_CHANGE = "com.yongyida.robot.BATTERY_CHANGE" ;

    private static final String KEY_IS_CHARGING      = "isCharging" ;
    private static final String KEY_LEVEL            = "level" ;
    public static final String KEY_STATE            = "state" ;

    private void registerReceiver(){

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);        //每分钟变化
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED); //设置了系统时区
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);     //设置了系统时间
        intentFilter.addAction(ACTION_BATTERY_CHANGE);
//        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);

        mContext.registerReceiver(mTimeChangeReceiver, intentFilter);
    }

    private void unregisterReceiver(){

        mContext.unregisterReceiver(mTimeChangeReceiver);
    }

    public void setTimeChangedListener(TimeChangedListener timeChangedListener){

        mTimeChangedListener = timeChangedListener ;
    }
    private TimeChangedListener mTimeChangedListener = new TimeChangedListener() {

        @Override
        public void onTimeChanged() {

            refreshTime();
        }
    };

    public interface TimeChangedListener{

        void onTimeChanged();
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

                    changeTime();
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
            batteryChange(level);
        }

        boolean isChange1 = responseRobotInfo.setBatteryState(isCharging ? 1 : 0 );
        boolean isChange2 = responseRobotInfo.setBatteryLevel(level);
        if(isChange1 || isChange2){
            onRobotInfoChanged() ;
        }
    }

    //信息变化
    private void onRobotInfoChanged(){

        MainServiceInfo.response(mContext, responseRobotInfo);    // 响应机器信息
    }

    /**
     * 监听路径运行回调
     * */
    private void startListen(){

        // 任务状态推送
        GsApi.addStatusListener(new GsStatusListener() {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {

                LogHelper.w(TAG, LogHelper.__TAG__() + ", httpStatus : " + serverHandshake.getHttpStatus() + ", httpStatusMessage : " + serverHandshake.getHttpStatusMessage());
            }
            private String last ;
            @Override
            public void onMessage(String s) {

                if(!s.equals(last)){
                    last = s ;
                    LogHelper.w(TAG, LogHelper.__TAG__() + ", s : " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s) ;
                        int statusCode = jsonObject.getInt("statusCode") ;

                        switch (statusCode){
                            case 407: // 到达目标点

                                JSONObject statusData = jsonObject.getJSONObject("statusData") ;
                                String pointName = statusData.getString("name") ;
                                isArrivedLeaveChargingPathEnd(pointName) ;

                                break;

                            case 409: // 寻找充电桩

                                statusData = jsonObject.getJSONObject("statusData") ;
                                pointName = statusData.getString("name") ;
                                isArrivedLeaveChargingPathEnd(pointName) ;
                                break;

                            case 302: // 跟线中

                                PathStatus pathStatus = GSON.fromJson(s, PathStatus.class) ;
                                LogHelper.e(TAG, LogHelper.__TAG__() + ", pathStatus : " + pathStatus);

                                if(pathStatus != null && pathStatus.statusData != null){

                                    ArrayList<String> passedPointsList = pathStatus.statusData.getPassedPointsList() ;
                                    if(!passedPointsList.isEmpty()){
                                        pointName = passedPointsList.get(0) ;
                                        if(isArrivedLeaveChargingPathEnd(pointName)){

                                            return;
                                        }
                                        onArrivedPoint(pathStatus.statusData.graphName, pointName);
                                    }else{

                                        onFollowingPath(pathStatus) ;

                                    }
                                }
                                break;
                            case 305:
                                startCurrentTask();

                                break;
                            case 306:   // 跑路径结束

                                pathStatus = GSON.fromJson(s, PathStatus.class) ;
                                LogHelper.e(TAG, LogHelper.__TAG__() + ", pathStatus : " + pathStatus);

                                if(pathStatus != null && pathStatus.statusData != null){

                                    ArrayList<String> passedPointsList = pathStatus.statusData.getPassedPointsList() ;

                                    if(!passedPointsList.isEmpty()){
                                        pointName = passedPointsList.get(0) ;
                                        if(isArrivedLeaveChargingPathEnd(pointName)){

                                            return;
                                        }

                                        onArrivedPoint(pathStatus.statusData.graphName, pointName);
                                    }
                                }
                                break;


                            case 1006: // 定位丢失

                                break;

                            case 1110: // 被包围中

                                String statusMsg = jsonObject.getString("statusMsg") ;
                                LogHelper.e(TAG, LogHelper.__TAG__() + ", statusMsg : " + statusMsg);
                                surround() ;
                                break;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onClose(int i, String s, boolean b) {

                LogHelper.w(TAG, LogHelper.__TAG__() + ", i : " + i + ", s : " + s + ", b : " + b);
            }

            @Override
            public void onError(Exception e) {

                LogHelper.w(TAG, LogHelper.__TAG__() + ", Exception : " + e.getMessage());
            }
        }, GsNavStatus.pushstatus.ROBOT_STATUS) ;

        // 设备状态推送
        GsApi.addStatusListener(new GsStatusListener() {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {

                LogHelper.w(TAG, LogHelper.__TAG__() + ", httpStatus : " + serverHandshake.getHttpStatus() + ", httpStatusMessage : " + serverHandshake.getHttpStatusMessage());
            }
            private String last ;
            @Override
            public void onMessage(String s) {

                if(!s.equals(last)){
                    last = s ;
//                    LogHelper.w(TAG, LogHelper.__TAG__() + ", s : " + s);

                    try {
                        JSONObject jsonObject = new JSONObject(s) ;
                        int charger = jsonObject.getInt("charger") ;

                        if(TaskHandler.this.charger != charger){
                            TaskHandler.this.charger = charger;

                            if(charger == 0){

                                if(mLeaveChargingPointListener != null){
                                    mLeaveChargingPointListener.onLeaveChargingPoint();
                                    mLeaveChargingPointListener = null ;
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onClose(int i, String s, boolean b) {

                LogHelper.w(TAG, LogHelper.__TAG__() + ", i : " + i + ", s : " + s + ", b : " + b);
            }

            @Override
            public void onError(Exception e) {

                LogHelper.w(TAG, LogHelper.__TAG__() + ", Exception : " + e.getMessage());
            }
        }, GsNavStatus.pushstatus.DEVICE_SATUS) ;

        GsNav gsNav = new GsNav.Builder().setContext(mContext).build() ;
        gsNav.connect(new ConnectListener() {
            @Override
            public void status(int i, String s) {
//                LogHelper.w(TAG, LogHelper.__TAG__() + ", i : " + i + ", s : " + s);
            }
        });

    }

    /**
     * 被包围
     * */
    private void surround(){

        if(!isSurround){

            isSurround = true ;
            onSurround() ;
        }

        playSurround() ;
        showToast("机器人已经被包围了") ;

        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, SURROUND_TIME) ;
    }

    /**通知被包围*/
    private void onSurround(){
        LogHelper.e(TAG, LogHelper.__TAG__()) ;
        // 暂停任务
        pauseTask();
    }

    /**通知接触包围*/
    private void onDisSurround(){
        LogHelper.e(TAG, LogHelper.__TAG__()) ;
        // 继续任务
        resumeTask();
    }

    private MediaPlayer mPlayer ;
    private void playSurround(){
        if(mPlayer == null){
            mPlayer = new MediaPlayer() ;
            try {
                AssetFileDescriptor afd = mContext.getAssets().openFd("surround/借过一下.mp3") ;
                mPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset() ,afd.getLength());
                mPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(!mPlayer.isPlaying()){

            mPlayer.start();
        }
    }

    /**
     * 是的达到 离开充电路径的终点
     * */
    private boolean isArrivedLeaveChargingPathEnd(String pointName){

        LogHelper.w(TAG, LogHelper.__TAG__() + ", pointName: " + pointName ) ;
//        showToastNotMainThread("pointName : " + pointName);
        showToast("pointName : " + pointName);

        if(pointName != null){

            if(NavigationHelper.POINT_WORK_NAME.equals(pointName)){ // 起始点

                if(mCurrTimerTask != null){ // 有定时任务

                    goToTimerTaskPath(mCurrTimerTask.getPathInfo().getName());
//                    NavigationHelper.goToTimerTaskPath(mMapInfo.getMapName(), mCurrTimerTask.getPathInfo().getName());

                }else{ // 前往充电点

                    mCurrTaskReason = NavigationConstant.TASK_CHARGING_0 ;
                    startChargingTaskPoint();// 无定时任务，开始充电任务
                }
                return true ;
            }else if(NavigationHelper.POINT_IN_AUTO_CHARGING_NAME.equals(pointName)){   // 充电辅助点

                // 前往充电点
                goToPoint(NavigationHelper.POINT_AUTO_CHARGING_NAME);
//                NavigationHelper.startPointTask(mMapInfo.getMapName(), NavigationHelper.POINT_AUTO_CHARGING_NAME);

                return true ;

            }else if(NavigationHelper.POINT_IN_DOOR.equals(pointName)){
                //进入充电桩的门
                mHandler.sendEmptyMessage(WHAT_DOOR_IN ) ;
                return true ;

            }else if(NavigationHelper.POINT_OUT_DOOR.equals(pointName)){
                //离开充电桩的门
                mHandler.sendEmptyMessage(WHAT_DOOR_OUT ) ;
                return true ;

            }else {

                if(mCurrTeamTask != null && mCurrTeamTask == mCurrTask){    //

                    if(pointName.equals(mCurrTeamTask.getTeamHelperPointName())){//到达任务起始点

                        if(mTeamTaskListener != null){
                            mTeamTaskListener.onTeamTaskStart(mCurrTeamTask.getTeamName());
                        }
                        // 开始正式收队
                        startTeamTaskTips(mCurrTeamTask.getTeamName());

                        goToTeamTaskPath(mCurrTeamTask.getTeamName(), mCurrTeamTask.getTeamPathName());
//                        NavigationHelper.goToTeamTaskPath(mMapInfo.getMapName(), mTeamTask.getTeamName(), mTeamTask.getTeamPathName());

                        return true ;

                    }else if(pointName.equals(mCurrTeamTask.getEndPointName())){//到达任务终点

                        // 延迟20秒结束
                        mHandler.postDelayed(mTeamTaskCompleteRunnable, 20*1000) ;

                        return true ;
                    }
                }
            }


        }

        return false ;
    }



    /**
     * 路径上面点的名称
     *
     * */
    private void onArrivedPoint(String graphName, String pointName){

        LogHelper.i(TAG, LogHelper.__TAG__() + ", graphName : " + graphName + ", pointName : " + pointName);

        String key = PathDetailInfoData.toKey(mSelectMapInfo.getMapName(), graphName, pointName) ;
        LogHelper.i(TAG, LogHelper.__TAG__() + ", key : " + key);

        PointActionInfo pointInfo = PathDetailInfoData.getInstance(mContext).queryPathDetailInfo(key) ;
        LogHelper.i(TAG, LogHelper.__TAG__() + ", pointInfo : " + GSON.toJson(pointInfo));

        startExecuteAction(pointInfo.getActions()) ;

    }


    private Runnable mTeamTaskCompleteRunnable = new Runnable() {
        @Override
        public void run() {

            onTeamTaskComplete() ;
        }
    };


    /**
     * 单个收队结束
     * */
    private void onTeamTaskComplete(){

        if(mTeamTaskListener != null){
            mTeamTaskListener.onTeamTaskComplete(mCurrTeamTask.getTeamName());
        }
        stopTeamTaskTips() ;

        nextTeamTask() ;
    }


    private static final int SURROUND_TIME = 1000 ; // 包围间隔时间
    private boolean isSurround = false ;      // 是否包围

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {

            if(isSurround){

                isSurround = false ;
                onDisSurround() ;
            }
        }
    };


    /**
     * 加载数据
     * 1、查询定时任务
     * 2、查询收队任务
     * */
    public void loadData() {
        LogHelper.i(TAG, LogHelper.__TAG__() );

        ArrayList<TimerTask> timerTasks = TaskInfoData.getInstance(mContext).query(mSelectMapInfo.getMapName()) ;
        if(timerTasks == null){

            timerTasks = new ArrayList<>() ;
        }
        this.mTimerTasks = timerTasks ;

        queryTeamTasks(mSelectMapInfo.getMapName()) ;

        startPreTimerTask();
    }

    /**
     * 更加地图查询队列数据
     * */
    private void queryTeamTasks(String mapName){

        this.mTeamTasks.clear();
        NavigationHelper.queryTeamTasks(mapName,mTeamTasks);
    }

    /***/
    public ArrayList<TeamTask> getTeamTasks() {
        return mTeamTasks;
    }


    /**离开充电桩监听*/
    private interface LeaveChargingPointListener {

        void onLeaveChargingPoint() ;
    }

    private LeaveChargingPointListener mLeaveChargingPointListener ;

    /**
     * 离开充电桩
     *
     * int nonChargeMinLevel, int chargeMinLevel,
     *
     * */
    private boolean leaveChargingPoint( LeaveChargingPointListener leaveChargingPointListener){

        this.mLeaveChargingPointListener = leaveChargingPointListener ;

        LogHelper.i(TAG, LogHelper.__TAG__() + ",charger : " + charger);
        switch (charger){
            case 3 : // 3：自动充电

                NavigationHelper.forward(0.2f, 0.3f, new NavigationHelper.MoveToListener() {
                    @Override
                    public void moveTo() {

                        if(charger == 0){    //

                            if(mLeaveChargingPointListener != null){
                                mLeaveChargingPointListener.onLeaveChargingPoint();
                                mLeaveChargingPointListener = null ;
                            }
                        }
                    }
                });

                return true ;
            case 4 : // 4：手动充电

                String text = "当前处于手动充电，请拔掉充电桩" ;
                showToast(text);

                break;
            default:

                if(mLeaveChargingPointListener != null){
                    mLeaveChargingPointListener.onLeaveChargingPoint();
                    mLeaveChargingPointListener = null ;
                }
                break;

        }

        return false ;
    }

    /**************************************定时任务（开始）**************************************/

    /**
     * 1、控制当前定时任务
     * @param isExecute true 开始任务; false 停止任务
     * */
    public void controlCurrTimerTask(boolean isExecute){

        LogHelper.e(TAG, LogHelper.__TAG__()  + ", isExecute : " + isExecute);

        if((mCurrTimerTask != null)){
            //
            if(isExecute){

                if(batteryLevel > mChargingTask.getBatters()[0]){//大于可充电的

                    mCurrTimerTask.setStop(false);
                    mCurrTaskReason = NavigationConstant.TASK_TIMER_3 ;
                    startTimerTaskPoint(); // 用户主动打开，开始定时任务

                }else{

                    showToast(String.format(Locale.CHINA,"当期电量%d,低于最低需求%d",batteryLevel,mChargingTask.getBatters()[0]));
                }

            }else {
                mCurrTimerTask.setStop(true);
                mCurrTaskReason = NavigationConstant.TASK_CHARGING_3 ;
                startChargingTaskPoint();// 用户主动关闭，开始充电任务
            }
        }
    }

    /**
     * 更新时间
     * */
    private void refreshTime(){

        Calendar calendar = Calendar.getInstance() ;
        hour = calendar.get(Calendar.HOUR_OF_DAY) ;
        minute = calendar.get(Calendar.MINUTE) ;

        if(mChargingTask != null){  // 有充电任务(更加时间 修改电量的临界值)

            if(TimerTask.equalTime(17, 30 ,hour,minute) <= 0){

                mChargingTask.setBatters(ChargingTask.BATTERY_BEFORE_17_30) ;

            }else {
                mChargingTask.setBatters(ChargingTask.BATTERY_AFTER_17_30) ;
            }
        }

        LogHelper.e(TAG, LogHelper.__TAG__()  + ", hour : " + hour  + ", minute : " + minute );
    }

    /**
     * 2、时间变化
     * */
    private void changeTime(){

        LogHelper.e(TAG, LogHelper.__TAG__() );

        refreshTime() ;

        startPreTimerTask();
    }

    /**
     *  3、电量变化
     * */
    public void batteryChange(int batteryLevel){

        LogHelper.e(TAG, LogHelper.__TAG__() + ", batteryLevel : " + batteryLevel );

        if(this.batteryLevel != batteryLevel){

            this.batteryLevel = batteryLevel ;
            startPreTimerTask();
        }
    }

    /***
     *  预处理定时任务
     * */
    private void startPreTimerTask(){

        LogHelper.e(TAG, LogHelper.__TAG__());

        // 当前执行收队任务，直接跳过
        if((mCurrTask != null && mCurrTask.getTaskType() == BaseTask.TaskType.TEAM)){

            return ;
        }

        // 没有定时任务，执行充电任务
        TimerTask timerTask = queryTaskInfo(hour, minute) ;
        boolean isChanged = changeTimerTask(timerTask) ;
        LogHelper.e(TAG, LogHelper.__TAG__() + ", timerTask : " + timerTask );
        if(timerTask == null){

            mCurrTaskReason = NavigationConstant.TASK_CHARGING_0 ;
            startChargingTaskPoint(); // 无定时任务，开始充电任务
            return ;
        }

        // 有定时任务
        // 如果当前是充电任务
        if(mCurrTask != null && mCurrTask.getTaskType() == BaseTask.TaskType.CHARGING){

            // 电量高于可执行任务的电量并且没有被暂停，开始执行正常任务
            if(!timerTask.isStop() && (batteryLevel > mChargingTask.getBatters()[1]) ){

                mCurrTaskReason = NavigationConstant.TASK_TIMER_1 ;
                startTimerTaskPoint();// 电量变化，开始定时任务
            }
            return ;
        }

        // 电量过低 执行充电任务
        if(batteryLevel < mChargingTask.getBatters()[0]){ // 电量低于最低的电量，开始执行充电任务

            mCurrTaskReason = NavigationConstant.TASK_CHARGING_1 ;
            startChargingTaskPoint(); //电量变化，开始充电任务
            return;
        }

        // 和之前一个不相同，开始下一个任务
        if(isChanged){

            mCurrTaskReason = NavigationConstant.TASK_TIMER_0 ;
            startTimerTaskPoint(); // 时间变化，开始定时任务
        }
    }

    /**
     * 前往开始任务起始点
     * */
    private void startTimerTaskPoint(){

        LogHelper.e(TAG, LogHelper.__TAG__());
        mCurrTask = mCurrTimerTask ;
        startCurrentTask();
    }


    /**
     * 切换定时任务任务
     * */
    private boolean changeTimerTask(TimerTask timerTask){

        if(mCurrTimerTask != timerTask){

            if(mCurrTimerTask != null){

                mCurrTimerTask.setStop(false);
            }
            mCurrTimerTask = timerTask ;

            return true ;
        }

        return false ;
    }

    /**************************************定时任务（结束）**************************************/

    /**************************************充电任务（开始）**************************************/

    /**
     * 前往充电点（辅助）
     * */
    private void startChargingTaskPoint(){

        LogHelper.i(TAG, LogHelper.__TAG__() );

        mCurrTask = mChargingTask ;
        startCurrentTask();
    }
    /**************************************充电任务（结束）**************************************/


    /**************************************收队任务（开始）**************************************/


    /**
     * 需要收队的任务列表
     * */
    private final ArrayList<String> mTeamNames = new ArrayList<>();
    public ArrayList<String> getTeamNames() {
        return mTeamNames;
    }

    public TeamTask getCurrTeamTask() {
        return mCurrTeamTask;
    }

    private TeamTaskListener mTeamTaskListener ;

    /**
     * 开始收队任务列表
     * @param isAppend true 表示追加
     *                  false 表示覆盖
     * */
    public void startTeamTasks(boolean isAppend, ArrayList<String> teamNames, TeamTaskListener teamTaskListener){

        mTeamTaskListener = teamTaskListener ;

        LogHelper.i(TAG, LogHelper.__TAG__() );
        if(teamNames == null){

            return ;
        }

        if(isAppend){ //增加到队列末尾

            addTeamNames(teamNames) ;

            if(mCurrTask != null && mCurrTask.getTaskType() == BaseTask.TaskType.TEAM){
                // 当前任务为收队任务
                // 添加完成就结束

            }else {

                nextTeamTask() ;
            }

        }else { // 使用新的队列

            mTeamNames.clear();
            addTeamNames(teamNames) ;

            nextTeamTask() ;
        }

    }


    /**
     * 增加队伍名，过滤掉相同名称的
     * */
    private void addTeamNames(ArrayList<String> teamNames){

        final int size = teamNames.size() ;
        for (int i = 0 ; i < size ; i ++){

            String teamName = teamNames.get(i) ;
            if(!mTeamNames.contains(teamName)){

                mTeamNames.add(teamName) ;
            }
        }
    }

    /**
     * 下一个任务
     * */
    private void nextTeamTask (){

        if(mTeamNames.isEmpty()){ // 没有任务任务

            mCurrTeamTask = null ;  // 当前收队任务结束

            if(mTeamTaskListener != null){
                mTeamTaskListener.onAllTeamTaskComplete();
            }
            // 全部任务结束
            if(mCurrTimerTask != null){ //如果当前定时任务不为空

                mCurrTaskReason = NavigationConstant.TASK_TIMER_2 ;
                startTimerTaskPoint(); // 收队任务结束，开始定时任务

            }else {

                mCurrTaskReason = NavigationConstant.TASK_CHARGING_2 ;
                startChargingTaskPoint();// 收队任务结束并且无定时任务，开始充电任务
            }

        }else{  //下一个队伍任务

            final String teamName = mTeamNames.remove(0) ;
            startTeamTask(teamName) ;
        }
    }

    /**
     * 开始收队任务
     * */
    private void startTeamTask(final String teamName){

        LogHelper.i(TAG, LogHelper.__TAG__() );

        TeamTask teamTask = queryTeamTask(teamName) ;
        changeTeamTask(teamTask) ;
        if(teamTask == null){

            String text = "查询不到《" + teamName + "》的收队任务！";
            showToast(text);

            if(mTeamTaskListener != null){
                mTeamTaskListener.onFail(teamName,TeamTaskListener.NO_SUCH_NAME_TEAM_TASK,text);
            }
            nextTeamTask();
            return;
        }

        if(charger == 3 || charger == 4){ // 充电任务

            if(batteryLevel > ChargingTask.TEAM_BATTERY_CHARGING){  //充电够了30%

                startCurrTeamTaskPoint();

            }else {

                String text = "当前电量"+batteryLevel+"%,低于充电时候所需的"+ ChargingTask.TEAM_BATTERY_CHARGING+"%";
                showToast(text);
                if(mTeamTaskListener != null){
                    mTeamTaskListener.onFail(teamName, TeamTaskListener.BATTERY_TO_LOW,text);
                }
                nextTeamTask();
            }


        }else {//没有充电

            if(batteryLevel > ChargingTask.TEAM_BATTERY_NO_CHARGING){

                startCurrTeamTaskPoint();

            }else{

                String text = "当前电量"+batteryLevel+"%,低于不充电时候所需的"+ ChargingTask.TEAM_BATTERY_NO_CHARGING+"%";
                if(mTeamTaskListener != null){
                    mTeamTaskListener.onFail(teamName, TeamTaskListener.BATTERY_TO_LOW,text);
                }
                nextTeamTask();
            }
        }

    }


    /**
     * 开始收队任务
     * */
    private void startCurrTeamTaskPoint(){

        LogHelper.i(TAG,LogHelper.__TAG__());

        mCurrTaskReason = NavigationConstant.TASK_TEAM_0 ;

        mCurrTask = mCurrTeamTask ;
        startCurrentTask();
    }


    /**
     * 取消收队任务
     * */
    public boolean cancelTeamTask(String teamName){

        if(mCurrTeamTask != null && mCurrTeamTask.getTeamName().equals(teamName)){

            onTeamTaskComplete();
            return true;
        }

        if(!mTeamNames.remove(teamName)){

            showToast("查询不到《" + teamName + "》的收队任务！");
            return false;
        }

        return true;
    }


    /**
     * 根据队伍名称查询队伍信息
     * */
    private TeamTask queryTeamTask(String teamName){

        if(teamName != null){

            final int size = mTeamTasks.size() ;
            for (int i = 0 ; i < size ; i ++){

                TeamTask teamTask = mTeamTasks.get(i) ;
                if(teamName.equals(teamTask.getTeamName())){

                    return teamTask ;
                }
            }
        }
        return null ;
    }

    /**切换任务*/
    private void changeTeamTask(TeamTask teamTask){

        mCurrTeamTask = teamTask ;
    }

    /**************************************收队任务（结束）**************************************/


    /**
     * 开始当期任务
     * */
    private void startCurrentTask(){

        if (mCurrTask != null){

            stopExecuteAction() ; // 取消其他的动作语料
            if(mCurrTask.getTaskType() == BaseTask.TaskType.TIMER){

                leaveChargingPoint(new LeaveChargingPointListener() {

                    @Override
                    public void onLeaveChargingPoint() {

                        if(isInWorkSpace){  // 在工作区内 直接去工作起始点

                            goToPointByMainLinePath(NavigationHelper.POINT_WORK_NAME) ;

                        }else { // 在工作区外

                            goToPointByMainLinePath(NavigationHelper.POINT_OUT_DOOR) ;
                        }
                    }
                });


            }else if(mCurrTask.getTaskType() == BaseTask.TaskType.CHARGING){

                if(isInWorkSpace){

                    goToPointByMainLinePath(NavigationHelper.POINT_IN_DOOR) ;

                }else {

                    // 开始充电任务
                    goToPointByMainLinePath(NavigationHelper.POINT_IN_AUTO_CHARGING_NAME) ;

                }

            }else if(mCurrTask.getTaskType() == BaseTask.TaskType.TEAM){

                leaveChargingPoint(new LeaveChargingPointListener() {

                    @Override
                    public void onLeaveChargingPoint() {

                        if(isInWorkSpace){  // 在工作区内 直接去工作起始点

                            if(mTeamTaskListener != null){
                                mTeamTaskListener.onStartTeamLine(mCurrTeamTask.getTeamHelperPointName());
                            }
                            goToPointByMainLinePath(mCurrTeamTask.getTeamHelperPointName()) ;

                        }else { // 在工作区外

                            if(mTeamTaskListener != null){
                                mTeamTaskListener.onStartTeamLine(NavigationHelper.POINT_OUT_DOOR);
                            }

                            goToPointByMainLinePath(NavigationHelper.POINT_OUT_DOOR) ;

                        }

                    }
                });
            }
        }
    }


    /**
     * 通过主辅道去某个点
     *
     * */
    private void goToPointByMainLinePath(String pointName){

        String text =  "开始通过《mainLine》路径前往《" + pointName + "》点" ;
        LogHelper.i(TAG, LogHelper.__TAG__() + ", text : " + text);
        mCurrTaskResult = text ;
        if(mOnTaskChangeListener != null){
            mOnTaskChangeListener.onCurrTaskInfoChange(getCurrTaskInfo());
        }

        NavigationHelper.goToNearbyPathCharging(mSelectMapInfo.getMapName(), pointName);
    }

    /**
     * 直接前往某个点
     * */
    private void goToPoint(String pointName){

        String text =  "开始自主导航前往《" + pointName + "》点" ;
        LogHelper.i(TAG, LogHelper.__TAG__() + ", text : " + text);
        mCurrTaskResult = text ;
        if(mOnTaskChangeListener != null){
            mOnTaskChangeListener.onCurrTaskInfoChange(getCurrTaskInfo());
        }

        NavigationHelper.startPointTask(mSelectMapInfo.getMapName(), pointName);
    }

    /**
     * 收队任务
     */
    private void goToTeamTaskPath(String teamName, String teamPathName){

        String text =  "开始收《" + teamName + "》队" ;
        LogHelper.i(TAG, LogHelper.__TAG__() + ", text : " + text);
        mCurrTaskResult = text ;
        if(mOnTaskChangeListener != null){
            mOnTaskChangeListener.onCurrTaskInfoChange(getCurrTaskInfo());
        }

        NavigationHelper.goToTeamTaskPath(mSelectMapInfo.getMapName(), teamName, teamPathName);
    }

    /**
     * 定时任务
     * */
    private void goToTimerTaskPath(String taskName){

        String text =  "开始定时《" + taskName + "》任务" ;
        LogHelper.i(TAG, LogHelper.__TAG__() + ", text : " + text);
        mCurrTaskResult = text ;
        if(mOnTaskChangeListener != null){
            mOnTaskChangeListener.onCurrTaskInfoChange(getCurrTaskInfo());
        }

        NavigationHelper.goToTimerTaskPath(mSelectMapInfo.getMapName(), taskName);
    }


    /**
     * 根据时间查找符合要求的任务
     * */
    private TimerTask queryTaskInfo(int hour, int minute){

        LogHelper.i(TAG, LogHelper.__TAG__() + ", hour : " + hour  + ", minute : " + minute  );

        final int size = mTimerTasks.size() ;
        for (int i = 0; i < size; i++) {

            TimerTask timerTask = mTimerTasks.get(i) ;

            if((TimerTask.equalTime(timerTask.getStartHour(), timerTask.getStartMinute(), hour, minute) <= 0) &&
                    (TimerTask.equalTime(timerTask.getEndHour(), timerTask.getEndMinute(), hour, minute) >= 0)){

                return timerTask ;
            }
        }

        return null ;
    }

    private static final Gson GSON = new Gson() ;

    /**
     *
     * */
    private void onFollowingPath(PathStatus pathStatus){

        if(mCurrTask != null && mCurrTask == mCurrTeamTask ){

            PathStatus.StatusData statusData = pathStatus.statusData ;
            if(statusData != null && statusData.graphName != null && statusData.nextPoint != null){

                if(statusData.graphName.equals(mCurrTeamTask.getTeamName()) && statusData.nextPoint.equals(mCurrTeamTask.getEndPointName())){

                    float percent = (1.0f - (statusData.remainingMilleage/statusData.totalMilleage) ) *100 ;

                    String text = "完成比率" + percent + "%" ;
                    LogHelper.e(TAG, LogHelper.__TAG__() + text);
                    showToast(text);

                    if(mTeamTaskListener != null){

                        mTeamTaskListener.onTeamTaskSchedule(mCurrTeamTask.getTeamName(), percent);
                    }

                }
            }
        }
    }


    private ExecuteActionThread mExecuteActionThread ;
    private void startExecuteAction(ArrayList<ActionData> actions){

        PlayAction playAction = null ;
        StopAction stopAction = null ;

        final int size = actions.size() ;
        for (int i = 0; i < size ; i++) {

            ActionData actionData = actions.get(i);
            BaseAction action = actionData.getAction();
            switch (action.getType()) {

                case PLAY:
                    playAction = (PlayAction) action;
                    break;

                case STOP:
                    stopAction = (StopAction) action;
                    break;
            }
        }

        if(playAction != null || stopAction != null){

            stopExecuteAction() ;

            mExecuteActionThread = new ExecuteActionThread(playAction, stopAction) ;
            mExecuteActionThread.start() ;
        }
    }

    private void stopExecuteAction(){

        if(mExecuteActionThread != null){

            mExecuteActionThread.stopRun();
            mExecuteActionThread = null ;
        }

        mHandler.removeCallbacks(mTeamTaskCompleteRunnable);


    }

    interface TurnToAngleListener{

        void turnToAngle() ;
    }

    private class ExecuteActionThread extends Thread{

        final PlayAction playAction ;
        final StopAction stopAction ;

        Timer timer;
        java.util.TimerTask task;

        private boolean isBasisPlay = false ;

        private ExecuteActionThread(PlayAction playAction, StopAction stopAction){

            this.playAction = playAction ;
            this.stopAction = stopAction ;
        }

        @Override
        public void run() {

            if(stopAction != null){

                pauseTask() ;
                isPauseTask = true ;

                final StopAction.Turn turn = stopAction.getTurn() ;
                final StopAction.End end = stopAction.getEnd() ;
                if(turn.getType() == StopAction.Turn.Type.TURN){    // 转动

                    TurnToAngleListener turnToAngleListener = new TurnToAngleListener(){
                        @Override
                        public void turnToAngle() {

                            endTurn(end) ;
                        }
                    };
                    turnToAngle(turn.getAngle(), turnToAngleListener);

                }else{ // 不转动

                    endTurn(end) ;
                }

            }

            if(playAction != null){

                switch (playAction.getMediaType()){

                    case WORD:
                        stopPlayVideo() ;
                        startSpeak() ;
                        break;

                    case VIDEO:
                        stopSpeak();


                        if(isMainThread()){

                            startPlayVideo() ;

                        }else{

                            mHandler.sendEmptyMessage(WHAT_START_PLAY_VIDEO);

                        }

                        break;

                }
            }
        }

        private void endTurn(StopAction.End end){

            switch (end.getType()){

                case PLAY:
                    isBasisPlay = true ;
                    break;

                case TIME:

                    timer = new Timer();
                    task = new java.util.TimerTask() {
                        @Override
                        public void run() {

                            isPauseTask = false ;
                            resumeTask() ;
                        }
                    } ;
                    timer.schedule(task, end.getMilliSecond());

                    break;
            }

        }

        private void startSpeak(){

            VoiceHelper.SpeakListener speakListener = new VoiceHelper.SpeakListener() {
                @Override
                public void onEnd() {
                    LogHelper.i(TAG, LogHelper.__TAG__() ) ;
                    if(isBasisPlay){

                        isPauseTask = false ;
                        resumeTask() ;
                    }
                }

                @Override
                public void onError() {
                    LogHelper.i(TAG, LogHelper.__TAG__()) ;
                }
            };
            VoiceHelper.getInstance(mContext).startSpeak(playAction.getName(), playAction.getTimes(), speakListener );
        }

        private void stopSpeak(){

            VoiceHelper.getInstance(mContext).stopSpeak();
        }

        private void startPlayVideo(){

            VideoHelper.PlayVideoListener playVideoListener = new VideoHelper.PlayVideoListener() {
                @Override
                public void onEnd() {
                    LogHelper.i(TAG, LogHelper.__TAG__() ) ;
                    if(isBasisPlay){

                        isPauseTask = false ;
                        resumeTask() ;
                    }
                }

                @Override
                public void onError() {
                    LogHelper.i(TAG, LogHelper.__TAG__()) ;
                }
            };
            VideoHelper.getInstance(mContext).startPlayVideo(playAction.getName(), playAction.getTimes(),playVideoListener) ;
        }

        private void stopPlayVideo(){

            VideoHelper.getInstance(mContext).stopPlayVideo() ;
        }

        /**
         * 转至到角度
         * */
        private void turnToAngle(final int angle,TurnToAngleListener turnToAngleListener){

            // 由于没有指定转至指定角度的接口，先查询当前的角度
            GsApi.getNowPosition(new ResponseListener<NowPosition>() {
                @Override
                public void success(NowPosition nowPosition) {

                    int currAngle = (int) nowPosition.angle;
                    if(currAngle < 0){

                        currAngle += 360 ;
                    }
                    int rotateAngle = angle - currAngle;

                    RotateTo rotateTo = new RotateTo() ;
                    rotateTo.rotateAngle = rotateAngle ;
                    rotateTo.rotateSpeed = 0.5f ;

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
                    },rotateTo);
                }

                @Override
                public void faild(String s, String s1) {

                }

                @Override
                public void error(Throwable throwable) {

                }
            });

            startListener(turnToAngleListener) ;
        }

        private Timer mTurnToAngleTimer ;
        private java.util.TimerTask mTurnToAngleTask ;
        private TurnToAngleListener mTurnToAngleListener ;

        private void startListener(TurnToAngleListener turnToAngleListener){
            mTurnToAngleListener = turnToAngleListener ;

            stopListener() ;

            mTurnToAngleTimer = new Timer() ;
            mTurnToAngleTask = new java.util.TimerTask() {
                @Override
                public void run() {

                    GsApi.isRotateFinishedListener(responseListener) ;
                }
            };
            mTurnToAngleTimer.schedule(mTurnToAngleTask, 1000);
        }
        private void stopListener(){

            if(mTurnToAngleTimer != null){

                mTurnToAngleTimer.cancel();
                mTurnToAngleTimer = null ;
            }

            if(mTurnToAngleTask != null){

                mTurnToAngleTask.cancel();
                mTurnToAngleTask = null ;
            }
        }

        private ResponseListener<Boolean> responseListener = new ResponseListener<Boolean>() {
            @Override
            public void success(Boolean aBoolean) {

                LogHelper.i(TAG, LogHelper.__TAG__() + ", aBoolean : " + aBoolean);
                if(aBoolean){

                    stopListener();

                    if(mTurnToAngleListener != null){

                        mTurnToAngleListener.turnToAngle();
                    }
                }
            }

            @Override
            public void faild(String s, String s1) {
            }

            @Override
            public void error(Throwable throwable) {
            }
        };

        void stopRun(){

            if(task != null){

                task.cancel() ;
                task = null ;
            }

            if(timer != null){

                timer.cancel();
                timer = null ;
            }

        }

    }

    /**
     * 任务暂停
     * */
    private boolean isPauseTask  = false;

    /**
     * 暂停任务
     * 有可能包围或者是任务暂停都能路程暂停
     * */
    private void pauseTask(){

        LogHelper.e(TAG, LogHelper.__TAG__()) ;

        TaskQueueApi.pauseTaskQueue(null) ;
    }

    /**
     * 恢复任务
     * (如果被包围)
     * */
    private void resumeTask(){

        LogHelper.e(TAG, LogHelper.__TAG__() + ", isSurround : " + isSurround  + ", isPauseTask : " + isPauseTask ) ;
        if(isSurround || isPauseTask){ //被包围或者任务暂停
            return;
        }

        TaskQueueApi.resumeTaskQueue(null) ;
    }
    /**

     * 开始收队提示
     * */
    private void startTeamTaskTips(String teamName){

        // 显示收队动作
        if(teamName != null){
            LogHelper.e(TAG, LogHelper.__TAG__() + ",teamName : " + teamName);
            int index = teamName.indexOf("_") ;
            LogHelper.e(TAG, LogHelper.__TAG__() + ",index : " + index);
            if(index > 0){

                String number = teamName.substring(0,index);
                LogHelper.e(TAG, LogHelper.__TAG__() + ",number : " + number);
                int num ;
                try{

                    num = Integer.parseInt(number) ;

                }catch (Exception e){
                    num = 0 ;
                }

                LogHelper.e(TAG, LogHelper.__TAG__() + ",num : " + num);
                if(num%2 == 0){

                    showAction("收队双号");
                }else {
                    showAction("收队单号");
                }
            }
        }


        CloseTeamHelper.CloseTeamListener closeTeamListener = null ;

        CloseTeamHelper.getInstance(mContext).startCloseTeam(teamName, closeTeamListener);
    }

    /**
     * 停止收队提示
     * */
    private void stopTeamTaskTips(){

        showAction("收队结束");
        CloseTeamHelper.getInstance(mContext).stopCloseTeam();
    }


    /**显示动作*/
    private void showAction(String name){

        LogHelper.e(TAG, LogHelper.__TAG__() + ",name : " + name);

//        String data = "{\"audioName\":\"" + name + "\",\"isOnlyAction\":true}" ;
//
//        Intent intent = new Intent() ;
//        intent.setPackage("com.yongyida.robot.multimodal") ;
//        intent.setAction("com.yongyida.robot.MULTIMODAL") ;
//        intent.putExtra("type", "CTRL.DANCE") ;
//        intent.putExtra("data" , data) ;
//
//        mContext.startService(intent) ;


        if("收队双号".equals(name)){

            int[] armLefts      = {2203, 2112, 2960, 1407, 2071, 0};
            int[] armRights     = {2071, 2079, 2076, 2036, 2055,0 } ;
            int[] fingerLefts   = {0, 0, 0, 0, 0} ;
            int[] fingerRights  = {0, 0, 0, 0, 0};

            initValues(armLefts, armRights, fingerLefts ,fingerRights ) ;

        }else if("收队单号".equals(name)){

            int[] armLefts      = {2060, 2112, 2048, 2072, 2068, 0};
            int[] armRights     = {2367, 1948, 3001, 1361, 2097, 0 } ;
            int[] fingerLefts   = {0, 0, 0, 0, 0} ;
            int[] fingerRights  = {0, 0, 0, 0, 0};

            initValues(armLefts, armRights, fingerLefts ,fingerRights ) ;

        }else {

            int[] armLefts      = {2035, 2112, 2060, 2060, 2069, 0};
            int[] armRights     = {2072, 2096, 2040, 2024, 2054, 0} ;
            int[] fingerLefts   = {0, 0, 0, 0, 0} ;
            int[] fingerRights  = {0, 0, 0, 0, 0};

            initValues(armLefts, armRights, fingerLefts ,fingerRights ) ;

        }


        SendClient.getInstance(mContext).send(null,handControl,null);
    }

    private HandControl handControl = new HandControl() ;
    public void initHandControl(){

        // 手
        handControl.setControl(true);
        handControl.setDirection(Direction.BOTH);

        int[] armLefts = {};
        int[] armRights = {} ;
        int[] fingerLefts = {} ;
        int[] fingerRights = {};

        initValues(armLefts, armRights, fingerLefts ,fingerRights ) ;


    }

    private void initValues(int[] armLefts, int[] armRights, int[] fingerLefts, int[] fingerRights ){

        //手臂
        ArmControl armControl = handControl.getArmControl() ;
        initArms(armControl.getArmLefts(), armLefts) ;
        initArms(armControl.getArmRights(),armRights) ;

        // 手指
        FingerControl fingerControl = handControl.getFingerControl() ;
        initFingers(fingerControl.getFingerLefts(),fingerLefts) ;
        initFingers(fingerControl.getFingerRights(),fingerRights) ;

    }


    private void initArms(ArrayList<SteeringControl> arms, int[] values){

        int size = arms.size() ;
        for (int i = 0 ; i < size ; i ++ ){

            initArm(arms.get(i),values[i]) ;
        }
    }

    private void initArm(SteeringControl arm, int value){

        arm.setMode(SteeringControl.Mode.DISTANCE_TIME);

        arm.getDistance().setType(SteeringControl.Distance.Type.TO);
        arm.getDistance().setUnit(SteeringControl.Distance.Unit.ORIGINAL);
//        arm.getDistance().setValue(2048);
        arm.getDistance().setValue(value);

        arm.getTime().setUnit(SteeringControl.Time.Unit.MILLI_SECOND);
        arm.getTime().setValue(3000);

        arm.setDelay(0);
    }

    private void initFingers(ArrayList<SteeringControl> fingers, int[] values ){

        int size = fingers.size() ;
        for (int i = 0 ; i < size ; i ++ ){

            initFinger(fingers.get(i), values[i]) ;
        }
    }

    private void initFinger(SteeringControl finger, int value){

        finger.setMode(SteeringControl.Mode.DISTANCE_TIME);

        finger.getDistance().setType(SteeringControl.Distance.Type.TO);
        finger.getDistance().setUnit(SteeringControl.Distance.Unit.PERCENT);
//        finger.getDistance().setValue(0);
        finger.getDistance().setValue(value);

        finger.getTime().setUnit(SteeringControl.Time.Unit.MILLI_SECOND);
        finger.getTime().setValue(3000);

        finger.setDelay(0);
    }


    /*******************************/
    /**
     * 到达doorIn点
     * */
    private void onDoorIn(){

        String doorIn = "door/奥丁回去充电.mp3" ;
        PlayerHelper.getInstance().startPlayAsset(mContext, doorIn);

        showWaitOpenDoorDialog(false) ;
    }

    private void onDoorOut(){

        String doorOut = "door/进场工作.mp3" ;
        PlayerHelper.getInstance().startPlayAsset(mContext, doorOut);

        showWaitOpenDoorDialog(true) ;
    }


    private AlertDialog mWaitOpenDoorDialog ;
    private boolean mNeedInWorkSpace ;  //设置一个将要的数据值
    private void showWaitOpenDoorDialog(boolean needInWorkSpace){

        mNeedInWorkSpace = needInWorkSpace ;

        if(mWaitOpenDoorDialog == null){

            View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_open_door, null) ;
            Button confirmBtn = view.findViewById(R.id.confirm_btn) ;
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PlayerHelper.getInstance().stopPlay();
                    TaskHandler.this.isInWorkSpace = mNeedInWorkSpace ;
                    startCurrentTask();
                    mWaitOpenDoorDialog.dismiss();
                }
            });

            mWaitOpenDoorDialog = new AlertDialog.Builder(mContext).create() ;
            mWaitOpenDoorDialog.setCancelable(false);
            mWaitOpenDoorDialog.setCanceledOnTouchOutside(false);
            mWaitOpenDoorDialog.setView(view);

            mWaitOpenDoorDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        }

        mWaitOpenDoorDialog.show();

    }

    private String mCurrTaskReason ;  //当前任务的信息
    private String mCurrTaskResult ;  //当前任务的信息
    public String getCurrTaskInfo(){

        return mCurrTaskReason + ";" + mCurrTaskResult ;
    }

    private OnTaskChangeListener mOnTaskChangeListener ;
    public void setOnTaskChangeListener(OnTaskChangeListener onTaskChangeListener) {
        this.mOnTaskChangeListener = onTaskChangeListener;
    }

    /**任务编号*/
    public interface OnTaskChangeListener{

        void onCurrTaskInfoChange(String currTaskInfo) ;

        void onTimerTaskChange(TimerTask timerTask) ;

    }

    private static final String DATA = "data" ;



    public boolean handleIntent(Intent intent){

        if(intent != null) {

            String json = intent.getStringExtra(DATA);

            TransferData transferData = null;
            try {
                transferData = GSON.fromJson(json, TransferData.class);
            } catch (Exception e) {
            }
            if (transferData != null) {

                switch (transferData.getType()) {

                    case Base.TYPE_REQUEST_ROBOT_INFO: // 请求机器信息

                        RequestRobotInfo requestRobotInfo = null;
                        try {
                            requestRobotInfo = GSON.fromJson(transferData.getJsonData(), RequestRobotInfo.class);

                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                        if (requestRobotInfo != null) {
                            MainServiceInfo.response(mContext, responseRobotInfo);    // 响应机器信息
                        }

                        break;
                    case Base.TYPE_REQUEST_CLOSE_TEAM: // 请求收队信息

                        RequestCloseTeam requestCloseTeam = null;
                        try {
                            requestCloseTeam = GSON.fromJson(transferData.getJsonData(), RequestCloseTeam.class);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (requestCloseTeam != null) {

                            ResponseCloseTeam responseCloseTeam = new ResponseCloseTeam();
                            responseCloseTeam.setInitMap(isInitMap);
                            if (isInitMap) {

                                TeamTaskListener teamTaskListener = new TeamTaskListener(){

                                    private ResponseCloseTeam responseCloseTeam = new ResponseCloseTeam();
                                    private ResponseCloseTeam.Fail fail = new ResponseCloseTeam.Fail() ;
                                    private ResponseCloseTeam.Schedule schedule = new ResponseCloseTeam.Schedule();
                                    {
                                        responseCloseTeam.setInitMap(true);
                                    }

                                    @Override
                                    public void onStartTeamLine(String teamName) {

                                        responseCloseTeam.setActionAndTeamName(ResponseCloseTeam.ACTION_GO_TO_START_POINT_BY_AUXILIARY_LINE, teamName);
                                        MainServiceInfo.response(mContext, responseCloseTeam);
                                    }

                                    @Override
                                    public void onTeamTaskStart(String teamName) {

                                        responseCloseTeam.setActionAndTeamName(ResponseCloseTeam.ACTION_START_CLOSE_TEAM, teamName);
                                        MainServiceInfo.response(mContext, responseCloseTeam);
                                    }

                                    private long last ;

                                    @Override
                                    public void onTeamTaskSchedule(String teamName, float percent) {

                                        long curr = System.currentTimeMillis() ;
                                        if(curr - last > 2000){ //增加间隔2秒内最多发送一次

                                            schedule.setPercent(percent);
                                            responseCloseTeam.closingTeam(teamName, schedule);
                                            MainServiceInfo.response(mContext, responseCloseTeam);

                                            last = curr ;
                                        }
                                    }

                                    @Override
                                    public void onTeamTaskComplete(String teamName) {

                                        responseCloseTeam.setActionAndTeamName(ResponseCloseTeam.ACTION_COMPLETE_CLOSE_TEAM, teamName);
                                        MainServiceInfo.response(mContext, responseCloseTeam);
                                    }

                                    @Override
                                    public void onFail(String teamName, int failCode, String failMessage) {

                                        fail.setFailCode(failCode);
                                        fail.setFailMessage(failMessage);
                                        responseCloseTeam.failCloseTeam(teamName, fail);
                                        MainServiceInfo.response(mContext, responseCloseTeam);
                                    }

                                    @Override
                                    public void onAllTeamTaskComplete() {

                                        responseCloseTeam.completeAllCloseTeam();
                                        MainServiceInfo.response(mContext, responseCloseTeam);
                                    }
                                } ;




                                startTeamTasks(true, requestCloseTeam.getTeamNames(),teamTaskListener);

                            } else {

                                MainServiceInfo.response(mContext, responseCloseTeam);
                            }
                        }

                        break;
                    case Base.TYPE_REQUEST_CANCEL_CLOSE_TEAM: // 请求取消收队

                        RequestCancelCloseTeam requestCancelCloseTeam = null;
                        try {
                            requestCancelCloseTeam = GSON.fromJson(transferData.getJsonData(), RequestCancelCloseTeam.class);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (requestCancelCloseTeam != null) {

                            ResponseCancelCloseTeam responseCancelCloseTeam = new ResponseCancelCloseTeam();
                            responseCancelCloseTeam.setInitMap(isInitMap);

                            if (isInitMap) { // 如果未初始化

                                String teamName = requestCancelCloseTeam.getTeamName();
                                responseCancelCloseTeam.setTeamName(teamName);
                                boolean isSuccessCancel = cancelTeamTask(teamName);
                                responseCancelCloseTeam.setSuccessCancel(isSuccessCancel);
                            }

                            MainServiceInfo.response(mContext, responseCancelCloseTeam);
                        }
                        break;
                    case Base.TYPE_REQUEST_CLOSE_TEAM_NAMES: // 请求收队信息

                        RequestCloseTeamNames requestCloseTeamNames = null;
                        try {
                            requestCloseTeamNames = GSON.fromJson(transferData.getJsonData(), RequestCloseTeamNames.class);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (requestCloseTeamNames != null) {

                            ResponseCloseTeamNames responseCloseTeamNames = new ResponseCloseTeamNames();
                            responseCloseTeamNames.setInitMap(isInitMap);

                            if (isInitMap) { // 地图已经初始化

                                TeamTask currTeamTask = getCurrTeamTask();
                                ArrayList<String> teamNames = getTeamNames();

                                String currTeamName = (currTeamTask == null) ? null : currTeamTask.getTeamName();

                                responseCloseTeamNames.setCurrTeamName(currTeamName);
                                responseCloseTeamNames.setTeamNames(teamNames);
                            }

                            MainServiceInfo.response(mContext, responseCloseTeamNames);

                        }

                        break;
                }
            }
        }

        return false ;
    }


}
