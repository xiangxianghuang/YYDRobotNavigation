package com.yongyida.robot.navigation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gs.gsnavlibrary.api.GsApi;
import com.gs.gsnavlibrary.api.GsNav;
import com.gs.gsnavlibrary.api.MapApi;
import com.gs.gsnavlibrary.api.TaskQueueApi;
import com.gs.gsnavlibrary.base.GsNavStatus;
import com.gs.gsnavlibrary.bean.common.NowPosition;
import com.gs.gsnavlibrary.bean.common.RotateTo;
import com.gs.gsnavlibrary.bean.taskqueue.TaskQueue;
import com.gs.gsnavlibrary.listener.ConnectListener;
import com.gs.gsnavlibrary.listener.GsStatusListener;
import com.gs.gsnavlibrary.listener.ResponseListener;
import com.yongyida.robot.data.PathDetailInfoData;
import com.yongyida.robot.data.TaskInfoData;
import com.yongyida.robot.navigation.bean.MapInfo;
import com.yongyida.robot.navigation.bean.PointActionInfo;
import com.yongyida.robot.navigation.bean.PathStatus;
import com.yongyida.robot.navigation.bean.TaskInfo;
import com.yongyida.robot.navigation.bean.path.ActionData;
import com.yongyida.robot.navigation.bean.path.BaseAction;
import com.yongyida.robot.navigation.bean.path.PlayAction;
import com.yongyida.robot.navigation.bean.path.StopAction;
import com.yongyida.robot.util.LogHelper;
import com.yongyida.robot.voice.VideoHelper;
import com.yongyida.robot.voice.VoiceHelper;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Create By HuangXiangXiang 2018/8/31
 * 任务优先级：数字越小，优先级越高
 *
 * 定时任务（优先级3）
 *      触发条件
 *       1、用户主动触发（在规定时间内的任务主动开关）
 *       2、时间变化
 *       3、电量变化
 *
 *  充电任务（优先级2）
 *      触发条件
 *      1、用户主动触发
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
public class TaskHandle {

    private static final String TAG = TaskHandle.class.getSimpleName() ;

    /**
     * 17.30 之前 低于20的电量自动充电， 超过90的电量自动开始任务
     */
    private static final int[] BATTERY_BEFORE_17_30 = { 20, 90 } ;

    /**
     * 17.30 之后 低于20的电量自动充电， 超过90的电量自动开始任务
     */
    private static final int[] BATTERY_AFTER_17_30 = { 20, 70 } ;

    private static TaskHandle mInstance ;

    public static TaskHandle getInstance(Context context){

        if(mInstance == null) {
            mInstance = new TaskHandle(context.getApplicationContext()) ;
        }
        return mInstance ;
    }

    private Context mContext ;

    private MapInfo mMapInfo ;

    private ArrayList<TaskInfo> mTaskInfos ;

    private TaskInfo mCurrTaskInfo ;

    private boolean isStartChargeTask = false ;  //是否充电任务 false 表示执行普通任务， true 表示 执行充电任务
    private boolean isAutoCharging = false;    // 自动充电
    private int hour ;
    private int minute ;
    private int batteryLevel = 100 ; // 初始化高 一开始去充电

    private int[] batters = BATTERY_BEFORE_17_30;

    private TaskHandle(Context context) {

        mContext = context;

        refreshTime() ;

        startListen() ;
//        listenTaskStatus() ;
    }


    public TaskInfo getCurrTaskInfo() {

        return mCurrTaskInfo;
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
//                        String statusMsg = jsonObject.getString("statusMsg") ;

                        switch (statusCode){
                            case 407:

                                JSONObject statusData = jsonObject.getJSONObject("statusData") ;
                                String name = statusData.getString("name") ;
                                if(name != null && name.equals(exitChargingPointName)){

                                    startCurrTask();
                                }
                                break;

                            case 409:   // 表示到达充电点任务

                                statusData = jsonObject.getJSONObject("statusData") ;
                                name = statusData.getString("name") ;
                                if(name != null && name.equals(exitChargingPointName)){

                                    startCurrTask();
                                }
                            break;

                            case 302:

                                PathStatus pathStatus = GSON.fromJson(s, PathStatus.class) ;
                                LogHelper.e(TAG, LogHelper.__TAG__() + ", pathStatus : " + pathStatus);

                                if(pathStatus != null && pathStatus.statusData != null){

                                    ArrayList<String> passedPointsList = pathStatus.statusData.getPassedPointsList() ;

                                    if(!passedPointsList.isEmpty()){
                                        String pointName = passedPointsList.get(0) ;
                                        if(pointName != null && pointName.equals(exitChargingPointName)){

                                            startCurrTask();
                                            return;
                                        }

                                        onArrivedPoint(pointName);
                                    }
                                }

                                break;

                            case 1110:

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
                        isAutoCharging =(charger == 3);

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

            onSurround() ;
        }
        isSurround = true ;

//        showToast("机器人已经被包围了") ;
        mHandler.sendEmptyMessage(0);

        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, SURROUND_TIME) ;
    }

    /**通知被包围*/
    private void onSurround(){

        // 暂停任务
        pauseTask();
    }

    /**通知接触包围*/
    private void onDisSurround(){

        // 继续任务
        resumeTask();
    }


    private static final int SURROUND_TIME = 1000 ;
    private boolean isSurround = false ;      // 是否包围

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
            showToast("机器人已经被包围了") ;
        }
    };
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {

            if(isSurround){

                onDisSurround() ;
            }

            isSurround = false ;

        }
    };



    private Toast mToast ;
    private void showToast(String text){

        if(mToast == null){

            mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT) ;
            mToast.show();

        }else {

            mToast.setText(text);
            mToast.show();
        }

    }


    /**初始化*/
    public void init(MapInfo mapInfo) {
        LogHelper.i(TAG, LogHelper.__TAG__() );

        this.mMapInfo = mapInfo ;
        ArrayList<TaskInfo> taskInfos = TaskInfoData.getInstance(mContext).query(mapInfo.getMapName()) ;
        if(taskInfos == null){

            taskInfos = new ArrayList<>() ;
        }
        this.mTaskInfos = taskInfos ;


        startTask();
    }

    /**
     * 1、用户手动控制
     * */
    public void controlTask(boolean isRun){

        LogHelper.i(TAG, LogHelper.__TAG__()  + ", isRun : " + isRun);

        if(mCurrTaskInfo != null){
            //
            if(isRun){

                startCurrTaskJudgeIfCharging();

            }else {
                startChargingTask();
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

        if(TaskInfo.equalTime(17, 30 ,hour,minute) <= 0){

            batters = BATTERY_BEFORE_17_30 ;

        }else {
            batters = BATTERY_AFTER_17_30 ;
        }

        LogHelper.i(TAG, LogHelper.__TAG__()  + ", hour : " + hour  + ", minute : " + minute );
    }


    /**
     * 2、时间变化
     * */
    public void changeTime(){

        LogHelper.i(TAG, LogHelper.__TAG__() );

        refreshTime() ;

        startTask();
    }

    /**
     *  3、电量变化
     * */
    public void batteryChange(int batteryLevel){

        LogHelper.i(TAG, LogHelper.__TAG__() + ", batteryLevel : " + batteryLevel );

        if(this.batteryLevel != batteryLevel){

            this.batteryLevel = batteryLevel ;
            startTask();
        }

    }

    private void startTask(){

        TaskInfo taskInfo = queryTaskInfo(hour, minute) ;
        LogHelper.i(TAG, LogHelper.__TAG__() + ", taskInfo : " + taskInfo );
        if(taskInfo == null){   // 没有任务 执行充电任务

            mCurrTaskInfo = null ;
            startChargingTask();
            return ;
        }

        if(mCurrTaskInfo == taskInfo){  // 有任务，并且与当前任务相同

            if(batteryLevel <= batters[0]){ // 低于最低电量执行充电任务

                startChargingTask();

            } else if(batteryLevel <= batters[1]){
                // 不做任何操作

            }else { // 高于最高电脑

                if(isStartChargeTask){
                    startCurrTaskJudgeIfCharging();
                }
            }

        }else{//  有任务，并且与当前任务不相同 , 如果在电量满足任务时候就开始任务

            mCurrTaskInfo = taskInfo ;

            if(batteryLevel <= batters[0]){ // 低于最低电量执行充电任务

                startChargingTask();

//            } else if(batteryLevel <= batters[1]){
//
//                if(!isStartChargeTask){ //
//
//                    startCurrTaskJudgeIfCharging();
//                }

            }else { // 高于最高电量

                startCurrTaskJudgeIfCharging();
            }

        }

    }

    /**
     * 根据时间查找符合要求的任务
     * */
    private TaskInfo queryTaskInfo(int hour, int minute){

        LogHelper.i(TAG, LogHelper.__TAG__() + ", hour : " + hour  + ", minute : " + minute  );

        final int size = mTaskInfos.size() ;
        for (int i = 0; i < size; i++) {

            TaskInfo taskInfo = mTaskInfos.get(i) ;

            if((TaskInfo.equalTime(taskInfo.getStartHour(), taskInfo.getStartMinute(), hour, minute) <= 0) &&
                    (TaskInfo.equalTime(taskInfo.getEndHour(), taskInfo.getEndMinute(), hour, minute) >= 0)){

                return taskInfo ;
            }
        }

        return null ;
    }

    private static final String CHARGING_PATH_NAME    = "zdhc";         // 充电路径
    private static final String CHARGING_POINT_NAME   = "zdhcd";        // 充电点

    private String chargingPathName     = CHARGING_PATH_NAME ;
    private String chargingPointName    = CHARGING_POINT_NAME ;

    /**
     *
     * 充电任务
     * */
    private void startChargingTask(){

        LogHelper.i(TAG, LogHelper.__TAG__() + ", isStartChargeTask : " + isStartChargeTask );

        if(!isStartChargeTask){
            isStartChargeTask = true ;

            stopExecuteAction() ; //    取消其他的动作语料

            // 开始充电任务
            NavigationHelper.goToNearbyPathCharging(mMapInfo.getMapName(), chargingPathName ,chargingPointName);
        }
    }


    private static final String EXIT_CHARGING_PATH_NAME      = "fzdhc";      // 充电路径(反自动回充)
    private static final String EXIT_CHARGING_POINT_NAME     = "fzdhcd";     // 充电点(反自动回充点)

    private String exitChargingPathName     = EXIT_CHARGING_PATH_NAME ;
    private String exitChargingPointName    = EXIT_CHARGING_POINT_NAME ;

    /**
     * 开始当前任务 并且判断是否处于充电状态
     * 如果属于充电状态
     *          先执行退出充电线路
     * 如果不属于充电状态则直接开始任务
     *
     * */
    private void startCurrTaskJudgeIfCharging(){

        LogHelper.i(TAG, LogHelper.__TAG__() + ", isStartChargeTask : " + isStartChargeTask );

        if(isStartChargeTask){

            if(isAutoCharging){

                forward(0.2F, 0.3F, new MoveToListener() {
                    @Override
                    public void moveTo() {

                        isStartChargeTask = false ;
                        NavigationHelper.goToNearbyPathCharging(mMapInfo.getMapName(), exitChargingPathName ,exitChargingPointName);
                    }
                });


            }else{

                isStartChargeTask = false ;
                NavigationHelper.goToNearbyPathCharging(mMapInfo.getMapName(), exitChargingPathName ,exitChargingPointName);
            }
        }else {

            startCurrTask() ;
        }
    }

    interface MoveToListener{

        void moveTo() ;
    }


    /**
     * 以0.3的速度 向前进0.2
     * */
    private void forward(float distance, float speed, MoveToListener moveToListener){

        LogHelper.i(TAG, LogHelper.__TAG__() + ", distance : " + distance + ", speed : " + speed );

        mMoveToListener = moveToListener ;

        GsApi.moveTo(null,distance,speed ) ;
        startListener() ;
    }

    private Timer mTurnToTimer ;
    private TimerTask mTurnToTask ;
    private MoveToListener mMoveToListener ;
    private ResponseListener<Boolean> mIsMoveToFinishedListener = new ResponseListener<Boolean>() {
        @Override
        public void success(Boolean aBoolean) {

            if (aBoolean) {

                if(mMoveToListener != null){

                    mMoveToListener.moveTo();
                }
            }

        }

        @Override
        public void faild(String s, String s1) {

        }

        @Override
        public void error(Throwable throwable) {

        }
    } ;

    private void startListener(){

        stopListener() ;

        mTurnToTimer = new Timer() ;
        mTurnToTask = new TimerTask() {
            @Override
            public void run() {

                GsApi.isMoveToFinishedListener(mIsMoveToFinishedListener) ;
            }
        };
        mTurnToTimer.schedule(mTurnToTask, 1000);
    }
    private void stopListener(){

        if(mTurnToTimer != null){

            mTurnToTimer.cancel();
            mTurnToTimer = null ;
        }

        if(mTurnToTask != null){

            mTurnToTask.cancel();
            mTurnToTask = null ;
        }
    }


    /**
     * 开始当前任务
     * */
    private void startCurrTask(){

        final String mapName = mCurrTaskInfo.getPathInfo().getMapName() ;
        final String pathName = mCurrTaskInfo.getPathInfo().getName() ;

        LogHelper.i(TAG, LogHelper.__TAG__() + ", taskName : " + pathName);
        ResponseListener<String> responseListener = new ResponseListener<String>() {
            @Override
            public void success(String s) {

                LogHelper.i(TAG, LogHelper.__TAG__() + ", s : " + s);
            }

            @Override
            public void faild(String s, String s1) {

                LogHelper.i(TAG, LogHelper.__TAG__() + ", s : " + s + ", s1 : " + s1);
            }

            @Override
            public void error(Throwable throwable) {

                LogHelper.i(TAG, LogHelper.__TAG__() + ", throwable : " + throwable.getMessage());
            }
        };

        TaskQueue taskQueue = new TaskQueue() ;
        taskQueue.map_name = mapName ;
        taskQueue.name = pathName ;
        taskQueue.loop = true ;
        taskQueue.loop_count = Integer.MAX_VALUE ;

        TaskQueueApi.startTaskQueue(responseListener, taskQueue) ;
    }


    private static final Gson GSON = new Gson() ;

    /**
     * 点的名称
     * */
    private void onArrivedPoint(String pointName){

        LogHelper.i(TAG, LogHelper.__TAG__() + ", pointName : " + pointName);

        PointActionInfo pointInfo = PathDetailInfoData.getInstance(mContext).queryPathDetailInfo(pointName) ;
        LogHelper.i(TAG, LogHelper.__TAG__() + ", pointInfo : " + GSON.toJson(pointInfo));

        startExecuteAction(pointInfo.getActions()) ;
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

    }

    public boolean isRun() {

        return !isStartChargeTask;
    }

    interface TurnToAngleListener{

        void turnToAngle() ;
    }


    private class ExecuteActionThread extends Thread{

        final PlayAction playAction ;
        final StopAction stopAction ;

        Timer timer;
        TimerTask task;

        private boolean isBasisPlay = false ;

        private ExecuteActionThread(PlayAction playAction, StopAction stopAction){

            this.playAction = playAction ;
            this.stopAction = stopAction ;
        }

        @Override
        public void run() {

            if(stopAction != null){

                pauseTask() ;

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
                        startPlayVideo() ;
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
                    task = new TimerTask() {
                        @Override
                        public void run() {

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
            if(isStartChargeTask){

                return;
            }

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
        private TimerTask mTurnToAngleTask ;
        private TurnToAngleListener mTurnToAngleListener ;

        private void startListener(TurnToAngleListener turnToAngleListener){
            mTurnToAngleListener = turnToAngleListener ;

            stopListener() ;

            mTurnToAngleTimer = new Timer() ;
            mTurnToAngleTask = new TimerTask() {
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



        public void stopRun(){

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


    //暂停任务
    private void pauseTask(){

        if(isStartChargeTask){

            return;
        }
        LogHelper.i(TAG, LogHelper.__TAG__()) ;
        TaskQueueApi.pauseTaskQueue(null) ;
    }

    /**恢复任务*/
    private void resumeTask(){
        if(isStartChargeTask){

            return;
        }
        LogHelper.i(TAG, LogHelper.__TAG__()) ;
        TaskQueueApi.resumeTaskQueue(null) ;
    }


    /**
     * 收队任务
     * @param teamName 队伍名称
     *
     * */
    private void collectTeam(String teamName){



    }

}
