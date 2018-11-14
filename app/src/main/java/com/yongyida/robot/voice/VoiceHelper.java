package com.yongyida.robot.voice;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Handler;

import com.yongyida.robot.data.MediaData;

import java.io.IOException;

/**
 * Create By HuangXiangXiang 2018/8/22
 *
 * 发音人帮助
 */
public class VoiceHelper {

    private static VoiceHelper mInstance ;
    public static VoiceHelper getInstance(Context context){

        if(mInstance == null) {
            mInstance = new VoiceHelper(context.getApplicationContext()) ;
        }
        return mInstance ;
    }


    //发音的语料
    private MediaData.Voice voice = MediaData.FANG_FANG;

    private Context context ;
    private VoiceHelper(Context context){

        this.context = context ;
    }


    public void setVoice(MediaData.Voice voice) {
        this.voice = voice;
    }

    public MediaData.Voice getVoice() {
        return voice;
    }

    public interface SpeakListener{

        void onEnd() ;

        void onError() ;

    }

    private MediaPlayer mediaPlayer ;
    private SpeakListener speakListener ;


    public void startSpeak(String type , SpeakListener speakListener){

        startSpeak(type,1, speakListener);
    }

    private Handler mHandler = new Handler() ;
    private Runnable mDelayRunnable = new Runnable() {
        @Override
        public void run() {

            if(mediaPlayer != null){

                mediaPlayer.start();
            }

        }
    } ;

    private int mTimes ;
    public void startSpeak(String type , final int times, SpeakListener speakListener){

        mHandler.removeCallbacks(mDelayRunnable);

        this.mTimes = times ;
        this.speakListener = speakListener ;

        if(mediaPlayer == null){

            mediaPlayer = new MediaPlayer() ;
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    if(--mTimes > 0) {

//                        mp.start();

                        mHandler.postDelayed(mDelayRunnable, 2 * 1000) ;


                    }else {

                        if(VoiceHelper.this.speakListener != null){

                            VoiceHelper.this.speakListener.onEnd();
                        }
                    }

                }
            });

            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {

                    if(VoiceHelper.this.speakListener != null){

                        VoiceHelper.this.speakListener.onError();
                    }

                    return false;
                }
            });

        }else {

            mediaPlayer.reset();
        }

        AssetManager assetManager = context.getAssets() ;

        try {
            String word = voice.getRandomPath(type) ;

            AssetFileDescriptor afd = assetManager.openFd("voice/" + voice.speaker +"/" + word + ".mp3") ;
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset() ,afd.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopSpeak() {

        this.speakListener = null ;
        if(mediaPlayer != null && mediaPlayer.isPlaying()){

            mediaPlayer.stop();
        }
    }



}
