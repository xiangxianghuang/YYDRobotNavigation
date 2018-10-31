package com.yongyida.robot.navigation.adapter;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.yongyida.robot.navigation.R;
import com.yongyida.robot.navigation.bean.TimerTask;
import com.yongyida.robot.navigation.popup.MorePopupWindow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

/**
 * Create By HuangXiangXiang 2018/8/24
 */
public class TaskInfoAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;

    private ArrayList<TimerTask> taskInfos;


    public TaskInfoAdapter(Context context, ArrayList<TimerTask> taskInfos) {

        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.taskInfos = taskInfos;
//        sort() ;
    }


    /**
     * 根据时间先后排序
     * */
    private void sort(){

        if(taskInfos != null){

            Collections.sort(taskInfos, new Comparator<TimerTask>() {
                @Override
                public int compare(TimerTask o1, TimerTask o2) {

                    if(o1.getStartHour() == o2.getStartHour()){

                        return o1.getStartMinute() - o2.getStartMinute();

                    }else {

                        return o1.getStartHour() - o2.getStartHour();
                    }
                }
            }) ;
        }

    }


    @Override
    public void notifyDataSetChanged() {

        sort() ;
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        if (taskInfos == null) {

            return 0;
        }
        return taskInfos.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder ;
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.item_task_info, null);

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        }else {

            holder = (ViewHolder) convertView.getTag();
        }

        holder.setData(position) ;

        return convertView;
    }

    private class ViewHolder implements View.OnClickListener {
        View view;
        TextView mIndexTvw;
        TextView mTaskNameTvw;
        TextView mStartTimeTvw;
        TextView mEndTimeTvw;
        ImageView mMoreIbn;

        TimerTask taskInfo ;

        ViewHolder(View view) {
            this.view = view;
            this.mIndexTvw = (TextView) view.findViewById(R.id.index_tvw);
            this.mTaskNameTvw = (TextView) view.findViewById(R.id.task_name_tvw);
            this.mStartTimeTvw = (TextView) view.findViewById(R.id.start_time_tvw);
            this.mEndTimeTvw = (TextView) view.findViewById(R.id.end_time_tvw);
            this.mMoreIbn = view.findViewById(R.id.more_ibn);

            this.mStartTimeTvw.setOnClickListener(this);
            this.mEndTimeTvw.setOnClickListener(this);
            this.mMoreIbn.setOnClickListener(this);

        }

        public void setData(int position) {

            this.taskInfo = taskInfos.get(position);

            this.mIndexTvw.setText(String.valueOf(position + 1));
            this.mTaskNameTvw.setText(taskInfo.getPathInfo().getName());
            this.mStartTimeTvw.setText(String.format(Locale.CHINA, "%02d:%02d", taskInfo.getStartHour(), taskInfo.getStartMinute()));
            this.mEndTimeTvw.setText(String.format(Locale.CHINA, "%02d:%02d", taskInfo.getEndHour(), taskInfo.getEndMinute()));

            if(position > 0){

                TimerTask lastTaskInfo = taskInfos.get(position - 1);
                //当前起始时间小于上一个结束时间
                if(taskInfo.getStartHour() < lastTaskInfo.getEndHour()){
                    // 错误数据
                    this.mStartTimeTvw.setBackgroundResource(R.drawable.bg_error);

                }else if(taskInfo.getStartHour() == lastTaskInfo.getEndHour()){

                    if(taskInfo.getStartMinute() <= lastTaskInfo.getEndMinute() ){

                        // 错误数据
                        this.mStartTimeTvw.setBackgroundResource(R.drawable.bg_error);

                    }else {
                        this.mStartTimeTvw.setBackgroundResource(R.drawable.bg_correct);
                    }
                }else {
                    this.mStartTimeTvw.setBackgroundResource(R.drawable.bg_correct);
                }
            }

        }

        @Override
        public void onClick(View v) {

            if(v == mStartTimeTvw){

                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        if((hourOfDay > taskInfo.getEndHour()) ||
                                ((hourOfDay == taskInfo.getEndHour()) && (minute >= taskInfo.getEndMinute()))) {// 开始时间小于 大于结束时间

                            Toast.makeText(context, "起始时间不能大于结束时间", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        taskInfo.setStartHour(hourOfDay);
                        taskInfo.setStartMinute(minute);

                        mStartTimeTvw.setText(String.format(Locale.CHINA, "%02d:%02d", taskInfo.getStartHour(), taskInfo.getStartMinute()));

                        notifyDataSetChanged();

                        if(onTaskDataChangeListener != null){

                            onTaskDataChangeListener.onTaskDataChange();
                        }

                    }
                };
                TimePickerDialog timePicker = new TimePickerDialog(context, onTimeSetListener, taskInfo.getStartHour(), taskInfo.getStartMinute(),true);
                timePicker.show();





            }else if(v == mEndTimeTvw){

                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        if((taskInfo.getStartHour() > hourOfDay) ||
                                ((taskInfo.getStartHour() == hourOfDay) && (taskInfo.getStartMinute() >= minute))) {// 开始时间小于 大于结束时间

                            Toast.makeText(context, "起始时间不能大于结束时间", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        taskInfo.setEndHour(hourOfDay);
                        taskInfo.setEndMinute(minute);

                        mEndTimeTvw.setText(String.format(Locale.CHINA, "%02d:%02d", taskInfo.getEndHour(), taskInfo.getEndMinute()));

                        notifyDataSetChanged(); //

                        if(onTaskDataChangeListener != null){

                            onTaskDataChangeListener.onTaskDataChange();
                        }

                    }
                };
                TimePickerDialog timePicker = new TimePickerDialog(context, onTimeSetListener, taskInfo.getEndHour(), taskInfo.getStartMinute(),true);
                timePicker.show();

            }else if(v == mMoreIbn){
                // 更多操作
                showMorePop(taskInfo, mMoreIbn) ;
            }

        }
    }

    //
    private TimerTask taskInfo ;

    private MorePopupWindow morePopupWindow ;
    private MorePopupWindow.OnDataChangeListener onDataChangeListener = new MorePopupWindow.OnDataChangeListener() {
        @Override
        public void onRename(String newName) {

            taskInfo.getPathInfo().setName(newName);

            notifyDataSetChanged();

            if(onTaskDataChangeListener != null){

                onTaskDataChangeListener.onTaskDataChange();
            }
        }

        @Override
        public void onDelete() {

            taskInfos.remove(taskInfo) ;
            notifyDataSetChanged();

            if(onTaskDataChangeListener != null){

                onTaskDataChangeListener.onTaskDataChange();
            }

        }
    } ;

    private void showMorePop(TimerTask taskInfo, View view){

        this.taskInfo = taskInfo ;

        if(morePopupWindow == null){
            morePopupWindow = new MorePopupWindow(context) ;
            morePopupWindow.setOnDataChangeListener(onDataChangeListener);
        }
        morePopupWindow.show(view);
    }


    /**任务队列变化*/
    private OnTaskDataChangeListener onTaskDataChangeListener ;
    public void setOnTaskChangeListener(OnTaskDataChangeListener onTaskDataChangeListener) {
        this.onTaskDataChangeListener = onTaskDataChangeListener;
    }

    public interface OnTaskDataChangeListener{

        /**数据变化*/
        void onTaskDataChange() ;
    }


}
