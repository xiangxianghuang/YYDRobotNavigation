package com.yongyida.robot.navigation.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import com.yongyida.robot.navigation.R;
import com.yongyida.robot.util.LogHelper;
import com.yongyida.robot.voice.VideoHelper;

import java.util.Calendar;

/**
 * Create By HuangXiangXiang 2018/9/4
 * 全屏播放视频
 */
public class PlayVideoActivity extends Activity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private static final String TAG = PlayVideoActivity.class.getSimpleName() ;

    private static final String KEY_PATH    = "path" ;
    private static final String KEY_TIMES   = "times" ;

    public static PlayVideoActivity mySelf ;
    private static VideoHelper.PlayVideoListener mPlayVideoListener;


    public static void startActivity(Context context ,String path, int times, VideoHelper.PlayVideoListener playVideoListener){

        Intent intent = new Intent(context, PlayVideoActivity.class) ;
        intent.putExtra(KEY_PATH, path) ;
        intent.putExtra(KEY_TIMES, times) ;
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        PlayVideoActivity.mPlayVideoListener = playVideoListener ;
    }


    private VideoView mVideoVvw;

    private String mPath ;
    private int mTimes ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mySelf = this ;

        setContentView(R.layout.dialog_play_video);

        this.mVideoVvw = findViewById(R.id.video_vvw);
        this.mVideoVvw.setOnPreparedListener(this);
        this.mVideoVvw.setOnCompletionListener(this);
        this.mVideoVvw.setOnErrorListener(this);


        Intent intent = getIntent() ;
        mPath = intent.getStringExtra(KEY_PATH) ;
        mTimes = intent.getIntExtra(KEY_TIMES, 1) ;
        startPlayVideo() ;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mySelf = null ;
        mPlayVideoListener = null ;
    }

    public void startPlayVideo() {

        String uri = "android.resource://" + getPackageName() + "/" + R.raw.test;
        mVideoVvw.setVideoURI(Uri.parse(uri));

//        this.mVideoVvw.setVideoPath(path);
        mVideoVvw.start();
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        LogHelper.i(TAG, LogHelper.__TAG__()+ "times : " + mTimes );

        if(--mTimes > 0){

            mp.start();

        }else {

            if(mPlayVideoListener != null){

                mPlayVideoListener.onEnd();
            }

            finish();
        }

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        LogHelper.i(TAG, LogHelper.__TAG__()+ "what : " + what + "extra : " + extra );

        if(mPlayVideoListener != null){

            mPlayVideoListener.onError();
        }
        finish();

        return false;
    }


}
