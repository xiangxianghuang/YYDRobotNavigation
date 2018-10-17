package com.yongyida.robot.navigation.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.yongyida.robot.data.MediaData;
import com.yongyida.robot.navigation.R;
import com.yongyida.robot.navigation.bean.path.ActionData;
import com.yongyida.robot.navigation.bean.path.BaseAction;
import com.yongyida.robot.navigation.bean.path.PlayAction;
import com.yongyida.robot.navigation.bean.path.StopAction;
import com.yongyida.robot.navigation.view.swipe.SwipeItemLayout;

import java.util.ArrayList;

/**
 * Create By HuangXiangXiang 2018/8/30
 */
public class ActionAdapter extends BaseAdapter {

    private final Context context;
    private final LayoutInflater layoutInflater;

    private ArrayList<ActionData> actions;

    public ActionAdapter(Context context) {

        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public void setActions(ArrayList<ActionData> actions){

        this.actions = actions ;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (actions == null) {

            return 0;
        }

        return actions.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return BaseAction.Type.values().length;
    }

    @Override
    public int getItemViewType(int position) {

        BaseAction action = actions.get(position).getAction() ;
        BaseAction.Type type = action.getType();

        return getIndex(type);
    }

    private int getIndex(BaseAction.Type type){

        BaseAction.Type[] types = BaseAction.Type.values() ;
        final int size = types.length ;
        for (int i = 0; i < size; i++) {

            if(type == types[i]){

                return i ;
            }
        }
        return 0 ;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ActionData actionData = actions.get(position) ;

        BaseAction action = actionData.getAction() ;
        BaseAction.Type type = action.getType();

        switch (type) {

            case STOP:
                return getStopView(actionData, position, convertView, parent);

            case PLAY:
                return getPlayView(actionData, position, convertView, parent);
        }
        return null;
    }

    /***/
    private View getStopView(ActionData actionData, int position, View convertView, ViewGroup parent) {

        StopViewHolder holder ;
        if(convertView == null){

            convertView = layoutInflater.inflate(R.layout.item_action_stop, null);
            holder = new StopViewHolder(convertView) ;

            convertView.setTag(holder);

        }else {
            holder = (StopViewHolder) convertView.getTag();
        }

        holder.setActionData(actionData);

        return convertView;
    }

    class StopViewHolder implements RadioGroup.OnCheckedChangeListener, View.OnClickListener, TextWatcher {

        SwipeItemLayout view;
        RadioGroup mTurnRgp;
        RadioButton mNoTurnRbn;
        RadioButton mTurnRbn;
        EditText mAngleEdt;

        RadioGroup mStopTypeRgp;
        RadioButton mMediaRbn;
        RadioButton mTimeRbn;
        EditText mMilliSecondEtt;
        Button mDeleteBtn ;

        ActionData actionData;
        StopAction stopAction ;

        StopViewHolder(View view) {
            this.view = (SwipeItemLayout) view;
            this.mTurnRgp = view.findViewById(R.id.turn_rgp);
            this.mNoTurnRbn = view.findViewById(R.id.no_turn_rbn);
            this.mTurnRbn = view.findViewById(R.id.turn_rbn);
            this.mAngleEdt = view.findViewById(R.id.angle_edt);

            this.mStopTypeRgp = view.findViewById(R.id.stop_type_rgp);
            this.mMediaRbn = view.findViewById(R.id.media_rbn);
            this.mTimeRbn = view.findViewById(R.id.time_rbn);
            this.mMilliSecondEtt = view.findViewById(R.id.milli_second_ett);

            this.mDeleteBtn = view.findViewById(R.id.delete_btn);


            this.mAngleEdt.addTextChangedListener(this);
            this.mMilliSecondEtt.addTextChangedListener(this);



            mTurnRgp.setOnCheckedChangeListener(this);
            mStopTypeRgp.setOnCheckedChangeListener(this);
            mDeleteBtn.setOnClickListener(this);

        }

        public void setActionData(ActionData actionData) {

            this.actionData = actionData;
            stopAction = (StopAction) actionData.getAction();

            StopAction.Turn turn = stopAction.getTurn() ;
            StopAction.Turn.Type type = turn.getType() ;
            if(StopAction.Turn.Type.TURN.equals(type)){
                // 转向
                mTurnRgp.check(R.id.turn_rbn);
            }else {
                // 不转向
                mTurnRgp.check(R.id.no_turn_rbn);
            }
            mAngleEdt.setText(String.valueOf(turn.getAngle()));

            StopAction.End end = stopAction.getEnd() ;
            StopAction.End.Type type1 = end.getType() ;
            if(StopAction.End.Type.TIME.equals(type1)){
              // 时间
                mStopTypeRgp.check(R.id.time_rbn);

            } else {
                //媒体
                mStopTypeRgp.check(R.id.media_rbn);
            }
            mMilliSecondEtt.setText(String.valueOf(end.getMilliSecond()));

            view.close();
        }

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            if(group == mTurnRgp){

                if(checkedId == R.id.no_turn_rbn){
                    // 没有转向
                    stopAction.getTurn().setType(StopAction.Turn.Type.NO_TURN);

                }else if (checkedId == R.id.turn_rbn){
                    // 有转向
                    stopAction.getTurn().setType(StopAction.Turn.Type.TURN);
                }

            }else if(group == mStopTypeRgp){

                if(checkedId == R.id.media_rbn){
                    // 媒体
                    stopAction.getEnd().setType(StopAction.End.Type.PLAY);

                }else if (checkedId == R.id.time_rbn){
                    // 时间
                    stopAction.getEnd().setType(StopAction.End.Type.TIME);
                }
            }


        }

        @Override
        public void onClick(View v) {

            actions.remove(actionData) ;
            notifyDataSetChanged();

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if(s == mAngleEdt.getText()){

                int angle = 0  ;
                try{

                    angle = Integer.parseInt(s.toString()) ;

                }catch (Exception e){
                }
                stopAction.getTurn().setAngle(angle);

            }else if(s == mMilliSecondEtt.getText()){

                int milliSecond = 1000  ;
                try{

                    milliSecond = Integer.parseInt(s.toString()) ;

                }catch (Exception e){
                }
                stopAction.getEnd().setMilliSecond(milliSecond);
            }

        }
    }


    /***/
    private View getPlayView(ActionData actionData, int position, View convertView, ViewGroup parent) {

        PlayViewHolder holder;
        if (convertView == null) {

            convertView = layoutInflater.inflate(R.layout.item_action_play, null);
            holder = new PlayViewHolder(convertView);

            convertView.setTag(holder);

        } else {

            holder = (PlayViewHolder) convertView.getTag();
        }

        holder.setActionData(actionData);

        return convertView;
    }

    class PlayViewHolder implements AdapterView.OnItemSelectedListener, View.OnClickListener, TextWatcher {

        SwipeItemLayout view;
        Spinner mMediaTypeSnr;
        Spinner mMediaSnr;
        EditText mTimesEtt;
        Button mDeleteBtn ;

        ActionData actionData ;
        PlayAction playAction ;

        ArrayList<String> mediaTypeNames ;
        ArrayAdapter mediaTypeSnr ;

        ArrayAdapter typeAdapter;

        private ArrayList<String> getMediaTypeNames(){

            ArrayList<String> mediaTypeNames = new ArrayList<>() ;

            PlayAction.MediaType[] mediaTypes = PlayAction.MediaType.values() ;
            for (PlayAction.MediaType mediaType: mediaTypes) {

                mediaTypeNames.add(mediaType.name) ;
            }
            return mediaTypeNames ;
        }


        PlayViewHolder(View view) {
            this.view = (SwipeItemLayout) view;
            this.mMediaTypeSnr = view.findViewById(R.id.media_type_snr);
            this.mMediaSnr = view.findViewById(R.id.media_snr);
            this.mTimesEtt = view.findViewById(R.id.times_ett);
            this.mDeleteBtn = view.findViewById(R.id.delete_btn);

            this.mTimesEtt.addTextChangedListener(this);


            mediaTypeNames = getMediaTypeNames() ;
            mediaTypeSnr = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, mediaTypeNames);
            this.mMediaTypeSnr.setAdapter(mediaTypeSnr);
            this.mMediaTypeSnr.setOnItemSelectedListener(this);


            this.typeAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, types);
            this.mMediaSnr.setAdapter(typeAdapter);
            this.mMediaSnr.setOnItemSelectedListener(this);

            mDeleteBtn.setOnClickListener(this);
        }

        void setActionData(ActionData actionData) {

            this.actionData = actionData;
            playAction = (PlayAction) actionData.getAction();

            switch (playAction.getMediaType()){// 播放类型

                case WORD:  //文字
                    mMediaTypeSnr.setSelection(0);

                    types.clear();
                    types.addAll(wordTypes);
                    typeAdapter.notifyDataSetChanged();

                    int selectIndex = getWordSelectIndex(playAction.getName());
                    this.mMediaSnr.setSelection(selectIndex);
                    break;

                case VIDEO://视频
                    mMediaTypeSnr.setSelection(1);

                    types.clear();
                    types.addAll(videoTypes);
                    typeAdapter.notifyDataSetChanged();

                    selectIndex = getVideoSelectIndex(playAction.getName());
                    this.mMediaSnr.setSelection(selectIndex);
                    break;
            }

            this.mTimesEtt.setText(String.valueOf(playAction.getTimes()));

            view.close();
        }


        /**选择文字*/
        private int getWordSelectIndex(String name) {

            final int size = wordTypes.size();
            for (int i = 0; i < size; i++) {

                if (wordTypes.get(i).equals(name)) {

                    return i;
                }
            }

            return 0;
        }

        /**选择视频*/
        private int getVideoSelectIndex(String name) {

            final int size = videoTypes.size();
            for (int i = 0; i < size; i++) {

                if (videoTypes.get(i).equals(name)) {

                    return i;
                }
            }

            return 0;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            if(parent == mMediaTypeSnr){

                String name = mediaTypeNames.get(position) ;
                if(PlayAction.MediaType.WORD.name.equals(name)){

                    if(playAction.getMediaType() != PlayAction.MediaType.WORD){

                        playAction.setMediaType(PlayAction.MediaType.WORD);
                        playAction.setName(wordTypes.get(0));

                        types.clear();
                        types.addAll(wordTypes);
                        typeAdapter.notifyDataSetChanged();

                        int selectIndex = getWordSelectIndex(playAction.getName());
                        this.mMediaSnr.setSelection(selectIndex);
                    }


                }else if(PlayAction.MediaType.VIDEO.name.equals(name)){

                    if(playAction.getMediaType() != PlayAction.MediaType.VIDEO){

                        playAction.setMediaType(PlayAction.MediaType.VIDEO);
                        playAction.setName(videoTypes.get(0));

                        types.clear();
                        types.addAll(videoTypes);
                        typeAdapter.notifyDataSetChanged();

                    }

                }

            }else if(parent == mMediaSnr){

                PlayAction.MediaType mediaType = playAction.getMediaType() ;
                if(PlayAction.MediaType.WORD == mediaType ){

                    playAction.setName(wordTypes.get(position));

                }else if(PlayAction.MediaType.VIDEO == mediaType){

                    playAction.setName(videoTypes.get(position));
                }

            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

        @Override
        public void onClick(View v) {

            actions.remove(actionData) ;
            notifyDataSetChanged();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if(s == mTimesEtt.getText()){

                int times = 0  ;
                try{

                    times = Integer.parseInt(s.toString()) ;

                }catch (Exception e){
                }
                playAction.setTimes(times);

            }
        }
    }

    private final ArrayList<String> types = new ArrayList<>() ;
    private final ArrayList<String> wordTypes = MediaData.wordTypes;
    private final ArrayList<String> videoTypes = getVideoTypes();

    private ArrayList<String> getVideoTypes(){

        ArrayList<String> videoTypes = new ArrayList<>() ;
        videoTypes.add("宣传视频") ;

        return videoTypes ;
    }



}
