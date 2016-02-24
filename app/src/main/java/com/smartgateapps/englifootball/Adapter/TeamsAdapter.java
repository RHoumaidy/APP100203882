package com.smartgateapps.englifootball.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartgateapps.englifootball.R;
import com.smartgateapps.englifootball.model.Team;
import com.smartgateapps.englifootball.engli.MyApplication;

import java.util.List;

/**
 * Created by Raafat on 04/11/2015.
 */
public class TeamsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Team> data;
    private Context ctx;
    private int res;
    private LayoutInflater inflater;
    private int lastPosition = -1;

    public TeamsAdapter(List<Team> data, Context ctx, int res) {
        this.data = data;
        this.ctx = ctx;
        this.res = res;
        this.inflater = LayoutInflater.from(ctx);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        View convertView = holder.itemView;
        Team currTeam = this.data.get(position);

        ImageView teamLogoImV = (ImageView)convertView.findViewById(R.id.teamLogoImV);
        TextView teamNameTV = (TextView)convertView.findViewById(R.id.teamNameTV);
        teamNameTV.setTypeface(MyApplication.font);

        teamLogoImV.setImageResource(currTeam.getTeamLogo());
        teamLogoImV.setScaleType(ImageView.ScaleType.FIT_CENTER);

        teamNameTV.setText(currTeam.getTeamName());
        setAnimation(convertView,position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.inflater.inflate(res,parent,false);
        return  new RecyclerView.ViewHolder(view) {};
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    //    public TeamsAdapter(Context context, int resource, List<Team> objects) {
//        super(context, resource, objects);
//        this.data = objects;
//        this.res = resource;
//        this.ctx = context;
//        this.inflater = LayoutInflater.from(ctx);
//    }
//
//
//    @Override
//    public int getCount() {
//        return this.data.size();
//    }
//
//    @Override
//    public Team getItem(int position) {
//        return this.data.get(position);
//    }
//
//    @Override
//    public int getPosition(Team item) {
//        return this.data.indexOf(item);
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        if(convertView == null){
//            convertView = this.inflater.inflate(res,null);
//        }
//
//
//        Team currTeam = getItem(position);
//
//        ImageView teamLogoImV = (ImageView)convertView.findViewById(R.id.teamLogoImV);
//        TextView teamNameTV = (TextView)convertView.findViewById(R.id.teamNameTV);
//
//        teamLogoImV.setImageResource(currTeam.getTeamLogo());
//        teamLogoImV.setScaleType(ImageView.ScaleType.FIT_CENTER);
//
//        teamNameTV.setText(currTeam.getTeamName());
//        setAnimation(convertView,position);
//        return convertView;
//    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(MyApplication.APP_CTX, android.R.anim.slide_in_left);
            animation.setDuration(300);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
