package com.yongyida.robot.navigation.bean;

import com.yongyida.robot.navigation.NavigationHelper;

/**
 * Create By HuangXiangXiang 2018/10/18
 * 充电任务
 *
 * 临界值
 */
public class ChargingTask extends BaseTask {


    /**不充电时候 收队任务的极限电量值 20% */
    public static final int TEAM_BATTERY_NO_CHARGING    = 5 ;
    /**充电时候 收队任务的极限电量值 30%  */
    public static final int TEAM_BATTERY_CHARGING       = 30 ;

    /**
     * 17.30 之前 低于20的电量自动充电， 超过90的电量自动开始任务
     */
    public static final int[] BATTERY_BEFORE_17_30 = { 5, 90 } ;

    /**
     * 17.30 之后 低于20的电量自动充电， 超过70的电量自动开始任务
     */
    public static final int[] BATTERY_AFTER_17_30 = { 5, 70 } ;


    /**电量的临界值*/
    private int[] batters = BATTERY_BEFORE_17_30;

    /**
     * true  表示去充电路径上
     * false 表示离开充电路径上
     * */
    private boolean isIn = true;

    /**冲出去*/
    private boolean isForward = false ;

    /**充电点的名称*/
    private String autoChargingPointName       = NavigationHelper.AUTO_CHARGING_POINT_NAME;

    //充电路径
    private String inAutoChargingPathName     = NavigationHelper.IN_AUTO_CHARGING_PATH_NAME ;
    //充电点
    private String inAutoChargingPointName    = NavigationHelper.IN_AUTO_CHARGING_POINT_NAME ;

    //离开充电的路径
    private String outAutoChargingPathName     =NavigationHelper.OUT_AUTO_CHARGING_PATH_NAME ;
    //离开充电的最终点
    private String outAutoChargingPointName    =NavigationHelper.OUT_AUTO_CHARGING_POINT_NAME ;

    public int[] getBatters() {
        return batters;
    }

    public void setBatters(int[] batters) {
        this.batters = batters;
    }

    public boolean isIn() {
        return isIn;
    }

    public void setIn(boolean in) {
        isIn = in;
    }

    public String getAutoChargingPointName() {
        return autoChargingPointName;
    }

    public void setAutoChargingPointName(String autoChargingPointName) {
        this.autoChargingPointName = autoChargingPointName;
    }

    public String getInAutoChargingPathName() {
        return inAutoChargingPathName;
    }

    public void setInAutoChargingPathName(String inAutoChargingPathName) {
        this.inAutoChargingPathName = inAutoChargingPathName;
    }

    public String getInAutoChargingPointName() {
        return inAutoChargingPointName;
    }

    public void setInAutoChargingPointName(String inAutoChargingPointName) {
        this.inAutoChargingPointName = inAutoChargingPointName;
    }

    public String getOutAutoChargingPathName() {
        return outAutoChargingPathName;
    }

    public void setOutAutoChargingPathName(String outAutoChargingPathName) {
        this.outAutoChargingPathName = outAutoChargingPathName;
    }

    public String getOutAutoChargingPointName() {
        return outAutoChargingPointName;
    }

    public void setOutAutoChargingPointName(String outAutoChargingPointName) {
        this.outAutoChargingPointName = outAutoChargingPointName;
    }

    public boolean isForward() {
        return isForward;
    }

    public void setForward(boolean forward) {
        isForward = forward;
    }

    @Override
    public TaskType getTaskType() {

        return TaskType.CHARGING;
    }




}
