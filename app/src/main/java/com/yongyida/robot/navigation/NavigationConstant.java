package com.yongyida.robot.navigation;

/**
 * Create By HuangXiangXiang 2018/11/9
 */
public class NavigationConstant {


    // 开始定时任务
    public static final String TASK_TIMER_0 = "时间变化，开始定时任务" ;
    public static final String TASK_TIMER_1 = "电量变化，开始定时任务" ;
    public static final String TASK_TIMER_2 = "收队任务结束，开始定时任务" ;
    public static final String TASK_TIMER_3 = "用户主动打开，开始定时任务" ;


    // 开始充电任务
    public static final String TASK_CHARGING_0 = "无定时任务，开始充电任务" ;
    public static final String TASK_CHARGING_1 = "电量变化，开始充电任务" ;
    public static final String TASK_CHARGING_2 = "收队任务结束并且无定时任务，开始充电任务" ;
    public static final String TASK_CHARGING_3 = "用户主动关闭，开始充电任务" ;


    // 开始收队任务
    public static final String TASK_TEAM_0 = "主动触发，开始收队任务" ;


}
