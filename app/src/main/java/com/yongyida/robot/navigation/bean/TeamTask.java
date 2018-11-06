package com.yongyida.robot.navigation.bean;

/**
 * Create By HuangXiangXiang 2018/10/18
 * 收队任务信息
 */
public class TeamTask extends BaseTask {


    /**
     * 收队的辅助路线的点名称(默认和收队队伍的名称一致)
     * */
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
