package com.yongyida.robot.navigation.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gs.gsnavlibrary.api.MapApi;
import com.gs.gsnavlibrary.listener.ResponseListener;
import com.yongyida.robot.navigation.R;
import com.yongyida.robot.navigation.bean.PathInfo;
import com.yongyida.robot.navigation.dialog.PathDetailDialog;

import java.util.ArrayList;

/**
 * Create By HuangXiangXiang 2018/8/24
 * 路径名称
 */
public class PathInfoAdapter extends BaseAdapter {


    private int selectIndex = -1;

    private Context context;
    private LayoutInflater inflater;

    private ArrayList<PathInfo> pathInfos;

    public PathInfoAdapter(Context context) {

        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setSelectIndex(int selectIndex) {

        this.selectIndex = selectIndex;
        notifyDataSetChanged();
    }

    public int getSelectIndex() {
        return selectIndex;
    }

    public void setPathInfos(ArrayList<PathInfo> pathInfos) {

        this.pathInfos = pathInfos;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        if (pathInfos == null) {

            return 0;
        }

        return pathInfos.size();
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

            convertView = inflater.inflate(R.layout.item_path_info, null);
            holder = new ViewHolder(convertView) ;
            convertView.setTag(holder);

        }else {

            holder = (ViewHolder) convertView.getTag();
        }

        holder.setData(position) ;

        return convertView;
    }

    private class ViewHolder implements View.OnClickListener {
        View view;
        TextView mNameTvw;
        ImageView mDetailIbn;
        LinearLayout mMainLlt;

        PathInfo pathInfo ;

        ViewHolder(View view) {
            this.view = view;
            this.mNameTvw = view.findViewById(R.id.name_tvw);
            this.mDetailIbn = view.findViewById(R.id.detail_ibn);
            this.mMainLlt = view.findViewById(R.id.main_llt);

            this.mDetailIbn.setOnClickListener(this);
        }

        public void setData(int position) {

            this.pathInfo = pathInfos.get(position) ;

            if(selectIndex == position){

                this.mMainLlt.setBackgroundResource(R.drawable.bg_select);
            }else {
                this.mMainLlt.setBackground(null);
            }

//            this.mNameTvw.setText(pathInfo.getType() +"\n\t" + pathInfo.getName());
            this.mNameTvw.setText(pathInfo.getName());
        }

        @Override
        public void onClick(View v) {

            showDetail(pathInfo) ;
        }
    }

    private void showDetail(final PathInfo pathInfo){

        MapApi.requestMapPng(new ResponseListener<Bitmap>() {
            @Override
            public void success(Bitmap bitmap) {

                showDetailDialog(pathInfo, bitmap) ;
            }

            @Override
            public void faild(String s, String s1) {

            }

            @Override
            public void error(Throwable throwable) {

            }
        },pathInfo.getMapName());

    }


    private PathDetailDialog pathDetailDialog ;
    private void showDetailDialog(PathInfo pathInfo, Bitmap bitmap){

        if(pathDetailDialog == null){

            pathDetailDialog = new PathDetailDialog(context) ;
        }

        pathDetailDialog.setPathInfo(pathInfo) ;
        pathDetailDialog.setBitmap(bitmap) ;
        pathDetailDialog.show();

    }




}
