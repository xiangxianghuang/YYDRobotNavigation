package com.yongyida.robot.json;

/**
 * Create By HuangXiangXiang 2018/10/26
 * 手机（经主服务）发送给APP端
 * 1、请求机器底盘信息
 * 2、请求收队
 */
public class FromMoblie {

    /**请求机器信息*/
    public static final int TYPE_REQUEST_Robot_INFO  = 0x01 ;

    /**请求收队*/
    public static final int TYPE_REQUEST_CLOSE_TEAM     = 0x02 ;

    /***/
    private int type = TYPE_REQUEST_CLOSE_TEAM ;

    /**json数据*/
    private String jsonData ;




}
