package com.yongyida.robot.navigation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yongyida.robot.navigation.R;
import com.yongyida.robot.navigation.bean.TeamTask;

import java.util.ArrayList;

/**
 * Create By HuangXiangXiang 2018/10/22
 */
public class TeamAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private ArrayList<TeamTask> mTeamTasks;

    private String mTeamName ;
    private float mPercent ;

    public TeamAdapter(Context context, ArrayList<TeamTask> teamTasks) {

        mInflater = LayoutInflater.from(context);
        mTeamTasks = teamTasks;
    }

    public void setData(String teamName){

        this.mTeamName = teamName ;
        this.mPercent = 0 ;
        notifyDataSetChanged();
    }

    public void setData(String teamName , float percent){

        this.mTeamName = teamName ;
        this.mPercent = percent ;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        if (mTeamTasks == null) {

            return 0;
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

        ViewHolder holder ;
        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.item_team, null);
            holder = new ViewHolder(convertView) ;
            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        holder.setData(mTeamTasks.get(position).getTeamName());

        return convertView;
    }

    class ViewHolder {
        View view;
        ProgressBar mPercentPbr;
        TextView mTeamNameTvw;

        ViewHolder(View view) {
            this.view = view;
            this.mPercentPbr = (ProgressBar) view.findViewById(R.id.percent_pbr);
            this.mTeamNameTvw = (TextView) view.findViewById(R.id.team_name_tvw);
        }

        private void setData(String teamName){

            this.mTeamNameTvw.setText(teamName);

            if(teamName.equals(mTeamName)){

                view.setBackgroundResource(R.drawable.bg_item_team_select);

                if(mPercent > 0){

                    mPercentPbr.setVisibility(View.VISIBLE);
                    mPercentPbr.setProgress((int) mPercent);

                }else{

                    mPercentPbr.setVisibility(View.GONE);
                }

            }else {
                view.setBackgroundResource(R.drawable.bg_item_team);

                mPercentPbr.setVisibility(View.GONE);
            }
        }


    }
}
