package com.yongyida.robot.navigation.bean;

/**
 * Create By HuangXiangXiang 2018/8/22
 */
public class MapInfo {

    // 地图名称
    public String mapName ;
    // 起始点名称
    public String startPointName ;

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getStartPointName() {
        return startPointName;
    }

    public void setStartPointName(String startPointName) {

        this.startPointName = startPointName;
    }


    public boolean equals(MapInfo mapInfo){

        if(this == mapInfo){

            return true ;
        }

        if(mapInfo == null){

            return false ;
        }

        if(mapName != null){
            if(!mapName.equals(mapInfo.mapName)){

                return false ;
            }
        }else{
            if(mapInfo.mapName != null){
                return false;
            }
        }

        if(startPointName != null){
            if(!startPointName.equals(mapInfo.startPointName)){

                return false ;
            }
        }else{
            if(mapInfo.startPointName != null){
                return false;
            }
        }

        return true ;
    }
}
