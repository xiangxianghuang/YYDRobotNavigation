package com.yongyida.robot.navigation.bean;

/**
 * Create By HuangXiangXiang 2018/8/23
 * 路径
 * 可以是一个点
 * 也可以是一个路径
 */
public class PathInfo {

    public enum Type{

        RECORD_PATH ,   // 录制路径
        SKETCH_GRAPH ,  // 手绘路径
        TASK_QUEUE ,    // 路径组合
        POSITION,       // 路径点
    }

    private String name ;
    private Type type ;
    private String mapName ;


    public PathInfo(){}


    public PathInfo(String name, Type type){

        this.name = name ;
        this.type = type ;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
