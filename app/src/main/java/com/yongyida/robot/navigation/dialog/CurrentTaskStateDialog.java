package com.yongyida.robot.navigation.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yongyida.robot.navigation.R;
import com.yongyida.robot.navigation.bean.BaseTask;
import com.yongyida.robot.navigation.bean.TeamTask;
import com.yongyida.robot.navigation.bean.TimerTask;
import com.yongyida.robot.util.Constant;

import java.util.Locale;

/**
 * Create By HuangXiangXiang 2018/8/23
 * 当前任务状态
 */
public class CurrentTaskStateDialog extends Dialog implements View.OnClickListener {

    private TextView mTaskNameTvw;
    private TextView mTaskTimeTvw;
    private TextView mTaskStateTvw;
    private Button mCancelBtn;
    private Button mOperationTaskBtn;


    private TimerTask currTimerTask ;


    public CurrentTaskStateDialog(@NonNull Context context, TimerTask currTimerTask ) {
        super(context);

        setTaskData(currTimerTask ) ;
    }


    public void setTask(TimerTask currTimerTask){

        setTaskData(currTimerTask ) ;
        setCurrTaskInfoView() ;
    }

    private void setTaskData(TimerTask currTimerTask){

        this.currTimerTask = currTimerTask ;
    }


    private void setCurrTaskInfoView(){

        if(currTimerTask != null){

            boolean isTimerTaskRun = !currTimerTask.isStop() ;
            mTaskNameTvw.setText(String.format(Locale.CHINA, "当前任务：%s", currTimerTask.getPathInfo().getName()));
            mTaskTimeTvw.setText(String.format(Locale.CHINA, "执行时断：%02d:%02d—%02d:%02d", currTimerTask.getStartHour(), currTimerTask.getStartMinute(), currTimerTask.getEndHour(), currTimerTask.getEndMinute()));
            mTaskStateTvw.setText(String.format(Locale.CHINA, "状态：%s", isTimerTaskRun?"执行中" : "暂停中"));
            mOperationTaskBtn.setVisibility(View.VISIBLE);

            mOperationTaskBtn.setText(isTimerTaskRun ? "停止当前任务" : "开始当前任务");
        }else{

            mTaskNameTvw.setText("当前任务：无");
            mTaskTimeTvw.setText("执行时断：无");
            mTaskStateTvw.setText("") ;
            mOperationTaskBtn.setVisibility(View.GONE);
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_current_task_state);
        this.mTaskNameTvw = (TextView) findViewById(R.id.task_name_tvw);
        this.mTaskTimeTvw = (TextView) findViewById(R.id.task_time_tvw);
        this.mTaskStateTvw = (TextView) findViewById(R.id.task_state_tvw);
        this.mCancelBtn = (Button) findViewById(R.id.cancel_btn);
        this.mOperationTaskBtn = (Button) findViewById(R.id.operation_task_btn);

        this.mCancelBtn.setOnClickListener(this);
        this.mOperationTaskBtn.setOnClickListener(this);

        Constant.initDialogSize(this);

        setCurrTaskInfoView() ;

    }


    @Override
    public void onClick(View v) {

        if(v  == mCancelBtn){

            dismiss();

        }else if(v == mOperationTaskBtn){

            if(toggleTaskListener != null){

                boolean isResult = toggleTaskListener.toggleTask(currTimerTask.isStop()) ;
                if(isResult){

                    if(currTimerTask.isStop()){

                        mTaskStateTvw.setText("状态：暂停中");
                        mOperationTaskBtn.setText("开始当前任务");

                    }else {

                        mTaskStateTvw.setText("状态：执行中");
                        mOperationTaskBtn.setText("停止当前任务");
                    }
                }
            }
        }
    }


    private ToggleTaskListener toggleTaskListener ;

    public void setToggleTaskListener(ToggleTaskListener toggleTaskListener) {
        this.toggleTaskListener = toggleTaskListener;
    }

    public interface ToggleTaskListener{

        boolean toggleTask(boolean isTaskRun);

    }

}
