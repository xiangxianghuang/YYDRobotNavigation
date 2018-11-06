package com.yongyida.robot.util;

import android.content.Context;
import android.content.Intent;

import com.yongyida.robot.json.Base;
import com.yongyida.robot.json.TransferData;

/**
 * Create By HuangXiangXiang 2018/9/5
 * 主要是与主服务相关的信息
 */
public class MainServiceInfo {

    private static final String TAG = MainServiceInfo.class.getSimpleName() ;

    // 业务服务接收的动作
    private static final String ACTION_BSN_SERVICE      = "com.yongyida.robot.BUSINESS";
    //接收的包名
    private static final String PACKAGE_BSN_SERVICE      = "com.yongyida.robot.business";
    // 是什么功能
    public static final String KEY_FUNCTION            = "function";
    // 携带参数
    public static final String KEY_RESULT              = "result";
    // 来自何处
    public static final String KEY_FROM                = "from";
    // 携带命令
    public static final String KEY_CMD                 = "cmd";
    // 携带参数
    public static final String KEY_ARG1                = "arg1";
    // 携带参数
    public static final String KEY_ARG2                = "arg2";
    // 携带信息
    public static final String KEY_MSG                 = "msg";
    // cmd + msg 的形式进行通信
    public static final String FUNC_CMD_MSG            = "CMD_MSG";


    /*----------------------------------【成语接龙】----------------------------------------------*/
    // 来自成语接龙的请求
    public static final int RECV_BSN_IDIOM_CMD = 1002;
    // 参数1：退出成语接龙模式【0表示】
    public static final int RECV_IDIOM_ARG1_STOP = 0;
    // 参数1：进入成语接龙模式【1表示】
    public static final int RECV_IDIOM_ARG1_START = 1;
    // 参数1：成语接龙提示【2表示】
    public static final int RECV_IDIOM_ARG1_REMIND = 2;
    // 参数1：成语接龙不会【3表示】
    public static final int RECV_IDIOM_ARG1_UNKNOWN = 3;
    // 参数1：成语接龙不想玩了【4表示】
    public static final int RECV_IDIOM_ARG1_QUIT = 4;


    /*---------------------------------【跳舞】---------------------------------------------------*/
    // 来自Dance的请求
    public static final int RECV_BSN_DANCE_CMD = 5000;
    // 参数1：退出跳舞模式【0表示】
    public static final int DANCE_ARG1_STOP = 0;
    // 参数1：进入跳舞模式【1表示】
    public static final int DANCE_ARG1_START = 1;

    // 发送给Dance的请求
    public static final int SEND_BSN_DANCE_CMD = 5001;
    // 参数1：退出跳舞模式【0表示】
    public static final int SEND_DANCE_ARG1_STOP = 0;
    // 参数1：进入跳舞模式【1表示】
    public static final int SEND_DANCE_ARG1_START = 1;
    // 参数1：暂停跳舞【2表示】
    public static final int SEND_DANCE_ARG1_PAUSE = 2;
    // 参数1：恢复跳舞【3表示】
    public static final int SEND_DANCE_ARG1_RESUME = 3;



    /*------------------------------------【写字】------------------------------------------------*/
    // 来自写字的请求
    public static final int RECV_BSN_CHARACTER_CMD = 5002;
    // 参数1：退出写字模式【0表示】
    public static final int RECV_CHARACTER_ARG1_STOP = 0;
    // 参数1：进入写字模式【1表示】
    public static final int RECV_CHARACTER_ARG1_START = 1;

    // 发送给写字的请求
    public static final int SEND_BSN_CHARACTER_CMD = 5003;



    /*----------------------------------【知识问答】----------------------------------------------*/
    // 来自知识问答的请求
    public static final int RECV_BSN_KNOWLEDGE_CMD = 5004;
    // 参数1：退出知识问答模式【0表示】
    public static final int RECV_KNOWLEDGE_ARG1_STOP = 0;
    // 参数1：进入知识问答模式【1表示】
    public static final int RECV_KNOWLEDGE_ARG1_START = 1;

    // 发送给知识问答的请求
    public static final int SEND_BSN_KNOWLEDGE_CMD = 5005;



    /*------------------------------------【诗词】------------------------------------------------*/
    // 来自诗词的请求
    public static final int RECV_BSN_POETRY_CMD = 5006;
    // 参数1：退出诗词模式【0表示】
    public static final int RECV_POETRY_ARG1_STOP = 0;
    // 参数1：进入诗词模式【1表示】
    public static final int RECV_POETRY_ARG1_START = 1;

    // 发送诗词的请求
    public static final int SEND_BSN_POETRY_CMD = 5007;



    /*------------------------------------【天气】------------------------------------------------*/
    // 来自天气的请求
    public static final int RECV_BSN_WEATHER_CMD = 5008;
    // 参数1：退出天气模式【0表示】
    public static final int RECV_WEATHER_ARG1_STOP = 0;
    // 参数1：进入天气模式【1表示】
    public static final int RECV_WEATHER_ARG1_START = 1;

    // 发送给天气的请求
    public static final int SEND_BSN_WEATHER_CMD = 5009;


    /*---------------------------------【关机与重启】---------------------------------------------*/
    // 来自【关机与重启】的请求
    public static final int RECV_BSN_SHUTDOWN_REBOOT_CMD = 5010;
    // 参数1：查询功能【3表示】
    public static final int RECV_SHUTDOWN_REBOOT_ARG1_QUERY = 3;
    // 参数2：关机功能【1表示】
    public static final int RECV_SHUTDOWN_REBOOT_ARG2_SHUTDOWN = 1;
    // 参数2：重启功能【2表示】
    public static final int RECV_SHUTDOWN_REBOOT_ARG2_REBOOT = 2;
    // 参数3：代表时间【单位：秒】

    // 发送给【关机与重启】的请求
    public static final int SEND_BSN_SHUTDOWN_REBOOT_CMD = 5011;
    // 参数1：设置功能【1表示】
    public static final int SEND_SHUTDOWN_REBOOT_ARG1_SETTING = 1;
    // 参数1：取消功能【2表示】
    public static final int SEND_SHUTDOWN_REBOOT_ARG1_CANCEL = 2;
    // 参数1：查询功能【3表示】
    public static final int SEND_SHUTDOWN_REBOOT_ARG1_QUERY = 3;

    // 参数2：关机功能【1表示】
    public static final int SEND_SHUTDOWN_REBOOT_ARG2_SHUTDOWN = 1;
    // 参数2：重启功能【2表示】
    public static final int SEND_SHUTDOWN_REBOOT_ARG2_REBOOT = 2;

    // 参数3：代表时间【单位：秒】


    /*------------------------------------【天气】------------------------------------------------*/
    // 来自机器人收队的请求
    public static final int RECV_BSN_CLOSE_TEAM_CMD = 5012;

    // 发给机器人收队的请求
    public static final int SEND_BSN_CLOSE_TEAM_CMD = 5013;


    /**
     * 向主服务发送Service信息
     * */
    public static void notifyMainService(Context context, int cmd, int arg1){

        LogHelper.i(TAG, LogHelper.__TAG__() + ",cmd : " + cmd + ", arg1 : " + arg1 ) ;

        Intent intent = new Intent(ACTION_BSN_SERVICE);
        intent.setPackage(PACKAGE_BSN_SERVICE);
        intent.putExtra(KEY_FROM, context.getPackageName());
        intent.putExtra(KEY_FUNCTION, FUNC_CMD_MSG);
        intent.putExtra(KEY_CMD, cmd);
        intent.putExtra(KEY_ARG1, arg1);
        context.startService(intent);
    }

    /************************【开始和结束】适用于《全部》功能************************/
    /**
     * 向主服务发送Service信息（【开始功能】）
     * */
    public static void notifyMainServiceStart(Context context, int functionType){

        notifyMainService(context, functionType, DANCE_ARG1_START) ;  //开始发送1
    }

    /**
     * 向主服务发送Service信息（【结束功能】）
     * */
    public static void notifyMainServiceStop(Context context,int functionType){

        notifyMainService(context, functionType, DANCE_ARG1_STOP) ;   //结束发送0
    }


    /************************【提示和不会】适用于《成语接龙》功能************************/
    /**
     * 成语接龙提示（向主服务发送Service信息）
     * */
    public static void notifyMainServiceIdiomRemind(Context context){

        notifyMainService(context, RECV_BSN_IDIOM_CMD, RECV_IDIOM_ARG1_REMIND) ;
    }

    /**
     * 成语接龙不会（向主服务发送Service信息）
     * */
    public static void notifyMainServiceIdiomUnknown(Context context){

        notifyMainService(context, RECV_BSN_IDIOM_CMD, RECV_IDIOM_ARG1_UNKNOWN) ;
    }

    /**
     * 退出成语接龙（向主服务发送Service信息）
     * */
    public static void notifyMainServiceIdiomQuit(Context context){

        notifyMainService(context, RECV_BSN_IDIOM_CMD, RECV_IDIOM_ARG1_QUIT) ;
    }


//    /************************【暂停和恢复】适用于《跳舞》功能************************/
//    /**
//     * 向主服务发送Service信息（《跳舞》【暂停】）
//     * */
//    public static void notifyMainServiceDancePause(Context context){
//
//        notifyMainService(context, RECV_BSN_DANCE_CMD, SEND_DANCE_ARG1_PAUSE) ;
//    }
//
//    /**
//     * 向主服务发送Service信息（《跳舞》【恢复】）
//     * */
//    public static void notifyMainServiceDanceResume(Context context){
//
//        notifyMainService(context, RECV_BSN_DANCE_CMD, SEND_DANCE_ARG1_RESUME) ;
//    }
//


        // 开启监听功能
         private static final String FUNC_START_LISTENING    = "START_LISTENING";
        // 携带这个表示直接监听，否则还要说一些反应语
        private static final String RESULT_DIRECT_LISTEN    = "DIRECT_LISTEN";

        /**
         * 开启循环监听
         * */
        public static void startDirectListen(Context context){

            startFunction(context, FUNC_START_LISTENING, RESULT_DIRECT_LISTEN) ;
        }

    private static void startFunction(Context context, String function, String result){

        Intent service = new Intent(ACTION_BSN_SERVICE) ;
        service.setClassName("com.yongyida.robot.business",
                "com.yongyida.robot.brain.system.MainService" ) ;
        service.putExtra(KEY_FUNCTION, function) ;
        service.putExtra(KEY_RESULT, result) ;

        context.startService(service) ;
    }


    public static void response(Context context, Base base){

        String json = TransferData.packToString(base) ;

        Intent intent = new Intent(ACTION_BSN_SERVICE);
        intent.setPackage(PACKAGE_BSN_SERVICE);
        intent.putExtra(KEY_FROM, context.getPackageName());
        intent.putExtra(KEY_FUNCTION, FUNC_CMD_MSG);
        intent.putExtra(KEY_CMD, RECV_BSN_CLOSE_TEAM_CMD);
        intent.putExtra(KEY_MSG, json);

        context.startService(intent);
    }


}
