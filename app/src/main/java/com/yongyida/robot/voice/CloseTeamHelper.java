package com.yongyida.robot.voice;

import android.content.Context;
import android.content.Intent;

import com.yongyida.robot.navigation.activity.CloseTeamActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Create By HuangXiangXiang 2018/10/25
 */
public class CloseTeamHelper {

    private static CloseTeamHelper mInstance ;

    public static CloseTeamHelper getInstance(Context context){

        if(mInstance == null) {
            mInstance = new CloseTeamHelper(context.getApplicationContext()) ;
        }
        return mInstance ;
    }

    private Context context ;


    private HashMap<String, CloseTeamData> mCloseTeamDatas = getCloseTeamData() ;




    private CloseTeamHelper(Context context) {

        this.context = context ;
    }

    private HashMap<String, CloseTeamData> getCloseTeamData(){

        HashMap<String, CloseTeamData> closeTeamDatas = new HashMap<>() ;

        for (int i = 5; i <=34 ; i++) {

            String name = "close_team_" + i ;
            String audioName = name + ".mp4" ;

            int id = context.getResources().getIdentifier(name,"drawable", context.getPackageName()) ;
            CloseTeamData closeTeamData = new CloseTeamData(audioName, id);

            closeTeamDatas.put(String.valueOf(i), closeTeamData) ;
        }

        return closeTeamDatas;
    }

    private CloseTeamData getCloseTeamData(String teamName){

        CloseTeamData closeTeamData = null;

        for (Map.Entry<String, CloseTeamData> entry: mCloseTeamDatas.entrySet()) {

            if(closeTeamData == null){

                closeTeamData = entry.getValue() ;
            }

            String key = entry.getKey() ;
            if(teamName.equals(key)){

                return entry.getValue() ;
            }
        }

        return closeTeamData ;
    }


    public interface CloseTeamListener{

        void onEnd() ;

        void onError() ;
    }


    public void startCloseTeam(String teamName, CloseTeamListener closeTeamListener){


        CloseTeamData closeTeamData = getCloseTeamData(teamName) ;
        if(CloseTeamActivity.mySelf == null || CloseTeamActivity.mySelf.isFinishing()) {

            CloseTeamActivity.startActivity(context, "close_team/" + closeTeamData.audioName, closeTeamData.imageId, closeTeamListener);
        }else {

            CloseTeamActivity.mySelf.startPlay("close_team/" + closeTeamData.audioName, closeTeamData.imageId, closeTeamListener);
        }
    }

//    public void startCloseTeam(String path , int imageId, CloseTeamListener closeTeamListener){
//
//        if(CloseTeamActivity.mySelf == null || CloseTeamActivity.mySelf.isFinishing()) {
//
//            CloseTeamActivity.startActivity(context, path, imageId, closeTeamListener);
//        }else {
//
//            CloseTeamActivity.mySelf.startPlay(path, imageId, closeTeamListener);
//        }
//    }
//

    public void stopCloseTeam(){

        if(CloseTeamActivity.mySelf != null && !CloseTeamActivity.mySelf.isFinishing()){

            CloseTeamActivity.mySelf.finish();
        }

    }


    /**
     * 收队信息
     * */
    private class CloseTeamData{

        private String audioName ;
        private int imageId ;

        CloseTeamData(String audioName, int imageId){

            this.audioName = audioName ;
            this.imageId = imageId ;
        }

    }

}
