package com.smartgateapps.englifootball.Adapter;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartgateapps.englifootball.model.MatchMatch;
import com.smartgateapps.englifootball.R;
import com.smartgateapps.englifootball.engli.MyApplication;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

/**
 * Created by Raafat on 15/01/2016.
 */
public class MatchAdapterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements StickyRecyclerHeadersAdapter {

    private List<MatchMatch> data;
    private Context ctx;
    private int res;
    private int hRes;
    private LayoutInflater inflater;
    private int lastPosition = -1;

    public MatchAdapterAdapter(Context context, int resource,int headerResource, List<MatchMatch> objects) {
        this.data = objects;
        this.ctx = context;
        this.res = resource;
        this.hRes = resource;
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
        MatchMatch currMatch = this.getItem(position);

        MatchListAdapter adapter = new MatchListAdapter(ctx, R.layout.fragment_match_item,currMatch.getMatches());
        RecyclerView recyclerView = (RecyclerView)convertView.findViewById(R.id.recyclerView);

        int orientation = getLayoutManagerOrientation(ctx.getResources().getConfiguration().orientation);
        recyclerView.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(ctx, orientation, false));

//        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private int getLayoutManagerOrientation(int activityOrientation) {
        if (activityOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            return LinearLayoutManager.VERTICAL;
        } else {
            return LinearLayoutManager.HORIZONTAL;
        }
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    @Override
    public long getHeaderId(int position) {
        return this.data.get(position).getHdId();
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = this.inflater.inflate(R.layout.match_mathc_header_layut,parent,false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

        MatchMatch currItme = this.getItem(position);

        ImageView iconImgV = (ImageView)holder.itemView.findViewById(R.id.logoImgView);
        TextView titleTxtV = (TextView)holder.itemView.findViewById(R.id.leagueTitleTxtView);

        MyApplication.picasso
                .load(currItme.getLeagueImageUrl())
                .into(iconImgV);
        titleTxtV.setText(currItme.getLeague());
        titleTxtV.setTypeface(MyApplication.font);
    }

    public MatchMatch getItem(int position) {
        return this.data.get(position);
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(ctx, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
