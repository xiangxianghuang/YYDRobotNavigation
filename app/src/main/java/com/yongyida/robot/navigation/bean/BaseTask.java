package com.yongyida.robot.navigation.bean;

/**
 * Create By HuangXiangXiang 2018/10/18
 * 基本任务
 */
public abstract class BaseTask {


    /**
     * 获取任务类型
     * */
    public abstract TaskType getTaskType() ;


    /**
     * 任务类型
     * 任务优先级别依次减小
     * */
    public enum TaskType{

        TEAM,           // 收队任务
        CHARGING,       // 充电任务
        TIMER,          // 定时任务
    }


}
