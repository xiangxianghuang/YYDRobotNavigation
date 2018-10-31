package com.yongyida.robot.navigation.bean;

import com.yongyida.robot.navigation.NavigationHelper;

/**
 * Create By HuangXiangXiang 2018/10/18
 * 收队任务信息
 */
public class TeamTask extends BaseTask {


    /**是否达到队伍起辅助始点*/
    private boolean isArrivedTeamHelperPoint = false;
    /**收队的辅助路线名称（默认使用"teamLine"）*/
    private String teamHelperPathName = NavigationHelper.TEAM_LINE_PATH_NAME ;
    /**收队的辅助路线的点名称(默认和收队队伍的名称一致)*/
    private String teamHelperPointName ;


    /**收队任务起点名称*/
    private String startPointName ;

    /**收队任务终点名称*/
    private String endPointName ;

    /**
     * 队伍名称
     * */
    private String teamName ;

    /**
     * 队伍路径名称
     * */
    private String teamPathName ;


    @Override
    public TaskType getTaskType() {

        return TaskType.TEAM;
    }

    public boolean isArrivedTeamHelperPoint() {
        return isArrivedTeamHelperPoint;
    }

    public void setArrivedTeamHelperPoint(boolean arrivedTeamHelperPoint) {
        isArrivedTeamHelperPoint = arrivedTeamHelperPoint;
    }

    public String getTeamHelperPathName() {
        return teamHelperPathName;
    }

    public void setTeamHelperPathName(String teamHelperPathName) {
        this.teamHelperPathName = teamHelperPathName;
    }

    public String getTeamHelperPointName() {
        return teamHelperPointName;
    }

    public void setTeamHelperPointName(String teamHelperPointName) {
        this.teamHelperPointName = teamHelperPointName;
    }

    public String getStartPointName() {
        return startPointName;
    }

    public void setStartPointName(String startPointName) {
        this.startPointName = startPointName;
    }

    public String getEndPointName() {
        return endPointName;
    }

    public void setEndPointName(String endPointName) {
        this.endPointName = endPointName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamPathName() {
        return teamPathName;
    }

    public void setTeamPathName(String teamPathName) {
        this.teamPathName = teamPathName;
    }

}
