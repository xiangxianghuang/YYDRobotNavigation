package com.yongyida.robot.json.request;

import com.yongyida.robot.json.Base;

import java.util.ArrayList;

/**
 * Create By HuangXiangXiang 2018/11/2
 */
public class RequestCloseTeam extends Base {

    /**队伍名称*/
    private ArrayList<String> teamNames ;

    public ArrayList<String> getTeamNames() {
        return teamNames;
    }

    public void setTeamNames(ArrayList<String> teamNames) {
        this.teamNames = teamNames;
    }

    @Override
    public int getType() {
        return TYPE_REQUEST_CLOSE_TEAM;
    }
}
