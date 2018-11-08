package com.yongyida.robot.navigation.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.gs.gsnavlibrary.api.PositionApi;
import com.gs.gsnavlibrary.bean.position.Position;
import com.gs.gsnavlibrary.listener.ResponseListener;
import com.yongyida.robot.navigation.R;
import com.yongyida.robot.navigation.bean.MapInfo;
import com.yongyida.robot.navigation.view.swipe.SwipeItemLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Create By HuangXiangXiang 2018/8/27
 */
public class MapInfoAdapter extends BaseAdapter {

    private final Context context;
    private final LayoutInflater inflater;
    private ArrayList<MapInfo> mapInfos;

    private int selectIndex = -1;


    public MapInfoAdapter(Context context, ArrayList<MapInfo> mapInfos) {

        this.context = context;
        this.inflater = LayoutInflater.from(context);

        this.mapInfos = mapInfos;
    }

    public void changeData(ArrayList<MapInfo> mapInfos) {

        this.selectIndex = -1 ;

        this.mapInfos = mapInfos;
        this.notifyDataSetChanged();
    }


    public void setSelectIndex(int selectIndex) {

        if(this.selectIndex != selectIndex){

            this.selectIndex = selectIndex;
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {

        if (this.mapInfos == null) {

            return 0;
        }

        return mapInfos.size();
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
            convertView = inflater.inflate(R.layout.item_map_info, null);

            holder = new ViewHolder(convertView) ;
            convertView.setTag(holder);

        }else {

            holder = (ViewHolder) convertView.getTag();
        }

        holder.setData(position);

        return convertView;
    }

    private class ViewHolder implements View.OnClickListener {
        SwipeItemLayout view;
        TextView mNameTvw;
        TextView mPointsTvw ;

        MapInfo mMapInfo ;

        ViewHolder(View view) {
            this.view = (SwipeItemLayout) view;
            this.mNameTvw = view.findViewById(R.id.name_tvw);
            this.mPointsTvw = view.findViewById(R.id.points_tvw);

            mPointsTvw.setOnClickListener(this);

        }

        void setData(int position){

            mMapInfo = mapInfos.get(position);
            mNameTvw.setText(mMapInfo.getMapName());

            if(selectIndex == position){

                mNameTvw.setBackgroundColor(0xffffff00);
            }else {

                mNameTvw.setBackground(null);
            }

        }

        @Override
        public void onClick(View v) {

            if(v == mPointsTvw){

                showPointsPop(view, mMapInfo.getMapName()) ;
            }
        }
    }

    private PopupWindow mPopupWindow ;
    private final ArrayList<String> mPointNames = new ArrayList<>() ;
    private ArrayAdapter mArrayAdapter ;
    private  SwipeItemLayout mItemLayout ;
    private String mMapName ;
    private void showPointsPop(final SwipeItemLayout itemLayout, final String mapName){

        mItemLayout = itemLayout ;
        mMapName = mapName ;

        PositionApi.requestPositions(new ResponseListener<List<Position>>() {
            @Override
            public void success(List<Position> positions) {

                mPointNames.clear();
                final int size = positions.size() ;
                for (int i = 0; i < size; i++) {

                    Position position = positions.get(i) ;
                    mPointNames.add(position.name) ;
                }
                showPointsPopView() ;
            }

            @Override
            public void faild(String s, String s1) {

            }

            @Override
            public void error(Throwable throwable) {

            }
        },mapName);
    }


    private void showPointsPopView(){

        if(mPopupWindow == null){

            mPopupWindow = new PopupWindow(context) ;
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setFocusable(true);
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(0xffffffff));

            ListView listView = new ListView(context) ;
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String pointName = mPointNames.get(position);
                    Toast.makeText(context, mMapName + ":" + pointName, Toast.LENGTH_SHORT).show();

                    if(mOnSelectMapListener != null){

                        MapInfo mapInfo = new MapInfo() ;
                        mapInfo.setMapName(mMapName);
                        mapInfo.setStartPointName(pointName);
                        mOnSelectMapListener.onSelectMap(mapInfo);
                    }

                    mItemLayout.close() ;
                    mPopupWindow.dismiss();
                }
            });

            mArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, mPointNames);
            listView.setAdapter(mArrayAdapter);

            mPopupWindow.setContentView(listView);

        }else {

            mArrayAdapter.notifyDataSetChanged();
        }

        mPopupWindow.showAsDropDown(mItemLayout,0,0, Gravity.RIGHT);

    }


    private OnSelectMapListener mOnSelectMapListener ;
    public void setOnSelectMapListener(OnSelectMapListener onSelectMapListener) {
        this.mOnSelectMapListener = onSelectMapListener;
    }

    public interface OnSelectMapListener{

        void onSelectMap(MapInfo mapInfo);
    }


}
