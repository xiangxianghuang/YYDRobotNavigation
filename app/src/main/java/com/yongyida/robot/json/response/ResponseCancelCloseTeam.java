package com.yongyida.robot.json.response;

/**
 * Create By HuangXiangXiang 2018/11/2
 */
public class ResponseCancelCloseTeam extends BaseResponse {

    /**
     * 是否成功取消收队
     * */
    private boolean isSuccessCancel ;

    /**
     * 队伍名称
     * */
    private String teamName ;

    public boolean isSuccessCancel() {
        return isSuccessCancel;
    }

    public void setSuccessCancel(boolean successCancel) {
        isSuccessCancel = successCancel;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    @Override
    public int getType() {
        return TYPE_RESPONSE_CANCEL_CLOSE_TEAM;
    }
}
