package com.yongyida.robot.navigation.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gs.gsnavlibrary.api.MapApi;
import com.gs.gsnavlibrary.bean.map.GsMap;
import com.gs.gsnavlibrary.listener.ResponseListener;
import com.yongyida.robot.data.MapInfoData;
import com.yongyida.robot.navigation.NavigationService;
import com.yongyida.robot.navigation.R;
import com.yongyida.robot.navigation.adapter.MapInfoAdapter;
import com.yongyida.robot.navigation.bean.MapInfo;
import com.yongyida.robot.navigation.view.refresh.SwipyRefreshLayout;
import com.yongyida.robot.navigation.view.refresh.SwipyRefreshLayoutDirection;
import com.yongyida.robot.util.LogHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Create By HuangXiangXiang 2018/8/23
 * 初始化界面
 */
public class MapActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = MapActivity.class.getSimpleName();

    /**
     * 当前地图
     */
    private TextView mCurrentMapTvw;
    private ListView mMapsLvw;
    private SwipyRefreshLayout mMapsSrl;
    /**
     * 切换地图
     */
    private Button mChangeMapBtn;

    private ArrayList<MapInfo> mapInfos;
    private MapInfoAdapter mapInfoAdapter;
    private ImageView mMapIvw;
    /**
     * 返回
     */
    private Button mBackBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);
        initView();

        bindService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unBindService();
    }

    private NavigationService.NavigationBinder mNavigationBinder;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mNavigationBinder = (NavigationService.NavigationBinder) service;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            mNavigationBinder = null;
        }
    };

    private void bindService() {

        Intent intent = new Intent(this, NavigationService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void unBindService() {

        unbindService(mServiceConnection);
        mNavigationBinder = null;
    }


    private void initView() {

        mCurrentMapTvw = findViewById(R.id.current_map_tvw);
        mMapsLvw = findViewById(R.id.maps_lvw);
        mMapsSrl = findViewById(R.id.maps_srl);
        mChangeMapBtn = findViewById(R.id.change_map_btn);
        mChangeMapBtn.setOnClickListener(this);

        mMapsSrl.setColorSchemeResources(R.color.colorPrimary);
        mMapsSrl.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {

                if (direction == SwipyRefreshLayoutDirection.TOP) {

                    requestGsMapList();
                }
            }
        });

        mapInfoAdapter = new MapInfoAdapter(this, mapInfos);
        mapInfoAdapter.setOnSelectMapListener(mOnSelectMapListener);

        mMapsLvw.setAdapter(mapInfoAdapter);
        mMapsLvw.setOnItemClickListener(this);
        mMapIvw = findViewById(R.id.map_ivw);

        MapInfo mapInfo = MapInfoData.getSelectMapInfo(this);
        mCurrentMapTvw.setText(String.format(Locale.CHINA, "当前地图《%s:%s》", mapInfo.getMapName(), mapInfo.getStartPointName()));

        requestGsMapList();
        mBackBtn = (Button) findViewById(R.id.back_btn);
        mBackBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.change_map_btn:
                break;
            case R.id.back_btn:
                onBackPressed();
                break;
        }
    }


    private ResponseListener<List<GsMap>> responseListener = new ResponseListener<List<GsMap>>() {
        @Override
        public void success(List<GsMap> gsMaps) {
            mapInfos = new ArrayList<>();

            final int size = gsMaps.size();
            LogHelper.i(TAG, LogHelper.__TAG__() + ", size : " + size);
            for (int i = 0; i < size; i++) {

                MapInfo mapInfo = new MapInfo();
                mapInfo.setMapName(gsMaps.get(i).name);

                mapInfos.add(mapInfo);
            }

            mapInfoAdapter.changeData(mapInfos);
            if (size > 0) {

                mMapsLvw.setSelection(0);
            }

            mMapsSrl.setRefreshing(false);
        }

        @Override
        public void faild(String s, String s1) {

            LogHelper.w(TAG, LogHelper.__TAG__() + ", s : " + s);

            mMapsSrl.setRefreshing(false);
        }

        @Override
        public void error(Throwable throwable) {

            LogHelper.e(TAG, LogHelper.__TAG__() + ", throwable : " + throwable);

            mMapsSrl.setRefreshing(false);
        }
    };

    private void requestGsMapList() {

        mapInfoAdapter.changeData(null);

        MapApi.requestGsMapList(responseListener);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        mapInfoAdapter.setSelectIndex(position);

        MapInfo mapInfo = mapInfos.get(position);
        MapApi.requestMapPng(new ResponseListener<Bitmap>() {
            @Override
            public void success(Bitmap bitmap) {

                mMapIvw.setImageBitmap(bitmap);
            }

            @Override
            public void faild(String s, String s1) {

            }

            @Override
            public void error(Throwable throwable) {

            }
        }, mapInfo.getMapName());

    }


    private MapInfoAdapter.OnSelectMapListener mOnSelectMapListener = new MapInfoAdapter.OnSelectMapListener() {

        @Override
        public void onSelectMap(MapInfo mapInfo) {

            MapInfoData.saveSelectMapInfo(MapActivity.this, mapInfo);
            mCurrentMapTvw.setText(String.format(Locale.CHINA, "当前地图《%s:%s》", mapInfo.getMapName(), mapInfo.getStartPointName()));

            if (mNavigationBinder != null) {

                mNavigationBinder.changeSelectMapInfo();
            }
        }
    };

}
