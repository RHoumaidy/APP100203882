package com.smartgateapps.englifootball.Adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartgateapps.englifootball.R;
import com.smartgateapps.englifootball.model.Team;
import com.smartgateapps.englifootball.engli.MyApplication;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

/**
 * Created by Raafat on 15/12/2015.
 */
public class TeamPlaceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements StickyRecyclerHeadersAdapter {

    private List<Team> data;
    private int res;
    private Context ctx;
    private LayoutInflater inflater;
    private int lastPosition = -1;

    public TeamPlaceAdapter(Context context, int resource, List<Team> objects) {
        this.ctx = context;
        this.data = objects;
        this.res = resource;
        this.inflater = LayoutInflater.from(ctx);
    }

    public Team getItem(int position) {
        return this.data.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.inflater.inflate(res, parent, false);

        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Team currTeam = this.getItem(position);
//
        View convertView = holder.itemView;
        LinearLayout placeItmeRelL = (LinearLayout) convertView;

        if (position % 2 == 0) {
            placeItmeRelL.setBackground(new ColorDrawable(ctx.getResources().getColor(R.color.listItemSelected)));
            placeItmeRelL.refreshDrawableState();
        }

        TextView placeTxtV = (TextView) convertView.findViewById(R.id.posTxtV);
        TextView teamNameTxtV = (TextView) convertView.findViewById(R.id.teamTxtV);
        TextView matchPlayedTxtV = (TextView) convertView.findViewById(R.id.playTxtView);
        TextView matchWinnedTxtV = (TextView) convertView.findViewById(R.id.winTxtV);
        TextView matchDrawedTxtV = (TextView) convertView.findViewById(R.id.drawTxtV);
        TextView matchLosedTxtV = (TextView) convertView.findViewById(R.id.loseTxtV);
        TextView pointTxtV = (TextView) convertView.findViewById(R.id.pointTxtV);

        placeTxtV.setTypeface(MyApplication.font);
        teamNameTxtV.setTypeface(MyApplication.font);
        matchPlayedTxtV.setTypeface(MyApplication.font);
        matchWinnedTxtV.setTypeface(MyApplication.font);
        matchDrawedTxtV.setTypeface(MyApplication.font);
        matchLosedTxtV.setTypeface(MyApplication.font);
        pointTxtV.setTypeface(MyApplication.font);

        placeTxtV.setText(currTeam.getPlace());
        teamNameTxtV.setText(currTeam.getTeamName());
        matchDrawedTxtV.setText(currTeam.getMatchDrawed());
        matchLosedTxtV.setText(currTeam.getMatchLosed());
        matchWinnedTxtV.setText(currTeam.getMatchWined());
        matchPlayedTxtV.setText(currTeam.getMatchPlayed());
        pointTxtV.setText(currTeam.getPoints());

        setAnimation(holder.itemView, position);
    }

    @Override
    public long getHeaderId(int position) {
        return 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = this.inflater.inflate(R.layout.fragment_places_header, parent, false);

        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        View convertView = holder.itemView;
        TextView placeTxtV = (TextView) convertView.findViewById(R.id.posTxtV);
        TextView teamNameTxtV = (TextView) convertView.findViewById(R.id.teamTxtV);
        TextView matchPlayedTxtV = (TextView) convertView.findViewById(R.id.playTxtView);
        TextView matchWinnedTxtV = (TextView) convertView.findViewById(R.id.winTxtV);
        TextView matchDrawedTxtV = (TextView) convertView.findViewById(R.id.drawTxtV);
        TextView matchLosedTxtV = (TextView) convertView.findViewById(R.id.loseTxtV);
        TextView pointTxtV = (TextView) convertView.findViewById(R.id.pointTxtV);
        TextView matchestxtV = (TextView)convertView.findViewById(R.id.matchestxtV);

        matchestxtV.setTypeface(MyApplication.font);
        placeTxtV.setTypeface(MyApplication.font);
        teamNameTxtV.setTypeface(MyApplication.font);
        matchPlayedTxtV.setTypeface(MyApplication.font);
        matchWinnedTxtV.setTypeface(MyApplication.font);
        matchDrawedTxtV.setTypeface(MyApplication.font);
        matchLosedTxtV.setTypeface(MyApplication.font);
        pointTxtV.setTypeface(MyApplication.font);
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(MyApplication.APP_CTX, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

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
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        if(convertView == null)
//            convertView = this.inflater.inflate(res,null);
//
//
//        Team currTeam = this.getItem(position);
//
//        LinearLayout placeItmeRelL = (LinearLayout) convertView;
//
//        if(position%2 == 0) {
//            placeItmeRelL.setBackground(new ColorDrawable(ctx.getResources().getColor(R.color.listItemSelected)));
//        }
//
//        TextView placeTxtV = (TextView) convertView.findViewById(R.id.posTxtV);
//        TextView teamNameTxtV  = (TextView) convertView.findViewById(R.id.teamTxtV);
//        TextView matchPlayedTxtV = (TextView) convertView.findViewById(R.id.playTxtView);
//        TextView matchWinnedTxtV = (TextView) convertView.findViewById(R.id.winTxtV);
//        TextView matchDrawedTxtV = (TextView) convertView.findViewById(R.id.drawTxtV);
//        TextView matchLosedTxtV = (TextView) convertView.findViewById(R.id.loseTxtV);
//        TextView pointTxtV = (TextView) convertView.findViewById(R.id.pointTxtV);
//
//        placeTxtV.setText(currTeam.getPlace());
//        teamNameTxtV.setText(currTeam.getTeamName());
//        matchDrawedTxtV.setText(currTeam.getMatchDrawed());
//        matchLosedTxtV.setText(currTeam.getMatchLosed());
//        matchWinnedTxtV.setText(currTeam.getMatchWined());
//        matchPlayedTxtV.setText(currTeam.getMatchPlayed());
//        pointTxtV.setText(currTeam.getPoints());
//
//        return convertView;
//    }
//
//
//    @Override
//    public View getHeaderView(int position, View convertView, ViewGroup parent) {
//        return this.inflater.inflate(R.layout.fragment_places_header,null);
//    }
//
//    @Override
//    public long getHeaderId(int position) {
//        return 1;
//    }
}
