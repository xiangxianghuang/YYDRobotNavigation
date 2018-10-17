package com.yongyida.robot.navigation.bean;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Create By HuangXiangXiang 2018/8/23
 */
public class PathStatus {

    public int statusCode ;
    public StatusData statusData ;
    public String statusMsg ;

    public class StatusData{

         public String graphName ;
         public int index ;
         public String nextPoint ;
         public String passedPoints ;
         public String pathName ;
         public float remainingMilleage ;
         public float remainingTime ;
         public float totalMilleage ;
         public float totalTime ;
         public int type ;

         // 经过的点
        private ArrayList<String> passedPointsList ;

        public ArrayList<String> getPassedPointsList() {

            if(passedPointsList == null){

                passedPointsList = new ArrayList<>() ;

                if(!TextUtils.isEmpty(passedPoints)){

                    try {
                        JSONArray array = new JSONArray(passedPoints) ;
                        final int size = array.length() ;
                        for (int i = 0 ; i < size ; i ++){

                            passedPointsList.add(array.get(i).toString()) ;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            return passedPointsList;
        }
    }


    @Override
    public String toString() {

        if (statusData == null){

            return "statusData 为空";
        }else {

            if(!statusData.getPassedPointsList().isEmpty()){

                // 到达某个点
                return " 到达" + statusData.getPassedPointsList().get(0) ;

            }else{

                // 前往某个点
                return "前往" +  statusData.nextPoint ;
            }

        }

    }
}
