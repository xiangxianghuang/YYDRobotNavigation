package com.yongyida.robot.navigation;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.gs.gsnavlibrary.api.PositionApi;
import com.gs.gsnavlibrary.api.RecordPathApi;
import com.gs.gsnavlibrary.api.SketchGraphApi;
import com.gs.gsnavlibrary.api.TaskQueueApi;
import com.gs.gsnavlibrary.bean.position.Position;
import com.gs.gsnavlibrary.bean.recordpath.RecordPath;
import com.gs.gsnavlibrary.bean.sketchpath.SketchCheckLine;
import com.gs.gsnavlibrary.bean.sketchpath.SketchCheckPath;
import com.gs.gsnavlibrary.bean.sketchpath.SketchGraph;
import com.gs.gsnavlibrary.bean.taskqueue.Task;
import com.gs.gsnavlibrary.bean.taskqueue.TaskQueue;
import com.gs.gsnavlibrary.listener.ResponseListener;
import com.yongyida.robot.navigation.bean.PathInfo;
import com.yongyida.robot.util.LogHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Create By HuangXiangXiang 2018/8/24
 * 导航帮助类
 */
public class NavigationHelper {

    private static final String TAG = NavigationHelper.class.getSimpleName() ;

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


    /**
     * 就近上预设轨道去充电
     * */
    public static void goToNearbyPathCharging(String mapName, String pathName, String pointName){

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

}
