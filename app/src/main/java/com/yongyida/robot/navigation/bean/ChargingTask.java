package com.yongyida.robot.navigation.bean;

/**
 * Create By HuangXiangXiang 2018/10/18
 * 充电任务
 *
 * 临界值
 */
public class ChargingTask extends BaseTask {


    /**不充电时候 收队任务的极限电量值 20% */
    public static final int TEAM_BATTERY_NO_CHARGING    = 20 ;
    /**充电时候 收队任务的极限电量值 30% */
    public static final int TEAM_BATTERY_CHARGING       = 30 ;

    /**
     * 17.30 之前 低于20的电量自动充电， 超过90的电量自动开始任务
     */
    public static final int[] BATTERY_BEFORE_17_30 = { 20, 90 } ;

    /**
     * 17.30 之后 低于20的电量自动充电， 超过70的电量自动开始任务
     */
    public static final int[] BATTERY_AFTER_17_30 = { 20, 70 } ;


    /**电量的临界值*/
    private int[] batters = BATTERY_BEFORE_17_30;


    public int[] getBatters() {
        return batters;
    }

    public void setBatters(int[] batters) {
        this.batters = batters;
    }

    @Override
    public TaskType getTaskType() {

        return TaskType.CHARGING;
    }




}
