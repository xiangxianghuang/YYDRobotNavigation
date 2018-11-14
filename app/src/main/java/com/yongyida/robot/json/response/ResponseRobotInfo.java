package com.yongyida.robot.json.response;

/**
 * Create By HuangXiangXiang 2018/10/25
 * 电量信息：充电状态以及电量
 * 底盘错误信息:
 * 高仙状态:
 *
 */
public class ResponseRobotInfo extends BaseResponse {


    /**
     * 充电状态
     * 0 表示未充电
     * 1 表示充电
     * */
    private int batteryState = 0 ;

    /**
     * 充电电量
     * 小于0 表示电量未知
     * 0-100 表示正常电量水平
     * */
    private int batteryLevel = 0 ;


//    /**
//     * 表示机器是否在线(可以通过向服务器查询是否在线，我这边无法获取)
//     * */
//    private int connectState = - 1 ;

    /**
     * 底盘错误状态信息（松轴、）
     * 0 表示正常
     * 1 表示有问题
     * */
    private int underPanState = 0 ;

    /**
     * 定位信息（开始初始会定位、运动中定位失败、不能执行任务）
     * 0 表示未定位
     * 1 表示成功定位
     * */
    private int locationState = 0;

    public int getBatteryState() {

        return batteryState;
    }

    public boolean setBatteryState(int batteryState) {

        if(this.batteryState != batteryState){

            this.batteryState = batteryState;
            return true ;
        }
        return false ;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public boolean setBatteryLevel(int batteryLevel) {

        if(this.batteryLevel != batteryLevel){

            this.batteryLevel = batteryLevel;
            return true ;
        }
        return false ;
    }


    public int getUnderPanState() {
        return underPanState;
    }

    public boolean setUnderPanState(int underPanState) {

        if(this.underPanState != underPanState){

            this.underPanState = underPanState;
            return true ;
        }
        return false ;
    }

    public int getLocationState() {
        return locationState;
    }

    public boolean setLocationState(int locationState) {

        if(this.locationState != locationState){

            this.locationState = locationState;
            return true ;
        }
        return false ;
    }

    @Override
    public int getType() {
        return TYPE_RESPONSE_ROBOT_INFO;
    }

}
