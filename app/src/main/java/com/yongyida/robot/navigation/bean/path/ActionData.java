package com.yongyida.robot.navigation.bean.path;

/**
 * Create By HuangXiangXiang 2018/8/31
 * 为了 保存JSON数据
 */
public class ActionData {

    private PlayAction playAction ;
    private StopAction stopAction ;

    public static ActionData getPlayAction(){

        ActionData actionData = new ActionData() ;
        actionData.playAction = new PlayAction() ;

        return actionData ;
    }

    public static ActionData getStopAction(){

        ActionData actionData = new ActionData() ;
        actionData.stopAction = new StopAction() ;

        return actionData ;
    }



    public void setAction(BaseAction action){

        playAction = null ;
        stopAction = null ;

        switch (action.getType()){

            case STOP:

                stopAction = (StopAction) action;
                break;

            case PLAY:

                playAction = (PlayAction) action;
                break;
        }

    }


    public BaseAction getAction(){

        if(playAction != null){

            return playAction ;
        }

        if(stopAction != null){

            return stopAction ;
        }

        return null ;
    }


}
