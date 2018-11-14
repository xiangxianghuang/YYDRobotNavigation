package com.yongyida.robot.navigation.activity;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gs.gsnavlibrary.api.GsApi;
import com.gs.gsnavlibrary.bean.common.NowPosition;
import com.gs.gsnavlibrary.bean.common.RotateTo;
import com.gs.gsnavlibrary.listener.ResponseListener;
import com.yongyida.robot.navigation.NavigationHelper;
import com.yongyida.robot.navigation.R;
import com.yongyida.robot.util.LogHelper;
import com.yongyida.robot.voice.PlayerHelper;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Create By HuangXiangXiang 2018/9/4
 */
public class TestActivity extends Activity {

    private static final String TAG = TestActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);
    }


    private MediaPlayer mPlayer ;
    public void playSurround(View view){
//        if(mPlayer == null){
//            mPlayer = new MediaPlayer() ;
//            try {
//                AssetFileDescriptor afd = getAssets().openFd("surround/借过一下.mp3") ;
//                mPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset() ,afd.getLength());
//                mPlayer.prepare();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        if(!mPlayer.isPlaying()){
//
//            mPlayer.start();
//        }

        boolean isCover = true ;
        Context context = this ;
        String assetFileName = "surround/借过一下.mp3" ;
        int times = -1 ;
        int delay = 1000 ;
        PlayerHelper.PlayerListener playerListener = null ;

        PlayerHelper.getInstance().startPlayAsset(isCover,context,assetFileName,times,delay,playerListener);
    }


}
