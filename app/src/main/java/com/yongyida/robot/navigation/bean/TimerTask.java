package com.yongyida.robot.navigation.bean;

/**
 * Create By HuangXiangXiang 2018/10/18
 * 定时任务
 */
public class TimerTask extends BaseTask {


    /**
     * 起始小时(0-23)
     * */
    private int startHour = 12;
    /**
     * 起始分钟(0-59)
     * */
    private int startMinute = 0;

    /**
     * 结束小时(0-23)
     * */
    private int endHour = 13;
    /**
     * 结束分钟(0-59)
     * */
    private int endMinute = 30;

    /**
     * 路线任务
     */
    private PathInfo pathInfo ;

    /**是否停止*/
    private boolean isStop = false ;



    public PathInfo getPathInfo() {
        return pathInfo;
    }

    public void setPathInfo(PathInfo pathInfo) {
        this.pathInfo = pathInfo;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {

        if(startHour < 0){

            this.startHour = 0 ;

        }else if(startHour > 23){

            this.startHour = 23 ;

        }else {

            this.startHour = startHour;
        }

    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {

        if(startMinute < 0){

            this.startMinute = 0 ;

        }else if(startMinute > 59){

            this.startMinute = 59 ;

        }else {

            this.startMinute = startMinute;
        }
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {

        if(endHour < 0){

            this.endHour = 0 ;

        }else if(endHour > 23){

            this.endHour = 23 ;

        }else {

            this.endHour = endHour;
        }

    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {

        if(endMinute < 0){

            this.endMinute = 0 ;

        }else if(endMinute > 59){

            this.endMinute = 59 ;

        }else {

            this.endMinute = endMinute;
        }
    }



    @Override
    public TaskType getTaskType() {
        return TaskType.TIMER;
    }


    /**
     * 比较2个时间
     * @return
     * 1    第一个时间大
     * 0    两个时间相同
     * -1   第二个时间大
     *
     * */
    public static int equalTime(int hour1, int minute1,int hour2, int minute2){

        if(hour1 > hour2){

            return 1 ;
        }else if(hour1 < hour2){

            return -1 ;
        }else {

            if(minute1 > minute2){

                return 1 ;
            }else if(minute1 < minute2){

                return -1 ;
            }else {

                return 0 ;
            }
        }
    }

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }
}
