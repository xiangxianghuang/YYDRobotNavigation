package com.yongyida.robot.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.yongyida.robot.navigation.bean.TaskInfo;

import java.util.ArrayList;

/**
 * Create By HuangXiangXiang 2018/8/31
 *
 *
 */
public class TaskInfoData {

    private static final String TAG = PathDetailInfoData.class.getSimpleName() ;

    private static final Gson GSON = new Gson() ;
    private static final String TASK_INFO_DATA = "TaskInfoData" ;

    private Context context ;

    private static TaskInfoData mInstance ;
    public static TaskInfoData getInstance(Context context){

        if(mInstance == null) {
            mInstance = new TaskInfoData(context.getApplicationContext()) ;
        }
        return mInstance ;
    }

    private TaskInfoData(Context context){

        this.context = context ;
    }

    public ArrayList<TaskInfo> query(String key){

        SharedPreferences sp = context.getSharedPreferences(TASK_INFO_DATA, Context.MODE_PRIVATE) ;
        String json = sp.getString(key, null) ;
        if(!TextUtils.isEmpty(json)){

            TaskInfos tasks = GSON.fromJson(json, TaskInfos.class) ;
            if(tasks != null){

                return tasks.taskInfos ;
            }
        }

        return null;
    }


    public void save(String key, ArrayList<TaskInfo> taskInfos){

        TaskInfos tasks = new TaskInfos();
        tasks.taskInfos = taskInfos ;

        String json = GSON.toJson(tasks) ;

        SharedPreferences sp = context.getSharedPreferences(TASK_INFO_DATA, Context.MODE_PRIVATE) ;
        SharedPreferences.Editor editor = sp.edit() ;
        editor.putString(key, json);
        editor.apply();

    }




    private class TaskInfos{

        private ArrayList<TaskInfo> taskInfos ;
    }

}
