package com.yongyida.robot.navigation.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.yongyida.robot.navigation.R;
import com.yongyida.robot.util.LogHelper;
import com.yongyida.robot.voice.CloseTeamHelper;

import java.io.IOException;

/**
 * Create By HuangXiangXiang 2018/10/25
 *
 *
 */
public class CloseTeamActivity extends Activity implements MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private static final String TAG = CloseTeamActivity.class.getSimpleName() ;


    private static final String KEY_PATH                = "path" ;
    private static final String KEY_IMAGE_ID            = "imageId" ;

    public static CloseTeamActivity mySelf ;
    private static CloseTeamHelper.CloseTeamListener mCloseTeamListener;


    public static void startActivity(Context context, String path, int imageId, CloseTeamHelper.CloseTeamListener closeTeamListener){

        Intent intent = new Intent(context, CloseTeamActivity.class) ;
        intent.putExtra(KEY_PATH, path) ;
        intent.putExtra(KEY_IMAGE_ID, imageId) ;
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        CloseTeamActivity.mCloseTeamListener = closeTeamListener ;
    }

    private ImageView mTipsIvw;

    private int mImageId ;
    private String mPath ;
    private MediaPlayer mPlayer ;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_close_team);
        initView();

        Intent intent = getIntent() ;
        mImageId = intent.getIntExtra(KEY_IMAGE_ID, R.drawable.close_team_1) ;
        mPath = intent.getStringExtra(KEY_PATH) ;
//        mPath = "voice/fangfang/收队.mp3" ;

        startPlay() ;

        mySelf = this ;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mHandler.removeCallbacks(mRunnable);
        if(mPlayer != null && mPlayer.isPlaying()){

            mPlayer.stop();
        }
        mySelf = null ;
        mCloseTeamListener = null ;
    }

    public void startPlay( String path, int imageId, CloseTeamHelper.CloseTeamListener closeTeamListener){

        mImageId = imageId ;
        mPath = path ;
        mCloseTeamListener = closeTeamListener ;

        startPlay() ;
    }

    private void startPlay() {

        mTipsIvw.setImageResource(mImageId);

        if(mPlayer == null){

            mPlayer = new MediaPlayer() ;

            mPlayer.setOnErrorListener(this);
            mPlayer.setOnCompletionListener(this);

        }

        try {
            AssetFileDescriptor afd = getAssets().openFd(mPath) ;
            mPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset() ,afd.getLength());
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void initView() {

        mTipsIvw = (ImageView) findViewById(R.id.tips_ivw);
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        LogHelper.e(TAG, LogHelper.__TAG__() + "," + Thread.currentThread() );

        mHandler.postDelayed(mRunnable, 3000) ;
    }

    private Handler mHandler = new Handler() ;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {

            if(mPlayer != null && !mPlayer.isPlaying()){

                mPlayer.start();
            }

        }
    } ;

}
