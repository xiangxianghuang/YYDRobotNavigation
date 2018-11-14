package com.yongyida.robot.navigation.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yongyida.robot.navigation.R;
import com.yongyida.robot.navigation.TaskHandler;
import com.yongyida.robot.navigation.bean.TimerTask;
import com.yongyida.robot.util.Constant;

import java.util.Locale;

/**
 * Create By HuangXiangXiang 2018/8/23
 * 当前任务状态
 */
public class CurrentTaskStateDialog extends Dialog implements View.OnClickListener {

    private String currTaskInfo ;
    private TimerTask currTimerTask;

    private TextView mCurrTaskInfoTvw;
    private TextView mTimeTaskNameTvw;
    private TextView mTimeTaskTimeTvw;
    private TextView mTaskStateTvw;
    private Button mCancelBtn;
    private Button mOperationTimerTaskBtn;



    public CurrentTaskStateDialog(@NonNull Context context, String currTaskInfo, TimerTask currTimerTask) {
        super(context);

        setTaskData(currTaskInfo, currTimerTask);
    }


    public void setTask(String currTaskInfo, TimerTask currTimerTask) {

        setTaskData(currTaskInfo, currTimerTask);
        setCurrTaskInfoView() ;
        setCurrTimerTaskView();
    }

    private void setTaskData(String currTaskInfo, TimerTask currTimerTask) {

        this.currTaskInfo = currTaskInfo ;
        this.currTimerTask = currTimerTask;
    }


    private void setCurrTaskInfoView() {

        mCurrTaskInfoTvw.setText(currTaskInfo);
    }


    private void setCurrTimerTaskView() {

        if(currTimerTask != null){

            boolean isTimerTaskRun = !currTimerTask.isStop() ;

            mTimeTaskNameTvw.setText(String.format(Locale.CHINA, "定时任务名称：%s", currTimerTask.getPathInfo().getName()));
            mTimeTaskTimeTvw.setText(String.format(Locale.CHINA, "定时任务时段：%02d:%02d—%02d:%02d", currTimerTask.getStartHour(), currTimerTask.getStartMinute(), currTimerTask.getEndHour(), currTimerTask.getEndMinute()));
            mTaskStateTvw.setText(String.format(Locale.CHINA, "状态：%s", isTimerTaskRun?"执行中" : "暂停中"));

            mOperationTimerTaskBtn.setVisibility(View.VISIBLE);
            mOperationTimerTaskBtn.setText(isTimerTaskRun ? "停止当前任务" : "开始当前任务");
        }else{

            mTimeTaskNameTvw.setText("定时任务名称：无");
            mTimeTaskTimeTvw.setText("定时任务时段：无");
            mTaskStateTvw.setText("定时任务状态：无");
            mOperationTimerTaskBtn.setVisibility(View.GONE);
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_current_task_state);

        this.mCurrTaskInfoTvw = (TextView) findViewById(R.id.curr_task_info_tvw);
        this.mTimeTaskNameTvw = (TextView) findViewById(R.id.time_task_name_tvw);
        this.mTimeTaskTimeTvw = (TextView) findViewById(R.id.time_task_time_tvw);
        this.mTaskStateTvw = (TextView) findViewById(R.id.task_state_tvw);
        this.mCancelBtn = (Button) findViewById(R.id.cancel_btn);
        this.mOperationTimerTaskBtn = (Button) findViewById(R.id.operation_timer_task_btn);

        this.mCancelBtn.setOnClickListener(this);
        this.mOperationTimerTaskBtn.setOnClickListener(this);

        Constant.initDialogSize(this);

        setCurrTaskInfoView() ;
        setCurrTimerTaskView();

    }


    @Override
    public void onClick(View v) {

        if (v == mCancelBtn) {

            dismiss();

        } else if (v == mOperationTimerTaskBtn) {

            if (toggleTaskListener != null) {

                boolean isResult = toggleTaskListener.toggleTask(currTimerTask.isStop());
                if (isResult) {

                    if (currTimerTask.isStop()) {

                        mTaskStateTvw.setText("状态：暂停中");
                        mOperationTimerTaskBtn.setText("开始当前任务");

                    } else {

                        mTaskStateTvw.setText("状态：执行中");
                        mOperationTimerTaskBtn.setText("停止当前任务");
                    }
                }
            }
        }
    }


    private ToggleTaskListener toggleTaskListener;

    public void setToggleTaskListener(ToggleTaskListener toggleTaskListener) {
        this.toggleTaskListener = toggleTaskListener;
    }

    public interface ToggleTaskListener {

        boolean toggleTask(boolean isTaskRun);

    }



    public final TaskHandler.OnTaskChangeListener mOnTaskChangeListener = new TaskHandler.OnTaskChangeListener(){

        @Override
        public void onCurrTaskInfoChange(String currTaskInfo) {

            CurrentTaskStateDialog.this.currTaskInfo = currTaskInfo ;
            setCurrTaskInfoView();
        }

        @Override
        public void onTimerTaskChange(TimerTask currTimerTask) {

            CurrentTaskStateDialog.this.currTimerTask = currTimerTask ;
            setCurrTimerTaskView();
        }
    } ;


}
