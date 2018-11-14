package com.yongyida.robot.voice;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;

import com.yongyida.robot.util.LogHelper;

import java.io.IOException;

/**
 * Create By HuangXiangXiang 2018/11/8
 * 播放指定次数
 * 播放无限次数
 * 单独播放音频
 */
public class PlayerHelper implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {

    private static final String TAG = PlayerHelper.class.getSimpleName() ;

    private static PlayerHelper mInstance ;
    public static PlayerHelper getInstance(){

        if(mInstance == null) {
            mInstance = new PlayerHelper() ;
        }
        return mInstance ;
    }

    private PlayerHelper(){
    }

    public interface PlayerListener{

        void onEnd() ;

        void onError() ;
    }

    private MediaPlayer mPlayer ;
    private PlayerListener mPlayerListener ;

    private Handler mHandler = new Handler() ;
    private Runnable mDelayRunnable = new Runnable() {
        @Override
        public void run() {

            if(mPlayer != null){

                mPlayer.start();
            }
        }
    } ;

    private int mTimes ;
    private int mDelay ;

    /**
     * 播放普通资源
     * 在线URL 或者 本地资源
     * */
    public void startPlay(boolean isCover, String path , final int times, int delay, PlayerListener playerListener ){

        if(!isCover){

            if(mPlayer!= null && mPlayer.isPlaying()){

                return;
            }
        }

        resetPlay() ;
        this.mTimes = times ;
        this.mDelay = delay ;
        this.mPlayerListener = playerListener ;
        if(mPlayer != null){

            try {
                mPlayer.setDataSource(path);
                mPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void startPlayAsset( Context context, String assetFileName){

        startPlayAsset(true, context, assetFileName, -1, 3000,null) ;
    }
    /**
     * 播放Assert
     * */
    public void startPlayAsset(boolean isCover, Context context, String assetFileName, int times, int delay,PlayerListener playerListener){

        if(!isCover){

            if(mPlayer!= null && mPlayer.isPlaying()){

                return;
            }
        }

        resetPlay() ;
        this.mTimes = times ;
        this.mDelay = delay ;
        this.mPlayerListener = playerListener ;
        if(mPlayer != null){

            try {
                AssetFileDescriptor afd = context.getAssets().openFd(assetFileName);
                mPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset() ,afd.getLength());
                mPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private void resetPlay(){

        mHandler.removeCallbacks(mDelayRunnable);
        if(mPlayer == null){

            mPlayer = new MediaPlayer() ;
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnErrorListener(this);
            mPlayer.setOnPreparedListener(this);
        }else{

            if(mPlayer.isPlaying()){
                mPlayer.stop();
            }
            mPlayer.reset();
        }
    }

    /**停止播放*/
    public void stopPlay() {

        this.mPlayerListener = null ;
        this.mHandler.removeCallbacks(mDelayRunnable);

        if(mPlayer != null && mPlayer.isPlaying()){

            mPlayer.stop();
        }
    }


    @Override
    public void onPrepared(MediaPlayer mp) {

        mp.start() ;
    }


    @Override
    public void onCompletion(MediaPlayer mp) {

        if(mTimes < 0 ){
            LogHelper.e(TAG, LogHelper.__TAG__() + ",mTimes : " + mTimes);

            if(mDelay <= 0 ){

                mp.start();

            }else {

                mHandler.postDelayed(mDelayRunnable, mDelay) ;
            }

        }else if(--mTimes <= 0 ){

            LogHelper.e(TAG, LogHelper.__TAG__() + ",mTimes : " + mTimes);

            if(mPlayerListener != null){

                mPlayerListener.onEnd();
            }

        }else {

            LogHelper.e(TAG, LogHelper.__TAG__() + ",mTimes : " + mTimes);

            if(mDelay <= 0 ){

                mp.start();

            }else {

                mHandler.postDelayed(mDelayRunnable, mDelay) ;
            }
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        LogHelper.e(TAG, LogHelper.__TAG__() + ",what : " + what + ",extra : " + extra);

        if(mPlayerListener != null){

            mPlayerListener.onError();
        }
        return false;
    }



}
