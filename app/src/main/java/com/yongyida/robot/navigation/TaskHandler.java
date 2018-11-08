package com.yongyida.robot.navigation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
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
import com.yongyida.robot.data.PathDetailInfoData;
import com.yongyida.robot.data.TaskInfoData;
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
import com.yongyida.robot.voice.CloseTeamHelper;
import com.yongyida.robot.voice.VideoHelper;
import com.yongyida.robot.voice.VoiceHelper;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
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

    private static TaskHandler mInstance ;
    public static TaskHandler getInstance(Context context){

        if(mInstance == null) {
            mInstance = new TaskHandler(context.getApplicationContext()) ;
        }
        return mInstance ;
    }

    private Context mContext ;

    private MapInfo mMapInfo ;

    // 当前任务
    private BaseTask mCurrTask ;

    // 定时任务
    private TimerTask mCurrTimerTask ;                                  // 当前定时任务
    private ArrayList<TimerTask> mTimerTasks ;                          // 全部的定时任务

    // 充电任务
    private ChargingTask mChargingTask = new ChargingTask();                                // 充电任务

    // 收队任务
    private TeamTask mTeamTask ;                                // 当前收队任务
    private ArrayList<TeamTask> mTeamTasks = new ArrayList<>(); // 全部的收队任务


    private int hour ;                                          // 当前小时
    private int minute ;                                        // 当前分钟
    private int charger = 0;                                    // 充电状态，0：未充电，3：自动充电，4：手动充电，5：自动充电下电池充满
    private int batteryLevel = 100 ;                            // 初始化高 否则机器一开始去充电
//    private boolean isChargingForward = false ;               // 是否尝试向前冲，离开充电桩


    private TaskHandler(Context context) {

        mContext = context;

        refreshTime() ;
        startListen() ;
    }

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

        showToastNotMainThread("机器人已经被包围了") ;

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



    /**
     * 是的达到 离开充电路径的终点
     * */
    private boolean isArrivedLeaveChargingPathEnd(String pointName){

        LogHelper.w(TAG, LogHelper.__TAG__()) ;
        showToastNotMainThread("pointName : " + pointName);

        if(pointName != null){

            if(NavigationHelper.POINT_WORK_NAME.equals(pointName)){ // 起始点

                if(mCurrTimerTask != null){ // 有定时任务

                    NavigationHelper.goToTimerTaskPath(mMapInfo.getMapName(), mCurrTimerTask.getPathInfo().getName());

                }else{ // 前往充电点

                    startChargingTaskPoint();
                }
                return true ;
            }else if(NavigationHelper.POINT_IN_AUTO_CHARGING_NAME.equals(pointName)){   // 充电辅助点

                // 前往充电点
                NavigationHelper.startPointTask(mMapInfo.getMapName(), NavigationHelper.POINT_AUTO_CHARGING_NAME);

                return true ;
            }else {

                if(mTeamTask != null && mTeamTask == mCurrTask){    //

                    if(pointName.equals(mTeamTask.getTeamHelperPointName())){//到达任务起始点

                        if(mTeamTaskListener != null){
                            mTeamTaskListener.onTeamTaskStart(mTeamTask.getTeamName());
                        }
                        // 开始正式收队
                        startTeamTaskTips(mTeamTask.getTeamName());

                        NavigationHelper.goToTeamTaskPath(mMapInfo.getMapName(), mTeamTask.getTeamName(), mTeamTask.getTeamPathName());

                        return true ;

                    }else if(pointName.equals(mTeamTask.getEndPointName())){//到达任务终点

                        // 延迟20秒结束
                        mHandler.postDelayed(mTeamTaskCompleteRunnable, 20*1000) ;

                        return true ;
                    }
                }
            }
        }

        return false ;
    }


    /**结束等待开门的*/
    private Runnable mFinishWaitDoorOpenRunnable = new Runnable() {
        @Override
        public void run() {

            resumeTask();
            CloseTeamHelper.getInstance(mContext).stopCloseTeam();
        }
    };

    /**
     * 路径上面点的名称
     *
     * */
    private void onArrivedPoint(String graphName, String pointName){

        LogHelper.i(TAG, LogHelper.__TAG__() + ", graphName : " + graphName + ", pointName : " + pointName);

        if(NavigationHelper.PATH_MAIN_LINE_NAME.equals(graphName)){

            if(NavigationHelper.POINT_IN_DOOR.equals(pointName)){   //进入充电桩的门

                pauseTask();
                CloseTeamHelper.getInstance(mContext).startCloseTeam(NavigationHelper.POINT_IN_DOOR, null);
                mHandler.postDelayed(mFinishWaitDoorOpenRunnable, 10*1000) ;


            }else if(NavigationHelper.POINT_OUT_DOOR.equals(pointName)){//离开充电桩的门

                pauseTask();
                CloseTeamHelper.getInstance(mContext).startCloseTeam(NavigationHelper.POINT_OUT_DOOR, null);
                mHandler.postDelayed(mFinishWaitDoorOpenRunnable, 10*1000) ;
            }

        }else {

            String key = PathDetailInfoData.toKey(mMapInfo.getMapName(), graphName, pointName) ;
            LogHelper.i(TAG, LogHelper.__TAG__() + ", key : " + key);

            PointActionInfo pointInfo = PathDetailInfoData.getInstance(mContext).queryPathDetailInfo(key) ;
            LogHelper.i(TAG, LogHelper.__TAG__() + ", pointInfo : " + GSON.toJson(pointInfo));

            startExecuteAction(pointInfo.getActions()) ;
        }

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
            mTeamTaskListener.onTeamTaskComplete(mTeamTask.getTeamName());
        }
        stopTeamTaskTips() ;

        nextTeamTask() ;
    }


    private static final int SURROUND_TIME = 1000 ; // 包围间隔时间
    private boolean isSurround = false ;      // 是否包围

    private static final int WHAT_TOAST = 0x01 ;    // toast信息
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case WHAT_TOAST:

                    String text = (String) msg.obj;
                    showToast(text) ;

                    break;
            }
        }
    };

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
     * 不在主线程显示toast 信息
     * */
    private void showToastNotMainThread(String text){

        Message message = mHandler.obtainMessage(WHAT_TOAST) ;
        message.obj = text ;
        mHandler.sendMessage(message) ;
    }

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

    /**
     * 初始化
     * */
    public void init(MapInfo mapInfo) {
        LogHelper.i(TAG, LogHelper.__TAG__() );

        this.mMapInfo = mapInfo ;
        ArrayList<TimerTask> timerTasks = TaskInfoData.getInstance(mContext).query(mapInfo.getMapName()) ;
        if(timerTasks == null){

            timerTasks = new ArrayList<>() ;
        }
        this.mTimerTasks = timerTasks ;

        queryTeamTasks(mapInfo.getMapName()) ;

        startPreTimerTask();
    }

    public void queryTeamTasks(String mapName){

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
     * */
    private boolean leaveChargingPoint(LeaveChargingPointListener leaveChargingPointListener){

        this.mLeaveChargingPointListener = leaveChargingPointListener ;

        switch (charger){
            case 0: // 0：未充电

                if(mLeaveChargingPointListener != null){
                    mLeaveChargingPointListener.onLeaveChargingPoint();
                    mLeaveChargingPointListener = null ;
                }
                return true ;
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

                showToastNotMainThread("当前处于手动充电，请拔掉充电桩");

                break;
            case 5 : // 5：自动充电下电池充满

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

                mCurrTimerTask.setStop(false);
                startTimerTaskPoint();

            }else {

                mCurrTimerTask.setStop(true);
                startChargingTaskPoint();
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
    public void changeTime(){

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

            startChargingTaskPoint();
            return ;
        }

        // 有定时任务
        // 如果当前是充电任务
        if(mCurrTask != null && mCurrTask.getTaskType() == BaseTask.TaskType.CHARGING){

            // 电量高于可执行任务的电量并且没有被暂停，开始执行正常任务
            if(!timerTask.isStop() && (batteryLevel > mChargingTask.getBatters()[1]) ){

                startTimerTaskPoint();
            }
            return ;
        }

        // 电量过低 执行充电任务
        if(batteryLevel < mChargingTask.getBatters()[0]){ // 电量低于最低的电量，开始执行充电任务

            startChargingTaskPoint();
            return;
        }

        // 和之前一个不相同，开始下一个任务
        if(isChanged){

            startTimerTaskPoint();
        }
    }

    /**
     * 前往开始任务起始点
     * */
    private void startTimerTaskPoint(){

        LogHelper.e(TAG, LogHelper.__TAG__());

        leaveChargingPoint(new LeaveChargingPointListener() {
            @Override
            public void onLeaveChargingPoint() {

                mCurrTask = mCurrTimerTask ;
                stopExecuteAction() ; // 取消其他的动作语料

                NavigationHelper.goToNearbyPathCharging(mMapInfo.getMapName(), NavigationHelper.POINT_WORK_NAME);
            }
        });
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

//        if((mCurrTask != mChargingTask)){
//
//            mCurrTask = mChargingTask ;
//            stopExecuteAction() ; //    取消其他的动作语料
//
//            // 开始充电任务
//            NavigationHelper.goToNearbyPathCharging(mMapInfo.getMapName(), NavigationHelper.POINT_IN_AUTO_CHARGING_NAME);
//        }
    }

    /**************************************充电任务（结束）**************************************/


    /**************************************收队任务（开始）**************************************/

    /**
     * 需要收队的任务列表
     * */
    private ArrayList<String> mTeamNames ;
    private TeamTaskListener mTeamTaskListener ;

    /**
     * 开始收队任务列表
     * */
    public void startTeamTasks(ArrayList<String> teamNames, TeamTaskListener teamTaskListener){

        LogHelper.i(TAG, LogHelper.__TAG__() );

        mTeamNames = teamNames ;
        mTeamTaskListener = teamTaskListener ;

        leaveChargingPoint(new LeaveChargingPointListener() {
            @Override
            public void onLeaveChargingPoint() {

                nextTeamTask() ;
            }
        });
    }

    /**
     * 下一个任务
     * */
    private void nextTeamTask (){

        if(mTeamNames == null || mTeamNames.isEmpty()){ // 没有任务任务

            mCurrTask = null ;

            if(mTeamTaskListener != null){
                mTeamTaskListener.onAllTeamTaskComplete();
            }
            // 全部任务结束
            if(mCurrTimerTask != null){ //如果当前定时任务不为空

                startTimerTaskPoint(); // 开始定时任务
            }else {

                startChargingTaskPoint();// 充电任务
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

                leaveChargingPoint(new LeaveChargingPointListener() {
                    @Override
                    public void onLeaveChargingPoint() {

                        startCurrTeamTaskPoint();
                    }
                });

            }else {

                String text = "当前电量"+batteryLevel+"%,低于充电时候所需的"+ ChargingTask.TEAM_BATTERY_CHARGING+"%";
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

        mCurrTask = mTeamTask ;
        stopExecuteAction() ; //    取消其他的动作语料

        if(mTeamTaskListener != null){
            mTeamTaskListener.onStartTeamLine(mTeamTask.getTeamHelperPointName());
        }

        NavigationHelper.goToNearbyPathCharging(mMapInfo.getMapName(), mTeamTask.getTeamHelperPointName());
    }


    /**
     * 取消收队任务
     * */
    public boolean cancelTeamTask(String teamName){

        if(mTeamTask != null && mTeamTask.getTeamName().equals(teamName)){

            onTeamTaskComplete();
            return true;
        }

        TeamTask teamTask = queryTeamTask(teamName) ;
        if(teamTask == null){

            showToast("查询不到《" + teamName + "》的收队任务！");
            return false;
        }

        mTeamTasks.remove(teamTask) ;
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

        mTeamTask = teamTask ;
    }

    /**************************************收队任务（结束）**************************************/


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

        if(mCurrTask != null && mCurrTask == mTeamTask ){

            PathStatus.StatusData statusData = pathStatus.statusData ;
            if(statusData != null && statusData.graphName != null && statusData.nextPoint != null){

                if(statusData.graphName.equals(mTeamTask.getTeamName()) && statusData.nextPoint.equals(mTeamTask.getEndPointName())){

                    float percent = (1.0f - (statusData.remainingMilleage/statusData.totalMilleage) ) *100 ;

                    String text = "完成比率" + percent + "%" ;
                    LogHelper.e(TAG, LogHelper.__TAG__() + text);
                    showToastNotMainThread(text);

                    if(mTeamTaskListener != null){

                        mTeamTaskListener.onTeamTaskSchedule(mTeamTask.getTeamName(), percent);
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

//        String path = "voice/fangfang/收队.mp3"  ;
//        int imageId = R.drawable.close_team_5 ;
        CloseTeamHelper.CloseTeamListener closeTeamListener = null ;

        CloseTeamHelper.getInstance(mContext).startCloseTeam(teamName, closeTeamListener);
    }

    /**
     * 停止收队提示
     * */
    private void stopTeamTaskTips(){

        CloseTeamHelper.getInstance(mContext).stopCloseTeam();
    }
}
