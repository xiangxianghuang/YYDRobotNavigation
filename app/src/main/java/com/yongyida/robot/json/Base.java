package com.yongyida.robot.json;

/**
 * Create By HuangXiangXiang 2018/11/2
 */
public abstract class Base {

    /**请求机器信息*/
    public static final int TYPE_REQUEST_ROBOT_INFO                 = 0x0001 ;
    /**请求收队*/
    public static final int TYPE_REQUEST_CLOSE_TEAM                 = 0x0002 ;
    /**请求取消收队*/
    public static final int TYPE_REQUEST_CANCEL_CLOSE_TEAM          = 0x0003 ;
    /**请求收队信息*/
    public static final int TYPE_REQUEST_CLOSE_TEAM_NAMES           = 0x0004 ;


    /**响应机器信息*/
    public static final int TYPE_RESPONSE_ROBOT_INFO                = 0x1001 ;
    /**响应收队*/
    public static final int TYPE_RESPONSE_CLOSE_TEAM                = 0x1002 ;
    /**响应取消收队*/
    public static final int TYPE_RESPONSE_CANCEL_CLOSE_TEAM         = 0x1003 ;
    /**响应收队信息*/
    public static final int TYPE_RESPONSE_CLOSE_TEAM_NAMES          = 0x1004 ;


    public abstract int getType() ;
}
