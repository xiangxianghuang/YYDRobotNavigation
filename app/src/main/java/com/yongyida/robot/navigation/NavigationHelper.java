package com.yongyida.robot.navigation;

import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.gs.gsnavlibrary.api.GsApi;
import com.gs.gsnavlibrary.api.GsNav;
import com.gs.gsnavlibrary.api.MapApi;
import com.gs.gsnavlibrary.api.PositionApi;
import com.gs.gsnavlibrary.api.RecordPathApi;
import com.gs.gsnavlibrary.api.SketchGraphApi;
import com.gs.gsnavlibrary.api.TaskQueueApi;
import com.gs.gsnavlibrary.bean.position.Position;
import com.gs.gsnavlibrary.bean.recordpath.RecordPath;
import com.gs.gsnavlibrary.bean.sketchpath.SketchGraph;
import com.gs.gsnavlibrary.bean.sketchpath.SketchLine;
import com.gs.gsnavlibrary.bean.taskqueue.Task;
import com.gs.gsnavlibrary.bean.taskqueue.TaskQueue;
import com.gs.gsnavlibrary.listener.ResponseListener;
import com.yongyida.robot.navigation.bean.PathInfo;
import com.yongyida.robot.navigation.bean.TeamTask;
import com.yongyida.robot.util.LogHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Create By HuangXiangXiang 2018/8/24
 * 导航帮助类
 *
 *
 *
 */
public class NavigationHelper {

    private static final String TAG = NavigationHelper.class.getSimpleName() ;


    /**
     * 辅助收队路径
     * */
    public final static String PATH_MAIN_LINE_NAME                      = "mainLine" ;

    /**
     * 充电点(真实)
     * */
    public static final String POINT_AUTO_CHARGING_NAME                 = "autoChargingPoint";
    /**
     * 充电点(辅助)
     * */
    public static final String POINT_IN_AUTO_CHARGING_NAME              = "inAutoChargingPoint";
    /**
     * 工作点
     * */
    public static final String POINT_WORK_NAME                          = "work";

    /**
     * 进入充电桩的门
     * */
    public static final String POINT_IN_DOOR                            = "inDoor";
    /**
     * 离开充电桩的门
     * */
    public static final String POINT_OUT_DOOR                           = "outDoor";


    public interface OnResponseAllPathAndPointListener{

        void onSuccess(ArrayList<PathInfo> pathInfos) ;

        void onFail(String message) ;

        void onError(String message) ;
    }

    //3、路径组合
    private static void requestTaskQueueList2( final String mapName,
                                             final OnResponseAllPathAndPointListener onGetAllPathAndPointListener){

        TaskQueueApi.requestTaskQueueList(new ResponseListener<List<TaskQueue>>() {
            @Override
            public void success(List<TaskQueue> taskQueues) {

                ArrayList<PathInfo> pathInfos = new ArrayList<>() ;

                final int size = taskQueues.size() ;
                for (int i = 0; i < size ; i++) {

                    TaskQueue taskQueue = taskQueues.get(i) ;
                    LogHelper.i(TAG, LogHelper.__TAG__() + ", name : " + taskQueue.name);

                    PathInfo pathInfo = new PathInfo(taskQueue.name, PathInfo.Type.TASK_QUEUE) ;
                    pathInfo.setMapName(taskQueue.map_name);
                    pathInfos.add(pathInfo) ;
                }

                onGetAllPathAndPointListener.onSuccess(pathInfos);
            }

            @Override
            public void faild(String s, String s1) {

                LogHelper.w(TAG, LogHelper.__TAG__() + ", s : " + s + ", s1 : " + s1);
                onGetAllPathAndPointListener.onFail("task_queue");
            }

            @Override
            public void error(Throwable throwable) {

                LogHelper.e(TAG, LogHelper.__TAG__() + ", throwable : " + throwable.getMessage());
                onGetAllPathAndPointListener.onError("task_queue");
            }
        },mapName);
    }


    /**
     * 获取全部路径和标记的点
     * */
    public static void requestAllPathAndPoint(String mapName,@NonNull OnResponseAllPathAndPointListener onResponseAllPathAndPointListener ){

//        getTemp(mapName,onResponseAllPathAndPointListener) ;

//        ArrayList<PathInfo> pathInfos = new ArrayList<>() ;
//        requestRecordPathData(pathInfos, mapName , onResponseAllPathAndPointListener) ;


        requestTaskQueueList2(mapName , onResponseAllPathAndPointListener) ;

    }

    //1、录制路径
    private static void requestRecordPathData(final ArrayList<PathInfo> pathInfos,
            final String mapName, final OnResponseAllPathAndPointListener onResponseAllPathAndPointListener){

        RecordPathApi.requestRecordPathList(new ResponseListener<List<RecordPath>>() {

            @Override
            public void success(List<RecordPath> recordPaths) {

                final int size = recordPaths.size() ;
                for (int i = 0; i < size; i++) {

                    RecordPath recordPathData = recordPaths.get(i) ;
                    LogHelper.i(TAG, LogHelper.__TAG__() + ", name : " + recordPathData.name);

                    PathInfo pathInfo = new PathInfo(recordPathData.name, PathInfo.Type.RECORD_PATH) ;
                    pathInfo.setMapName(recordPathData.mapName);
                    pathInfos.add(pathInfo) ;

                }

                requestSketchGraphList(pathInfos, mapName ,onResponseAllPathAndPointListener) ;
            }

            @Override
            public void faild(String s, String s1) {

                LogHelper.w(TAG, LogHelper.__TAG__() + ", s : " + s + ", s1 : " + s1);

                onResponseAllPathAndPointListener.onFail("record_path");
            }

            @Override
            public void error(Throwable throwable) {

                LogHelper.e(TAG, LogHelper.__TAG__() + ", throwable : " + throwable.getMessage());

                onResponseAllPathAndPointListener.onError("record_path");
            }
        }, mapName);

    }

    //2、手绘路径
    private static void requestSketchGraphList(final ArrayList<PathInfo> pathInfos,
                final String mapName, final OnResponseAllPathAndPointListener onResponseAllPathAndPointListener){

        SketchGraphApi.requestSketchGraphList(new ResponseListener<List<SketchGraph>>() {
            @Override
            public void success(List<SketchGraph> sketchGraphs) {

                final int size = sketchGraphs.size() ;
                for (int i = 0; i < size; i++) {

                    SketchGraph sketchGraph = sketchGraphs.get(i) ;
                    LogHelper.i(TAG, LogHelper.__TAG__() + ", name : " + sketchGraph.name);

                    PathInfo pathInfo = new PathInfo(sketchGraph.name, PathInfo.Type.SKETCH_GRAPH) ;
                    pathInfo.setMapName(sketchGraph.mapName);
                    pathInfos.add(pathInfo) ;
                }

                requestTaskQueueList(pathInfos, mapName ,onResponseAllPathAndPointListener) ;
            }

            @Override
            public void faild(String s, String s1) {

                LogHelper.w(TAG, LogHelper.__TAG__() + ", s : " + s + ", s1 : " + s1);
                onResponseAllPathAndPointListener.onFail("sketch_graph");
            }

            @Override
            public void error(Throwable throwable) {

                LogHelper.e(TAG, LogHelper.__TAG__() + ", throwable : " + throwable.getMessage());
                onResponseAllPathAndPointListener.onError("sketch_graph");
            }
        },mapName);
    }


    //3、路径组合
    private static void requestTaskQueueList(final ArrayList<PathInfo> pathInfos,
              final String mapName, final OnResponseAllPathAndPointListener onGetAllPathAndPointListener){

        TaskQueueApi.requestTaskQueueList(new ResponseListener<List<TaskQueue>>() {
            @Override
            public void success(List<TaskQueue> taskQueues) {

                final int size = taskQueues.size() ;
                for (int i = 0; i < size ; i++) {

                    TaskQueue taskQueue = taskQueues.get(i) ;
                    LogHelper.i(TAG, LogHelper.__TAG__() + ", name : " + taskQueue.name);

                    PathInfo pathInfo = new PathInfo(taskQueue.name, PathInfo.Type.TASK_QUEUE) ;
                    pathInfo.setMapName(taskQueue.map_name);
                    pathInfos.add(pathInfo) ;
                }
                requestPositions(pathInfos, mapName ,onGetAllPathAndPointListener) ;
            }

            @Override
            public void faild(String s, String s1) {

                LogHelper.w(TAG, LogHelper.__TAG__() + ", s : " + s + ", s1 : " + s1);
                onGetAllPathAndPointListener.onFail("task_queue");
            }

            @Override
            public void error(Throwable throwable) {

                LogHelper.e(TAG, LogHelper.__TAG__() + ", throwable : " + throwable.getMessage());
                onGetAllPathAndPointListener.onError("task_queue");
            }
        },mapName);
    }


    //4、路径点
    private static void requestPositions(final ArrayList<PathInfo> pathInfos,
            final String mapName, final OnResponseAllPathAndPointListener onResponseAllPathAndPointListener){

        PositionApi.requestPositions(new ResponseListener<List<Position>>() {
            @Override
            public void success(List<Position> positions) {

                final int size = positions.size() ;
                for (int i = 0; i < size; i++) {

                    Position position = positions.get(i) ;
                    LogHelper.i(TAG, LogHelper.__TAG__() + ", name : " + position.name);

                    PathInfo pathInfo = new PathInfo(position.name, PathInfo.Type.POSITION) ;
                    pathInfo.setMapName(position.mapName);
                    pathInfos.add(pathInfo) ;
                }

                onResponseAllPathAndPointListener.onSuccess(pathInfos);

            }

            @Override
            public void faild(String s, String s1) {

                LogHelper.w(TAG, LogHelper.__TAG__() + ", s : " + s + ", s1 : " + s1);
                onResponseAllPathAndPointListener.onFail("position");
            }

            @Override
            public void error(Throwable throwable) {

                LogHelper.e(TAG, LogHelper.__TAG__() + ", throwable : " + throwable.getMessage());
                onResponseAllPathAndPointListener.onError("position");
            }
        },mapName);
    }


    public static void goToNearbyPathCharging(String mapName, String pointName){

        goToNearbyPathCharging(mapName, PATH_MAIN_LINE_NAME, pointName);
    }

    /**
     * 就近上预设轨道去充电
     * */
    private static void goToNearbyPathCharging(String mapName, String pathName, String pointName){

        LogHelper.w(TAG, LogHelper.__TAG__() + ", mapName : " + mapName + ", pathName : " + pathName + ", pointName : " + pointName);

        ResponseListener<String> responseListener = new ResponseListener<String>() {
            @Override
            public void success(String s) {

                LogHelper.w(TAG, LogHelper.__TAG__() + ", s : " + s);
            }

            @Override
            public void faild(String s, String s1) {

                LogHelper.w(TAG, LogHelper.__TAG__() + ", s : " + s + ", s1 : " + s1);
            }

            @Override
            public void error(Throwable throwable) {

                LogHelper.e(TAG, LogHelper.__TAG__() + ", throwable : " + throwable.getMessage());
            }
        } ;

        Task task = new Task() ;
        task.name = "NavigationTask" ;
        task.start_param = new JSONObject() ;
        task.start_param.put("map_name", mapName) ;
        task.start_param.put("path_name", pathName) ;
        task.start_param.put("position_name", pointName) ;
        task.start_param.put("path_type", 2) ;

        TaskQueue taskQueue = new TaskQueue() ;
        taskQueue.map_name = mapName ;
        taskQueue.tasks = new ArrayList<>();
        taskQueue.tasks.add(task) ;

        LogHelper.w(TAG, LogHelper.__TAG__() + ", taskQueue : " + new Gson().toJson(taskQueue) );
        TaskQueueApi.startTaskQueue(responseListener, taskQueue) ;

//        ResponseListener<Boolean> responseListener1 = new ResponseListener<Boolean>() {
//            @Override
//            public void success(Boolean aBoolean) {
//
//            }
//
//            @Override
//            public void faild(String s, String s1) {
//
//            }
//
//            @Override
//            public void error(Throwable throwable) {
//
//            }
//        };
//        SketchCheckPath sketchCheckPath = new SketchCheckPath();
//        sketchCheckPath.lines = new ArrayList<>() ;
//
//        SketchCheckLine sketchCheckLine = new SketchCheckLine() ;
//
//        sketchCheckPath.lines.add(sketchCheckLine) ;
//
//
//        SketchGraphApi.verifySketchPath(responseListener1, sketchCheckPath) ;
    }

    /**
     * 定时任务（需要重复执行）
     * */
    public static  void goToTimerTaskPath(String mapName, String pathName){

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

    /**
     * 收队任务
     * */
    public static  void goToTeamTaskPath(String mapName, String teamName, String teamPathName){

        playGraphPathTask(mapName, teamName, teamPathName) ;
    }

    /**
     * 开始手绘路径任务
     * */
    public static void playGraphPathTask(String mapName, String graphName, String graphPathName){

        LogHelper.w(TAG, LogHelper.__TAG__() + ", mapName : " + mapName + ", graphName : " + graphName + ", graphPathName : " + graphPathName);

        ResponseListener<String> responseListener = new ResponseListener<String>() {
            @Override
            public void success(String s) {

                LogHelper.w(TAG, LogHelper.__TAG__() + ", s : " + s);
            }

            @Override
            public void faild(String s, String s1) {

                LogHelper.w(TAG, LogHelper.__TAG__() + ", s : " + s + ", s1 : " + s1);
            }

            @Override
            public void error(Throwable throwable) {

                LogHelper.e(TAG, LogHelper.__TAG__() + ", throwable : " + throwable.getMessage());
            }
        } ;

        Task task = new Task() ;
        task.name = "PlayGraphPathTask" ;
        task.start_param = new JSONObject() ;
        task.start_param.put("map_name", mapName) ;
        task.start_param.put("graph_name", graphName) ;
        task.start_param.put("graph_path_name", graphPathName) ;

        TaskQueue taskQueue = new TaskQueue() ;
        taskQueue.map_name = mapName ;
        taskQueue.tasks = new ArrayList<>();
        taskQueue.tasks.add(task) ;

        LogHelper.w(TAG, LogHelper.__TAG__() + ", taskQueue : " + new Gson().toJson(taskQueue) );
        TaskQueueApi.startTaskQueue(responseListener, taskQueue) ;
    }


    /**
     * 查询队伍列表
     * */
    public static void queryTeamTasks(String mapName, final ArrayList<TeamTask> teamTasks){

        SketchGraphApi.requestSketchGraphList(new ResponseListener<List<SketchGraph>>() {
            @Override
            public void success(List<SketchGraph> sketchGraphs) {

                final int size = sketchGraphs.size() ;
                for (int i = 0; i < size; i++) {

                    SketchGraph sketchGraph = sketchGraphs.get(i) ;
                    LogHelper.i(TAG, LogHelper.__TAG__() + ", name : " + sketchGraph.name);

                    List<SketchLine> lines = sketchGraph.lines ;
                    if(lines != null && lines.size() == 1){ // 有且只有一个条直线

                        SketchLine line = lines.get(0) ;

                        String start = line.begin ;
                        String end = line.end ;

                        if((start != null) && (start.endsWith("start")) &&
                                (end != null) && (end.endsWith("end"))){

                            LogHelper.i(TAG, LogHelper.__TAG__() + ", name : " + sketchGraph.name + ", start : " + start+ ", end : " + end);

                            if(sketchGraph.paths != null && sketchGraph.paths.size() == 1 ){


                                TeamTask teamTask = new TeamTask() ;
                                teamTask.setTeamHelperPointName(sketchGraph.name);
                                teamTask.setStartPointName(start);
                                teamTask.setEndPointName(end);
                                teamTask.setTeamName(sketchGraph.name);
                                teamTask.setTeamPathName(sketchGraph.paths.get(0).name);

                                teamTasks.add(teamTask) ;

                            }

                        }
                    }
                }

            }

            @Override
            public void faild(String s, String s1) {

                LogHelper.w(TAG, LogHelper.__TAG__() + ", s : " + s + ", s1 : " + s1);
            }

            @Override
            public void error(Throwable throwable) {

                LogHelper.e(TAG, LogHelper.__TAG__() + ", throwable : " + throwable.getMessage());
            }
        },mapName);

    }

    /**
     * 导航到指定的点
     * @param mapName 地图名称
     * @@param pointName 点的名称
     * */
    public static void startPointTask(String mapName, String pointName){

        LogHelper.w(TAG, LogHelper.__TAG__() + ", mapName : " + mapName + ", pointName : " + pointName );

        ResponseListener<String> responseListener = new ResponseListener<String>() {
            @Override
            public void success(String s) {

                LogHelper.w(TAG, LogHelper.__TAG__() + ", s : " + s);
            }

            @Override
            public void faild(String s, String s1) {

                LogHelper.w(TAG, LogHelper.__TAG__() + ", s : " + s + ", s1 : " + s1);
            }

            @Override
            public void error(Throwable throwable) {

                LogHelper.e(TAG, LogHelper.__TAG__() + ", throwable : " + throwable.getMessage());
            }
        } ;

        Task task = new Task() ;
        task.name = "NavigationTask" ;
        task.start_param = new JSONObject() ;
        task.start_param.put("map_name", mapName) ;
        task.start_param.put("position_name", pointName) ;

        TaskQueue taskQueue = new TaskQueue() ;
        taskQueue.tasks = new ArrayList<>();
        taskQueue.tasks.add(task) ;

        LogHelper.w(TAG, LogHelper.__TAG__() + ", taskQueue : " + new Gson().toJson(taskQueue) );
        TaskQueueApi.startTaskQueue(responseListener, taskQueue) ;
    }





    /**判断*/
    private static Timer mTurnToTimer ;
    private static TimerTask mTurnToTask ;
    private static MoveToListener mMoveToListener ;
    private static ResponseListener<Boolean> mIsMoveToFinishedListener = new ResponseListener<Boolean>() {
        @Override
        public void success(Boolean aBoolean) {

            LogHelper.i(TAG, LogHelper.__TAG__() + ", aBoolean : " + aBoolean) ;

            if (aBoolean) {

                if(mMoveToListener != null){

                    mMoveToListener.moveTo();
                }

                stopListener() ;
            }

        }

        @Override
        public void faild(String s, String s1) {

        }

        @Override
        public void error(Throwable throwable) {

        }
    } ;
    interface MoveToListener{

        void moveTo() ;
    }
    public static void forward(float distance, float speed, MoveToListener moveToListener){

        LogHelper.i(TAG, LogHelper.__TAG__() + ", distance : " + distance + ", speed : " + speed );

        mMoveToListener = moveToListener ;

        GsApi.moveTo(null,distance,speed ) ;
        startListener() ;
    }
    private static void startListener(){

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
    private static void stopListener(){

        if(mTurnToTimer != null){

            mTurnToTimer.cancel();
            mTurnToTimer = null ;
        }

        if(mTurnToTask != null){

            mTurnToTask.cancel();
            mTurnToTask = null ;
        }
    }


}
