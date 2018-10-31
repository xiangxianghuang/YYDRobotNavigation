package com.yongyida.robot.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Java实现类似C/C++中的__FILE__、__FUNC__、__LINE__,主要用于日志等功能中
 *
 * @version 1.0 2011-07-13
 *
 */
public class LogHelper {
    /**
     * 打印日志时获取当前的程序文件名,行号,方法名 输出格式为：[FileName | LineNumber | MethodName]
     */
    public static boolean debug = true;

    public static void v(String tag, String info) {
        if (debug) {
            Log.v(tag, info);
        }

        saveSdcardFile("v", tag, info) ;
    }

    public static void i(String tag, String info) {
        if (debug) {
            Log.i(tag, info);
        }
        saveSdcardFile("i", tag, info) ;
    }

    public static void e(String tag, String info) {
        if (debug) {
            Log.e(tag, info);
        }
        saveSdcardFile("e", tag, info) ;
    }

    public static void e(String tag, String info, Throwable tr) {
        if (debug) {
            Log.e(tag, info, tr);
        }
        saveSdcardFile("e", tag, info) ;
    }


    public static void w(String tag, String info) {
        if (debug) {
            Log.w(tag, info);
        }
        saveSdcardFile("w", tag, info) ;
    }



    public static String __TAG__() {
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
        StringBuffer toStringBuffer = new StringBuffer("[").append(traceElement.getFileName()).append(" | ")
                .append(traceElement.getLineNumber()).append(" | ").append(traceElement.getMethodName()).append("]");
        return toStringBuffer.toString() + "[*]";
    }

    // 当前文件名
    public static String __FILE__() {
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
        return traceElement.getFileName();
    }

    // 当前方法名
    public static String __FUNC__() {
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
        return traceElement.getMethodName();
    }

    // 当前行号
    public static int __LINE__() {
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
        return traceElement.getLineNumber();
    }

    // 当前时间
    public static String __TIME__() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sdf.format(now);
    }

    // 以下将文件保存本地
    private static boolean isSaveFile = false ;
    private static String packageName ;

    public static void enableSaveFile(Context context){

        packageName = context.getPackageName() ;
        isSaveFile = true ;
    }


    public static void disEnableSaveFile(){

        isSaveFile = false ;

    }

    /**
     *
     * 保存本地
     * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
     * */
    private static boolean saveSdcardFile(String level, String tag, String info) {

        if (isSaveFile) {

            String root = String.format(Locale.CHINA,"log/%s/%s" ,packageName,tag) ;
            File rootFile = new File(Environment.getExternalStorageDirectory(),root) ;
            if(rootFile.exists() || !rootFile.mkdirs()){

                try {

                    File file = new File(rootFile,level+".log") ;

                    FileWriter writer = new FileWriter(file, true) ;
                    String text = __TIME__() + "  " + info + "\n" ;
                    writer.write(text);
                    writer.flush();
                    writer.close();

                    Log.i("hxx" , "text : " + text);

                    return true ;

                } catch (IOException e) {
                    e.printStackTrace();

                    Log.e("hxx" , "IOException : " + e.getMessage() );
                }
            }

            return saveDataFile(level, tag, info) ;
        }
        return true ;
    }


    private static boolean saveDataFile(String level, String tag, String info) {

        String root = String.format(Locale.CHINA,"log/%s" ,tag) ;

        File rootFile = new File("/data/data/" + packageName,root) ;
        if(rootFile.exists() || !rootFile.mkdirs()){

            try {

                File file = new File(rootFile,level+".log") ;

                FileWriter writer = new FileWriter(file, true) ;
                String text = __TIME__() + "  " + info + "\n" ;
                writer.write(text);
                writer.flush();
                writer.close();

                Log.i("hxx" , "text : " + text);

                return true ;
            } catch (IOException e) {
                e.printStackTrace();

                Log.e("hxx" , "IOException : " + e.getMessage() );
            }
        }
        return false ;
    }

}