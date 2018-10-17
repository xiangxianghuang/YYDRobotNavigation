package com.yongyida.robot.voice;

import android.content.Context;

import com.yongyida.robot.navigation.activity.PlayVideoActivity;

/**
 * Create By HuangXiangXiang 2018/9/4
 */
public class VideoHelper {

    private static VideoHelper mInstance ;

    public static VideoHelper getInstance(Context context){

        if(mInstance == null) {
            mInstance = new VideoHelper(context.getApplicationContext()) ;
        }
        return mInstance ;
    }

    private Context context ;

    private VideoHelper(Context context) {

        this.context = context ;
    }


    public interface PlayVideoListener{

        void onEnd() ;

        void onError() ;

    }


    public void startPlayVideo(String type , PlayVideoListener playVideoListener){

        startPlayVideo(type,1, playVideoListener);
    }

    public void startPlayVideo(String type , final int times, PlayVideoListener playVideoListener){

        PlayVideoActivity.startActivity(context, type, times, playVideoListener);
    }

    public void stopPlayVideo(){

        if(PlayVideoActivity.mySelf != null){

            PlayVideoActivity.mySelf.finish();
        }

    }

}
