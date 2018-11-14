package com.yongyida.robot.json.response;

import java.util.ArrayList;

/**
 * Create By HuangXiangXiang 2018/11/2
 * 需要收队的队列
 *
 */
public class ResponseCloseTeamNames extends BaseResponse {

    /**
     * 当前正在收队的名称
     * */
    private String currTeamName ;

    /**
     * 需要收队队列名称
     * */
    private ArrayList<String> teamNames ;

    public String getCurrTeamName() {
        return currTeamName;
    }

    public void setCurrTeamName(String currTeamName) {
        this.currTeamName = currTeamName;
    }

    public ArrayList<String> getTeamNames() {
        return teamNames;
    }

    public void setTeamNames(ArrayList<String> teamNames) {
        this.teamNames = teamNames;
    }

    @Override
    public int getType() {
        return TYPE_RESPONSE_CLOSE_TEAM_NAMES;
    }
}
