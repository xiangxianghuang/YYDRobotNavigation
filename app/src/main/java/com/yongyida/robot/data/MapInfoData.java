package com.yongyida.robot.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.yongyida.robot.navigation.bean.MapInfo;

/**
 * Create By HuangXiangXiang 2018/8/28
 */
public class MapInfoData {

    private static final String SELECT_MAP_INFO         = "selectMapInfo" ;
    private static final String MAP_NAME                = "mapName" ;
    private static final String START_POINT_NAME        = "startPointName" ;


    /**
     * 选中的地图
     * */
    public static MapInfo getSelectMapInfo(Context context){

        SharedPreferences sp = context.getSharedPreferences(SELECT_MAP_INFO, Context.MODE_PRIVATE) ;
        String mapName = sp.getString(MAP_NAME, null) ;
        String startPointName = sp.getString(START_POINT_NAME, null) ;

        MapInfo mapInfo = new MapInfo() ;
        mapInfo.setMapName(mapName);
        mapInfo.setStartPointName(startPointName);

        return mapInfo ;
    }


    /**
     * 保存选中的地图
     * */
    public static void saveSelectMapInfo(Context context, MapInfo mapInfo){

        SharedPreferences sp = context.getSharedPreferences(SELECT_MAP_INFO, Context.MODE_PRIVATE) ;
        SharedPreferences.Editor editor = sp.edit() ;

        editor.putString(MAP_NAME, mapInfo.getMapName()) ;
        editor.putString(START_POINT_NAME, mapInfo.getStartPointName()) ;

        editor.apply();

    }



}
