package com.yongyida.robot.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.yongyida.robot.navigation.bean.PointActionInfo;
import com.yongyida.robot.util.LogHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Create By HuangXiangXiang 2018/8/31
 * 路径点详细信息
 *
 */
public class PathDetailInfoData {

    private static final String TAG = PathDetailInfoData.class.getSimpleName() ;


    private static final Gson GSON = new Gson() ;
    private static final String PATH_DETAIL_INFO_DATA = "PathDetailInfoData" ;

    private Context context ;

    private static PathDetailInfoData mInstance ;
    public static PathDetailInfoData getInstance(Context context){

        if(mInstance == null) {
            mInstance = new PathDetailInfoData(context.getApplicationContext()) ;
        }
        return mInstance ;
    }

    private PathDetailInfoData(Context context){

        this.context = context ;
    }



    public static String toKey(String mapName, String pathName, String pointName){

        return mapName + "_" + pathName + "_" + pointName ;
    }


    /**
     * 获取某个地图 某条路径 某个点的详细信息
     *
     * */
    public PointActionInfo queryPathDetailInfo(String pointName){

        PointActionInfo pointActionInfo = null ;

        SharedPreferences sp = context.getSharedPreferences(PATH_DETAIL_INFO_DATA, Context.MODE_PRIVATE) ;

        String key = pointName ;
        String data = sp.getString(key, null) ;
        LogHelper.i(TAG, LogHelper.__TAG__() + ",data : " + data);
        if(!TextUtils.isEmpty(data)){

            pointActionInfo = GSON.fromJson(data, PointActionInfo.class);
        }

        if(pointActionInfo == null){

            pointActionInfo = new PointActionInfo() ;
        }

        return pointActionInfo ;
    }


    /**
     * 保存数据
     * */
    public void savePathDetailInfos(HashMap<String,PointActionInfo> pointInfos){

        SharedPreferences sp = context.getSharedPreferences(PATH_DETAIL_INFO_DATA, Context.MODE_PRIVATE) ;
        SharedPreferences.Editor editor = sp.edit() ;

        for (Map.Entry<String,PointActionInfo> entry : pointInfos.entrySet()) {

            String key = entry.getKey() ;
            PointActionInfo pointActionInfo = entry.getValue() ;
            String json = GSON.toJson(pointActionInfo) ;

            editor.putString(key, json) ;
        }

        editor.apply() ;
    }


    /**
     * 保存数据
     * */
    public void savePathDetailInfo(String pathKey, PointActionInfo pointInfo){

        SharedPreferences sp = context.getSharedPreferences(PATH_DETAIL_INFO_DATA, Context.MODE_PRIVATE) ;
        SharedPreferences.Editor editor = sp.edit() ;
        String data = GSON.toJson(pointInfo) ;
        LogHelper.i(TAG, LogHelper.__TAG__() + ",data : " + data);
        editor.putString(pathKey, data) ;
        editor.apply() ;

    }


}
