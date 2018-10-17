package com.yongyida.robot.navigation.bean.path;

/**
 * Create By HuangXiangXiang 2018/8/28
 */
public abstract class BaseAction {

    public abstract Type getType() ;

    public enum Type{

        PLAY("播放"),     // 播放
        STOP("停止");     // 停止

        public String name ;

        Type(String name){

            this.name = name ;
        }

    }

}
