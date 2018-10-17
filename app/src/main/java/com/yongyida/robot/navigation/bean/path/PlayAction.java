package com.yongyida.robot.navigation.bean.path;

/**
 * Create By HuangXiangXiang 2018/8/28
 * 播放动作
 */
public class PlayAction extends BaseAction {

    private MediaType mediaType = MediaType.WORD;

    // 播放的音频
    private String name ;
    // 播发次数
    private int times = 1;


    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    @Override
    public Type getType() {

        return Type.PLAY;
    }


    // 媒体类型
    public enum MediaType{

        WORD("文字") ,    // 文字
        VIDEO("视频") ;   // 视频

        public String name ;
        MediaType(String name){

            this.name = name ;
        }
    }


}
