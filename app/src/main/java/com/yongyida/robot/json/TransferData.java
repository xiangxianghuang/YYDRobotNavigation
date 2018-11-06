package com.yongyida.robot.json;

import android.util.SparseArray;

import com.google.gson.Gson;
import com.yongyida.robot.json.request.RequestCancelCloseTeam;
import com.yongyida.robot.json.request.RequestCloseTeam;
import com.yongyida.robot.json.request.RequestCloseTeamNames;
import com.yongyida.robot.json.request.RequestRobotInfo;
import com.yongyida.robot.json.response.ResponseCancelCloseTeam;
import com.yongyida.robot.json.response.ResponseCloseTeam;
import com.yongyida.robot.json.response.ResponseCloseTeamNames;
import com.yongyida.robot.json.response.ResponseRobotInfo;

/**
 * Create By HuangXiangXiang 2018/10/26
 * 发送给手机
 * 本APP端（经主服务）发送给手机
 * 1、返回机器状态
 * 2、返回请求收队
 *
 * 手机（经主服务）发送给本APP端
 * 1、请求机器底盘信息
 * 2、请求收队
 */
public class TransferData {

    /**
     * 二级指令
     * */
    private String cmd  = "close_team";

    /**数据类型*/
    private int type  ;

    /**json数据*/
    private String jsonData ;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }


    /***********************************数据的一些拆封和包装***********************************/
    private static final Gson GSON = new Gson() ;
    private static final SparseArray<Class<? extends Base>> BASES = new SparseArray<>() ;
    {
        BASES.put(Base.TYPE_REQUEST_ROBOT_INFO, RequestRobotInfo.class);
        BASES.put(Base.TYPE_REQUEST_CLOSE_TEAM, RequestCloseTeam.class);
        BASES.put(Base.TYPE_REQUEST_CANCEL_CLOSE_TEAM, RequestCancelCloseTeam.class);
        BASES.put(Base.TYPE_REQUEST_CLOSE_TEAM_NAMES, RequestCloseTeamNames.class);

        BASES.put(Base.TYPE_RESPONSE_ROBOT_INFO, ResponseRobotInfo.class);
        BASES.put(Base.TYPE_RESPONSE_CLOSE_TEAM, ResponseCloseTeam.class);
        BASES.put(Base.TYPE_RESPONSE_CANCEL_CLOSE_TEAM, ResponseCancelCloseTeam.class);
        BASES.put(Base.TYPE_RESPONSE_CLOSE_TEAM_NAMES, ResponseCloseTeamNames.class);
    }


    /**
     * 包装成字符串
     * */
    public static String packToString(Base base){

        TransferData transferData = pack(base) ;
        return  GSON.toJson(transferData) ;
    }


    /**
     * 包装
     * */
    public static TransferData pack(Base base){

        TransferData transferData = new TransferData() ;

        transferData.type = base.getType() ;
        transferData.jsonData = GSON.toJson(base) ;

        return transferData ;
    }

    /**
     * 拆封
     * */
    public static Base unpack(TransferData transferData){

        int type = transferData.type ;
        Class<? extends Base> clazz = BASES.get(type) ;

        if(clazz != null){

            String jsonData = transferData.jsonData ;
            return GSON.fromJson(jsonData, clazz) ;
        }
        return null ;
    }

    /**
     * 拆封(从字符串开始拆封)
     * */
    public static Base unpack(String data){

        TransferData transferData = GSON.fromJson(data, TransferData.class);
        return unpack(transferData) ;
    }

}
