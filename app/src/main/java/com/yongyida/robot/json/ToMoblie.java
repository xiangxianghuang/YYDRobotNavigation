package com.yongyida.robot.json;

/**
 * Create By HuangXiangXiang 2018/10/26
 * 发送给手机
 * APP端（经主服务）发送给手机
 *
 */
public class ToMoblie {

    /**请求机器信息*/
    public static final int TYPE_REQUEST_ROBOT_INFO     = 0x01 ;

    /**请求收队*/
    public static final int TYPE_REQUEST_CLOSE_TEAM     = 0x02 ;

    /***/
    private int type = TYPE_REQUEST_CLOSE_TEAM ;

    /**json数据*/
    private String jsonData ;


}
