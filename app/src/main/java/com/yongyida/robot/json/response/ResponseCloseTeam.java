package com.yongyida.robot.json.response;

/**
 * Create By HuangXiangXiang 2018/11/2
 * 反馈收队信息
 */
public class ResponseCloseTeam extends BaseResponse{

    // 通过辅线前往收队起始点
    public static final int ACTION_GO_TO_START_POINT_BY_AUXILIARY_LINE  = 0x01;
    // 开始收队
    public static final int ACTION_START_CLOSE_TEAM                     = 0x02;
    // 收队中
    public static final int ACTION_CLOSING_TEAM                         = 0x03;
    // 完成(一个)收队
    public static final int ACTION_COMPLETE_CLOSE_TEAM                  = 0x04;
    // 收队失败
    public static final int ACTION_FAIL_CLOSE_TEAM                      = 0x05;
    // 完成全部收队
    public static final int ACTION_COMPLETE_ALL_CLOSE_TEAM              = 0x06;

    /**收队类型*/
    private int action ;
    /**队伍名*/
    private String teamName ;
    /**进度*/
    private Schedule schedule ;
    /**错误*/
    private Fail fail ;


    /**完成全部收队*/
    public void completeAllCloseTeam(){

        this.action = ACTION_COMPLETE_ALL_CLOSE_TEAM ;
        this.teamName = null ;
        this.schedule = null ;
        this.fail = null ;
    }

    /**收队中*/
    public void closingTeam(String teamName,Schedule schedule){

        this.action = ACTION_CLOSING_TEAM ;
        this.teamName = teamName ;
        this.schedule = schedule ;
        this.fail = null ;
    }

    /**收队失败*/
    public void failCloseTeam(String teamName, Fail fail){

        this.action = ACTION_FAIL_CLOSE_TEAM ;
        this.teamName = teamName ;
        this.schedule = null ;
        this.fail = fail ;
    }

    /**
     *
     * 发送普通的
     * */
    public void setActionAndTeamName(int action, String teamName){

        this.action = action ;
        this.teamName = teamName ;
        this.schedule = null ;
        this.fail = null ;
    }

    @Override
    public int getType() {
        return TYPE_RESPONSE_CLOSE_TEAM;
    }


    /**进度*/
    public static class Schedule{

        private float percent ;

        public float getPercent() {
            return percent;
        }

        public void setPercent(float percent) {
            this.percent = percent;
        }
    }


    /**错误*/
    public static class Fail{

        /**查询不到对应的任务*/
        public static final int NO_SUCH_TEAM_NAME       = 1 ;
        /**电量过低*/
        public static final int BATTERY_TO_LOW          = 2 ;

        private int failCode ;
        private String failMessage ;

        public int getFailCode() {
            return failCode;
        }

        public void setFailCode(int failCode) {
            this.failCode = failCode;
        }

        public String getFailMessage() {
            return failMessage;
        }

        public void setFailMessage(String failMessage) {
            this.failMessage = failMessage;
        }
    }
}
