package com.yongyida.robot.json.response;

/**
 * Create By HuangXiangXiang 2018/10/25
 *
 */
public class ResponseRobotInfo extends BaseResponse {

    private int batteryLevel ;  // 电量水平
    private int connectState ;  // 连接底盘 //xianx
    private int underPanState ; // 底盘信息
    private int locationState ; // 定位信息


    @Override
    public int getType() {
        return TYPE_RESPONSE_ROBOT_INFO;
    }



}
