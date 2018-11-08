package com.yongyida.robot.navigation.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.gs.gsnavlibrary.api.SketchGraphApi;
import com.gs.gsnavlibrary.api.TaskQueueApi;
import com.gs.gsnavlibrary.bean.common.ActionPoint;
import com.gs.gsnavlibrary.bean.sketchpath.SketchGraph;
import com.gs.gsnavlibrary.bean.taskqueue.Task;
import com.gs.gsnavlibrary.bean.taskqueue.TaskQueue;
import com.gs.gsnavlibrary.listener.ResponseListener;
import com.yongyida.robot.data.PathDetailInfoData;
import com.yongyida.robot.navigation.NavigationHelper;
import com.yongyida.robot.navigation.R;
import com.yongyida.robot.navigation.adapter.ActionAdapter;
import com.yongyida.robot.navigation.bean.PointActionInfo;
import com.yongyida.robot.navigation.bean.PathInfo;
import com.yongyida.robot.navigation.bean.path.BaseAction;
import com.yongyida.robot.navigation.popup.AddActionPopupWindow;
import com.yongyida.robot.util.LogHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Create By HuangXiangXiang 2018/8/28
 */
public class EditPathActivity extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = EditPathActivity.class.getSimpleName() ;
    private static final String PATH_INFO = "PathInfo" ;
    private static final Gson GSON = new Gson() ;


    public static void startEditPathActivity(Context context , PathInfo pathInfo){

        Intent intent = new Intent(context, EditPathActivity.class) ;
        String data = GSON.toJson(pathInfo) ;
        intent.putExtra(PATH_INFO, data) ;
        context.startActivity(intent);
    }


    private PathInfo mPathInfo ;


    /**
     * 上一个
     */
    private Button mPreBtn;
    private Spinner mPositionSnr;
    /**
     * 下一个
     */
    private Button mNextBtn;
    /**
     * 保存
     */
    private Button mSaveBtn;
    /**
     * TextView
     */
    private TextView mPathNameTvw;
    private View mView4;
    private ListView mActionsLvw;


    private ArrayList<String> mKeys = new ArrayList<>();
    private ArrayList<String> mPointNames = new ArrayList<>();
    private HashMap<String, PointActionInfo> mPointActionInfos = new HashMap<>() ;


    private ArrayAdapter<String> mPointAdapter;
    /**
     * 返回
     */
    private Button mBackBtn;
    /**
     * 添加
     */
    private Button mAddBtn;

    private ActionAdapter mActionAdapter ;

    private String mPathKey ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPathDetailInfoData = PathDetailInfoData.getInstance(this) ;

        String json = getIntent().getStringExtra(PATH_INFO);
        mPathInfo = GSON.fromJson(json, PathInfo.class) ;

        setContentView(R.layout.activity_edit_path);
        initView();

        requestTaskPoints(mPathInfo.getMapName(), mPathInfo.getName()) ;
    }


    /**
     * 请求任务的全部点数据
     * */
    private void requestTaskPoints(final String mapName, final String taskName){

        ResponseListener<List<TaskQueue>> responseListener = new ResponseListener<List<TaskQueue>>() {
            @Override
            public void success(List<TaskQueue> taskQueues) {

                final int size = taskQueues.size() ;
                for(int i = 0 ; i < size ; i++) {

                    TaskQueue taskQueue = taskQueues.get(i);
                    if (taskQueue.name.equals(taskName)) {

                        requestPathsPoints(taskQueue.tasks) ;
                        return;
                    }
                }
            }

            @Override
            public void faild(String s, String s1) {

            }

            @Override
            public void error(Throwable throwable) {

            }
        } ;
        TaskQueueApi.requestTaskQueueList(responseListener, mPathInfo.getMapName());

    }


    private Iterator<Task> mIterator ;
    private void requestPathsPoints(List<Task> tasks){

        mKeys.clear();
        mPointNames.clear();
        mIterator = tasks.iterator() ;
        requestNextPathPoints() ;
    }

    private void requestNextPathPoints(){

        if(mIterator.hasNext()){

            Task task = mIterator.next() ;
            if("PlayGraphPathTask".equals(task.name)){

                JSONObject json = task.start_param ;
                String mapName = json.getString("map_name") ;
                String graphName = json.getString("graph_name") ;

                requestPathPoints(mapName, graphName) ;
            }else {

                requestNextPathPoints() ;
            }


        }else {

            mPointAdapter.notifyDataSetChanged();
        }

    }

    PathDetailInfoData mPathDetailInfoData ;

    private void requestPathPoints(final String mapName, final String graphName){

        LogHelper.i(TAG, LogHelper.__TAG__() + ", mapName " +  mapName + ", graphName " +  graphName );

        //2、SKETCH_GRAPH ,  // 手绘路径
        SketchGraphApi.requestSketchGraph(new ResponseListener<SketchGraph>() {
            @Override
            public void success(SketchGraph sketchGraph) {

                LogHelper.i(TAG, LogHelper.__TAG__()+ ", graphName " +  graphName + ", sketchGraphName " +  sketchGraph.name );

                List<ActionPoint> points = sketchGraph.points;
                final int size = points.size();
                for (int i = 0; i < size; i++) {

                    String pointName = points.get(i).name ;
                    String key = PathDetailInfoData.toKey(mapName, graphName, pointName);

                    PointActionInfo pointActionInfo = mPathDetailInfoData.queryPathDetailInfo(key) ;

                    mKeys.add(key) ;
                    mPointNames.add(pointName) ;
                    mPointActionInfos.put(key, pointActionInfo) ;
                }

                requestNextPathPoints() ;
            }

            @Override
            public void faild(String s, String s1) {
            }

            @Override
            public void error(Throwable throwable) {

            }
        }, mapName, graphName);

    }



    private void initView() {
        mPreBtn = findViewById(R.id.pre_btn);
        mPreBtn.setOnClickListener(this);
        mPositionSnr = findViewById(R.id.point_snr);
        mNextBtn = findViewById(R.id.next_btn);
        mNextBtn.setOnClickListener(this);
        mSaveBtn = findViewById(R.id.save_btn);
        mSaveBtn.setOnClickListener(this);
        mPathNameTvw = findViewById(R.id.path_name_tvw);
        mView4 = findViewById(R.id.view4);
        mActionsLvw = findViewById(R.id.actions_lvw);


        mPointAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mPointNames);
        mPositionSnr.setAdapter(mPointAdapter);
        mPositionSnr.setOnItemSelectedListener(this);
        mBackBtn = (Button) findViewById(R.id.back_btn);
        mBackBtn.setOnClickListener(this);
        mAddBtn = (Button) findViewById(R.id.add_btn);
        mAddBtn.setOnClickListener(this);

        mActionAdapter = new ActionAdapter(this) ;
        mActionsLvw.setAdapter(mActionAdapter);

        mPathNameTvw.setText(mPathInfo.getName());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.pre_btn:

                mPositionSnr.setSelection(mSelectIndex - 1);
                break;
            case R.id.next_btn:
                mPositionSnr.setSelection(mSelectIndex + 1);
                break;
            case R.id.save_btn:

                mPathDetailInfoData.savePathDetailInfos(mPointActionInfos);
                Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show();
                break;
            case R.id.back_btn:
                onBackPressed();

                break;
            case R.id.add_btn:

                showAddActionPopupWindow() ;
                break;
        }
    }

    private AddActionPopupWindow mAddActionPopupWindow ;

    private AddActionPopupWindow.OnAddItemListener mOnAddItemListener = new AddActionPopupWindow.OnAddItemListener() {
        @Override
        public void onAddItem(BaseAction.Type type) {

            mSelectPoint.addAction(type);

            mActionAdapter.notifyDataSetChanged();

        }
    };

    private void showAddActionPopupWindow(){

        if(mAddActionPopupWindow == null){

            mAddActionPopupWindow = new AddActionPopupWindow(this) ;
            mAddActionPopupWindow.setOnAddItemListener(mOnAddItemListener);
        }
        mAddActionPopupWindow.show(mAddBtn, mSelectPoint);

    }



    private int mSelectIndex;
    private PointActionInfo mSelectPoint ;
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        mSelectIndex = position;
        if (position == 0) {
            // 第一个
            mPreBtn.setEnabled(false);
            mNextBtn.setEnabled(true);

        } else if (position == mPointNames.size() - 1) {
            // 最后一个
            mPreBtn.setEnabled(true);
            mNextBtn.setEnabled(false);

        } else {
            mPreBtn.setEnabled(true);
            mNextBtn.setEnabled(true);
        }

        String key = mKeys.get(position) ;
        mSelectPoint = mPointActionInfos.get(key) ;
        mActionAdapter.setActions(mSelectPoint.getActions());

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }



}
