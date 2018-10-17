package com.yongyida.robot.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Create By HuangXiangXiang 2018/8/30
 * 音频资源
 */
public class MediaData {

    // 说话内容
    public static final String TYPE_PICK_UP_LIGHTER             = "收取打火机" ;
    // 人流引导
    public static final String TYPE_HUMAN_FLOW_GUIDANCE         = "人流引导" ;
    // 人群内播报
    public static final String TYPE_BROADCAST_IN_CROWD          = "人群内播报" ;
    // Lady first
    public static final String TYPE_LADY_FIRST                  = "Lady first" ;
    // VIP
    public static final String TYPE_VIP                         = "VIP" ;
    // 原地旋转
    public static final String TYPE_PIROUETTE                   = "原地旋转" ;
    // 违禁物品
    public static final String TYPE_CONTRABAND                  = "违禁物品" ;

    public static final String TYPE_HELLO                       = "大家好" ;

    public static final String TYPE_IN_COME                     = "大家请往里走" ;

    public static final String TYPE_JIA                         = "贾" ;




    public static final ArrayList<String> wordTypes = new ArrayList<>() ;
    static {

        wordTypes.add(TYPE_PICK_UP_LIGHTER) ;
        wordTypes.add(TYPE_HUMAN_FLOW_GUIDANCE) ;
        wordTypes.add(TYPE_BROADCAST_IN_CROWD) ;
        wordTypes.add(TYPE_LADY_FIRST) ;
        wordTypes.add(TYPE_VIP) ;
        wordTypes.add(TYPE_PIROUETTE) ;
        wordTypes.add(TYPE_CONTRABAND) ;

        wordTypes.add(TYPE_HELLO) ;
        wordTypes.add(TYPE_IN_COME) ;
        wordTypes.add(TYPE_JIA) ;
    }


    /**
     * 语料
     * */
    public static class Voice{

        public final String speaker ;
        private final HashMap<String , ArrayList<String>> wordMap = new HashMap<>();

        Voice(String speaker){
            this.speaker = speaker ;
        }


        void addWord(String type, String word){

            ArrayList<String> words = wordMap.get(type) ;
            if(words == null){

                words = new ArrayList<>() ;
                wordMap.put(type, words) ;
            }
            words.add(word) ;
        }

        public String getRandomPath(String type){

            ArrayList<String> words = wordMap.get(type) ;
            final int size = words.size() ;
            if(size == 1 ){

                return words.get(0) ;
            }

            int index = new Random().nextInt(size) ;
            return words.get(index) ;

        }

    }

    public static final Voice FANG_FANG ;
    public static final Voice XIAO_GUANG;
    static {

        FANG_FANG = new Voice("fangfang") ;
        FANG_FANG.addWord(TYPE_PICK_UP_LIGHTER, "收取打火机");

        FANG_FANG.addWord(TYPE_HUMAN_FLOW_GUIDANCE, "人流引导1");
        FANG_FANG.addWord(TYPE_HUMAN_FLOW_GUIDANCE, "人流引导2");
        FANG_FANG.addWord(TYPE_HUMAN_FLOW_GUIDANCE, "人流引导3");
        FANG_FANG.addWord(TYPE_HUMAN_FLOW_GUIDANCE, "人流引导4");

        FANG_FANG.addWord(TYPE_BROADCAST_IN_CROWD, "人群内播报1");
        FANG_FANG.addWord(TYPE_BROADCAST_IN_CROWD, "人群内播报2");

        FANG_FANG.addWord(TYPE_LADY_FIRST, "Lady first");

        FANG_FANG.addWord(TYPE_VIP, "VIP");

        FANG_FANG.addWord(TYPE_PIROUETTE, "原地旋转");

        FANG_FANG.addWord(TYPE_CONTRABAND, "违禁物品");

        FANG_FANG.addWord(TYPE_HELLO, "大家好");

        FANG_FANG.addWord(TYPE_IN_COME, "大家请往里走");

        FANG_FANG.addWord(TYPE_JIA, "贾");


        XIAO_GUANG = new Voice("xiaoguang") ;
        XIAO_GUANG.addWord(TYPE_PICK_UP_LIGHTER, "收取打火机");

        XIAO_GUANG.addWord(TYPE_HUMAN_FLOW_GUIDANCE, "人流引导1");
        XIAO_GUANG.addWord(TYPE_HUMAN_FLOW_GUIDANCE, "人流引导2");
        XIAO_GUANG.addWord(TYPE_HUMAN_FLOW_GUIDANCE, "人流引导3");

        XIAO_GUANG.addWord(TYPE_BROADCAST_IN_CROWD, "人群内播报1");
        XIAO_GUANG.addWord(TYPE_BROADCAST_IN_CROWD, "人群内播报2");

        XIAO_GUANG.addWord(TYPE_LADY_FIRST, "Lady first");

        XIAO_GUANG.addWord(TYPE_VIP, "VIP");

        XIAO_GUANG.addWord(TYPE_PIROUETTE, "原地旋转");

        XIAO_GUANG.addWord(TYPE_CONTRABAND, "违禁物品");

    }







}
