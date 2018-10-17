package com.yongyida.robot.navigation.bean;

import com.yongyida.robot.navigation.bean.path.ActionData;
import com.yongyida.robot.navigation.bean.path.BaseAction;

import java.util.ArrayList;

/**
 * Create By HuangXiangXiang 2018/8/28
 * 路径点对应的动作
 */
public class PointActionInfo {

//    /**点的名称*/
//    private String name ;
//
//    public PointActionInfo(String name){
//
//        this.name = name ;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }

    /**
     * 动作集合
     * */
    private final ArrayList<ActionData> actions = new ArrayList<>() ;

    public void addAction(BaseAction.Type type){

        switch (type){

            case PLAY:

                actions.add(ActionData.getPlayAction()) ;
                break;

            case STOP:

                actions.add(ActionData.getStopAction()) ;
                break;
        }
    }

    public ArrayList<ActionData> getActions() {

        return actions;
    }
}
