package com.smartgateapps.englifootball.Adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
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
import com.smartgateapps.englifootball.model.TeamTransformation;
import com.smartgateapps.englifootball.engli.MyApplication;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

/**
 * Created by Raafat on 30/12/2015.
 */
public class TeamTransformationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements StickyRecyclerHeadersAdapter {

    private List<TeamTransformation> data;
    private Context ctx;
    private LayoutInflater inflater;
    private int lastPosition = -1;

    public TeamTransformationAdapter( Context ctx,List<TeamTransformation> data) {
        this.data = data;
        this.ctx = ctx;
        this.inflater = LayoutInflater.from(ctx);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.t_item_layout,parent,false);

        return new RecyclerView.ViewHolder(view) {};
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        View converView = holder.itemView;
        TeamTransformation currItem = this.data.get(position);

        LinearLayout tLinearLayout = (LinearLayout) converView.findViewById(R.id.tLLayout);
        TextView dateTxtV = (TextView) converView.findViewById(R.id.tDateTxtV);
        TextView playerNTxtV = (TextView) converView.findViewById(R.id.tPlayerNTxtV);
        TextView teamTxtV = (TextView) converView.findViewById(R.id.tTeamTxtV);
        ImageView teamCtryImgV = (ImageView) converView.findViewById(R.id.tTeamCntryTxtV);
        TextView typeTxtV = (TextView) converView.findViewById(R.id.tTypeTxtView);

        dateTxtV.setTypeface(MyApplication.font);
        playerNTxtV.setTypeface(MyApplication.font);
        teamTxtV.setTypeface(MyApplication.font);
        typeTxtV.setTypeface(MyApplication.font);

        if(position%2 == 0) {
            tLinearLayout.setBackground(new ColorDrawable(ctx.getResources().getColor(R.color.listItemSelected)));
            tLinearLayout.refreshDrawableState();
        }else{
            tLinearLayout.setBackground(new ColorDrawable(ctx.getResources().getColor(android.R.color.white)));
            tLinearLayout.refreshDrawableState();
        }

        dateTxtV.setText(currItem.getDate());
        playerNTxtV.setText(currItem.getpName());
        teamTxtV.setText(currItem.getoTeam());
        typeTxtV.setText(currItem.getType());
        MyApplication.picasso
                .load(currItem.getoTeamCUrl())
                .into(teamCtryImgV);

        setAnimation(holder.itemView,position);


    }

    @Override
    public long getHeaderId(int position) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = inflater.inflate(R.layout.t_header_layout,parent,false);

        return new RecyclerView.ViewHolder(view) {};
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(MyApplication.APP_CTX, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
