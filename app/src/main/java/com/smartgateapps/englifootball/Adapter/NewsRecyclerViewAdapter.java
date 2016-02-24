package com.smartgateapps.englifootball.Adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartgateapps.englifootball.R;
import com.smartgateapps.englifootball.model.News;
import com.smartgateapps.englifootball.engli.BlurBuilder;
import com.smartgateapps.englifootball.engli.MyApplication;
import com.squareup.picasso.Callback;

import java.util.List;

/**
 * Created by Raafat on 17/11/2015.
 */
public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<NewsRecyclerViewAdapter.MyHolder> {

    private List<News> data;
    private int res;
    private int lastPosition = -1;

    public NewsRecyclerViewAdapter(List<News> data , int res) {
        this.data = data;
        this.res = res;
    }


    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(res,null);
        MyHolder holder = new MyHolder(itemLayoutView);

        return holder;
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, int position) {
        News currNews = data.get(position);
        holder.titleTxtView.setText(currNews.getTitle());
        holder.descTxtView.setText(currNews.getSubTitle());
        holder.titleTxtView.setTypeface(MyApplication.font);
        holder.descTxtView.setTypeface(MyApplication.font);

        if(holder.imageView2 != null) {
            MyApplication.picasso
                    .load(currNews.getImgUrl())
                    .placeholder(R.drawable.water_mark)
                    .into(holder.imageView2, new Callback() {
                        @Override
                        public void onSuccess() {
                            if (Build.VERSION.SDK_INT > 17) {
                                Bitmap bitmap = ((BitmapDrawable) holder.imageView2.getDrawable()).getBitmap();
                                bitmap = BlurBuilder.blurRenderScript(MyApplication.APP_CTX, bitmap, 20);
                                holder.imageView.setImageBitmap(bitmap);
                            } else {
                                holder.imageView2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            }
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }else{
            MyApplication.picasso
                    .load(currNews.getImgUrl())
                    .placeholder(R.drawable.water_mark)
                    .into(holder.imageView);
        }

        setAnimation(holder.itemView,position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

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

    public static class MyHolder extends RecyclerView.ViewHolder{
        TextView titleTxtView;
        TextView descTxtView;
        ImageView imageView;
        ImageView imageView2;

        public MyHolder(View itemView) {
            super(itemView);
            titleTxtView = (TextView) itemView.findViewById(R.id.newsItemTitleTxV);
            descTxtView = (TextView) itemView.findViewById(R.id.newsItemSubTitleTxV);
            imageView = (ImageView) itemView.findViewById(R.id.newsItemImgV);
            imageView2 = (ImageView) itemView.findViewById(R.id.newsItemImgV2);
        }
    }
}
