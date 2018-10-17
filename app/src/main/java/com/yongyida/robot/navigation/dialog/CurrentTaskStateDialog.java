package com.yongyida.robot.navigation.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yongyida.robot.navigation.R;
import com.yongyida.robot.navigation.bean.TaskInfo;
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



    private TaskInfo currTaskInfo ;
    private boolean isTaskRun ;



    public CurrentTaskStateDialog(@NonNull Context context,TaskInfo currTaskInfo ,boolean isTaskRun ) {
        super(context);

        this.currTaskInfo = currTaskInfo;
        this.isTaskRun = isTaskRun;

    }

    public void setCurrTaskInfo(TaskInfo currTaskInfo){

        this.currTaskInfo = currTaskInfo;

        setCurrTaskInfoView() ;
    }

    private void setCurrTaskInfoView(){

        if(currTaskInfo != null){

            mTaskNameTvw.setText(String.format(Locale.CHINA, "当前任务：%s", currTaskInfo.getPathInfo().getName()));
            mTaskTimeTvw.setText(String.format(Locale.CHINA, "执行时断：%02d:%02d—%02d:%02d", currTaskInfo.getStartHour(), currTaskInfo.getStartMinute(), currTaskInfo.getEndHour(), currTaskInfo.getEndMinute()));

            mOperationTaskBtn.setVisibility(View.VISIBLE);

        }else{

            mTaskNameTvw.setText("当前任务：无");
            mTaskTimeTvw.setText("执行时断：无");

            mOperationTaskBtn.setVisibility(View.GONE);

        }
    }

    public void setTaskRun(boolean isTaskRun) {
        this.isTaskRun = isTaskRun;

        setTaskRunView() ;
    }
    public void setTaskRunView() {

        mOperationTaskBtn.setText(isTaskRun ? "停止当前任务" : "开始当前任务");
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

        setTaskRunView() ;
    }


    @Override
    public void onClick(View v) {

        if(v  == mCancelBtn){

            dismiss();

        }else if(v == mOperationTaskBtn){

            if(toggleTaskListener != null){

                boolean isResult = toggleTaskListener.toggleTask(!isTaskRun) ;
                if(isResult){
                    isTaskRun = !isTaskRun;
                    mOperationTaskBtn.setText(isTaskRun ? "停止当前任务" : "开始当前任务");
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
