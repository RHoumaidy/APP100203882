package com.smartgateapps.englifootball.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartgateapps.englifootball.R;
import com.smartgateapps.englifootball.model.Match;
import com.smartgateapps.englifootball.engli.MyApplication;

import java.util.List;

/**
 * Created by Raafat on 16/01/2016.
 */
public class MatchListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Match> data;
    private Context ctx;
    private int res;
    private LayoutInflater inflater;
    private int lastPosition = -1;


    public MatchListAdapter(Context context, int resource, List<Match> objects) {
        this.data = objects;
        this.ctx = context;
        this.res = resource;
        this.inflater = LayoutInflater.from(ctx);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.inflater.inflate(res, parent, false);

        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        View convertView = holder.itemView;
        Match currMatch = this.getItem(position);
        LinearLayout headerLayout = (LinearLayout) convertView.findViewById(R.id.headerLinearLayout);
        View childLayout = convertView.findViewById(R.id.childLinearLayout);
        String time = MyApplication.formatDateTime(currMatch.getDateTime())[1];
        String date = MyApplication.formatDateTime(currMatch.getDateTime())[0];
        
        if (!currMatch.isHeader()) {
            childLayout.setVisibility(View.VISIBLE);
            headerLayout.setVisibility(View.GONE);

            TextView matchTimeTxtV = (TextView) convertView.findViewById(R.id.matchTimeTxtV);
            TextView matchTeamRTxtV = (TextView) convertView.findViewById(R.id.matchTeamRTxtV);
            TextView matchTeamLTxtV = (TextView) convertView.findViewById(R.id.matchTeamLTxtV);
            TextView matchReslutRTxtV = (TextView) convertView.findViewById(R.id.matchResultRTxtV);
            TextView matchResultLTxtV = (TextView) convertView.findViewById(R.id.matchResultLTxtV);
            ImageView teamLImgView = (ImageView) convertView.findViewById(R.id.matchTeamLImgV);
            ImageView teamRImgView = (ImageView) convertView.findViewById(R.id.matchTEamRImgV);
            TextView endMatchTxtV = (TextView) convertView.findViewById(R.id.matchEdnTxtV);
            TextView goingMatchtxtV = (TextView) convertView.findViewById(R.id.matchGoingTxtV);

            matchTimeTxtV.setTypeface(MyApplication.font);
            matchTeamRTxtV.setTypeface(MyApplication.font);
            matchTeamLTxtV.setTypeface(MyApplication.font);
            matchReslutRTxtV.setTypeface(MyApplication.font);
            matchResultLTxtV.setTypeface(MyApplication.font);
            endMatchTxtV.setTypeface(MyApplication.font);
            goingMatchtxtV.setTypeface(MyApplication.font);

            if (currMatch.matchProgress() < 0) {
                endMatchTxtV.setVisibility(View.VISIBLE);
                goingMatchtxtV.setVisibility(View.GONE);
            } else if (currMatch.matchProgress() == 0) {
                goingMatchtxtV.setVisibility(View.VISIBLE);
                endMatchTxtV.setVisibility(View.VISIBLE);
            }else{
                goingMatchtxtV.setVisibility(View.GONE);
                endMatchTxtV.setVisibility(View.GONE);
            }

            matchTimeTxtV.setText(time);
            matchTeamRTxtV.setText(currMatch.getTeamR().getTeamName());

            matchReslutRTxtV.setText(currMatch.getResultR());
            matchResultLTxtV.setText(currMatch.getResultL());

            matchTeamLTxtV.setText(currMatch.getTeamL().getTeamName());

            teamRImgView.setImageDrawable(ctx.getResources().getDrawable(currMatch.getTeamR().getTeamLogo()));
            teamLImgView.setImageDrawable(ctx.getResources().getDrawable(currMatch.getTeamL().getTeamLogo()));

            setAnimation(convertView, position);
        } else {
            headerLayout.setVisibility(View.VISIBLE);
            childLayout.setVisibility(View.GONE);
            TextView matchDateTxtV = (TextView) convertView.findViewById(R.id.matchDateTxtV);
            matchDateTxtV.setText(date);
            matchDateTxtV.setTypeface(MyApplication.font);
        }
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public Match getItem(int position) {
        return this.data.get(position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(ctx, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

}
