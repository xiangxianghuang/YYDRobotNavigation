package com.yongyida.robot.navigation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yongyida.robot.navigation.R;
import com.yongyida.robot.navigation.bean.TeamTask;

import java.util.ArrayList;

/**
 * Create By HuangXiangXiang 2018/10/22
 */
public class TeamAdapter extends BaseAdapter {

    private final LayoutInflater mInflater ;
    private ArrayList<TeamTask> mTeamTasks ;

    public TeamAdapter(Context context, ArrayList<TeamTask> teamTasks){

        mInflater = LayoutInflater.from(context) ;
        mTeamTasks = teamTasks ;
    }

    @Override
    public int getCount() {
        if(mTeamTasks == null){

            return 0 ;
        }

        return mTeamTasks.size();
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

        TextView teamNameTvw ;
        if(convertView == null){

            convertView = mInflater.inflate(R.layout.item_team, null) ;
            teamNameTvw = convertView.findViewById(R.id.team_name_tvw) ;
            convertView.setTag(teamNameTvw);

        }else {

            teamNameTvw = (TextView) convertView.getTag();
        }

        teamNameTvw.setText(mTeamTasks.get(position).getTeamName());

        return convertView;
    }
}
