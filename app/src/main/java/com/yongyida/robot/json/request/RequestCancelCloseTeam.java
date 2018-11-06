package com.yongyida.robot.json.request;

import com.yongyida.robot.json.Base;

/**
 * Create By HuangXiangXiang 2018/11/2
 */
public class RequestCancelCloseTeam extends Base{

    private String teamName ;

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    @Override
    public int getType() {

        return TYPE_REQUEST_CANCEL_CLOSE_TEAM;
    }
}
