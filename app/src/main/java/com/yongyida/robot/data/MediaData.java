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
    // Lady first
    public static final String TYPE_LADY_FIRST                  = "Lady first" ;
    // 快捷通道
    public static final String TYPE_VIP                         = "快捷通道" ;
    // 奥丁正在巡查
    public static final String TYPE_PIROUETTE                   = "奥丁正在巡查" ;
    // 违禁物品
    public static final String TYPE_CONTRABAND                  = "违禁物品" ;
    // 两侧都可以排队
    public static final String TYPE_BOTH_LINE                  = "两侧都可以排队" ;
    // 排队要淡定
    public static final String TYPE_LINE_EASY                  = "排队要淡定" ;

    public static final ArrayList<String> wordTypes = new ArrayList<>() ;
    static {

        wordTypes.add(TYPE_PICK_UP_LIGHTER) ;
        wordTypes.add(TYPE_HUMAN_FLOW_GUIDANCE) ;
        wordTypes.add(TYPE_LADY_FIRST) ;
        wordTypes.add(TYPE_VIP) ;
        wordTypes.add(TYPE_PIROUETTE) ;
        wordTypes.add(TYPE_CONTRABAND) ;
        wordTypes.add(TYPE_BOTH_LINE) ;
        wordTypes.add(TYPE_LINE_EASY) ;

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
    static {

        FANG_FANG = new Voice("fangfang") ;

        FANG_FANG.addWord(TYPE_PICK_UP_LIGHTER, "收取打火机");

        FANG_FANG.addWord(TYPE_HUMAN_FLOW_GUIDANCE, "人流引导4");

        FANG_FANG.addWord(TYPE_LADY_FIRST, "lady first");

        FANG_FANG.addWord(TYPE_VIP, "快捷通道");

        FANG_FANG.addWord(TYPE_PIROUETTE, "奥丁正在巡查");

        FANG_FANG.addWord(TYPE_CONTRABAND, "违禁物品");

        FANG_FANG.addWord(TYPE_BOTH_LINE, "两侧都可以排队");

        FANG_FANG.addWord(TYPE_LINE_EASY, "排队要淡定");




    }







}
