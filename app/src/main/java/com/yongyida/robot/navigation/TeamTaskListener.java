package com.yongyida.robot.navigation;

/**
 * Create By HuangXiangXiang 2018/10/24
 */
public interface TeamTaskListener {

    /**查询不到对应的任务*/
    int NO_SUCH_NAME_TEAM_TASK              = 1 ;
    /**电量过低*/
    int BATTERY_TO_LOW                      = 2 ;

    /**开始队伍的辅助线*/
    void onStartTeamLine(String teamName);
    /**开始收队*/
    void onTeamTaskStart(String teamName) ;
    /**收队进度条*/
    void onTeamTaskSchedule(String teamName,float percent) ;
    /**完成一个收队*/
    void onTeamTaskComplete(String teamName) ;

    /**任务失败*/
    void onFail(String teamName,int failCode, String failMessage) ;

    /**完成全部收队*/
    void onAllTeamTaskComplete() ;
}
