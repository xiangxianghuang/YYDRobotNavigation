package com.yongyida.robot.navigation.bean.path;

/**
 * Create By HuangXiangXiang 2018/8/28
 * 停顿动作
 */
public class StopAction extends BaseAction {

    private final Turn turn = new Turn();
    private final End end = new End();

    public Turn getTurn() {
        return turn;
    }

    public End getEnd() {
        return end;
    }

    @Override
    public Type getType() {

        return Type.STOP;
    }

    /**
     * 转向
     * */
    public static class Turn{

        public enum Type{

            NO_TURN("不转向"),
            TURN("转向");

            public String name ;
            Type(String name){

                this.name = name ;
            }

        }

        private Type type ;

        private int angle = 0  ;

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public int getAngle() {
            return angle;
        }

        public void setAngle(int angle) {
            this.angle = angle;
        }
    }

    /**
     * 停止结束
     * */
    public static class End{

        public enum Type{

            PLAY("媒体播放结束"),  // 播放语音
            TIME("时间");         // 时间

            public String name ;
            Type(String name){

                this.name = name ;
            }

        }

        private Type type = Type.PLAY ;
        private int milliSecond = 1000;  // 毫秒

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public int getMilliSecond() {
            return milliSecond;
        }

        public void setMilliSecond(int milliSecond) {
            this.milliSecond = milliSecond;
        }
    }


}
