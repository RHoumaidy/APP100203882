package com.smartgateapps.englifootball.Adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.smartgateapps.englifootball.R;
import com.smartgateapps.englifootball.model.Stage;
import com.smartgateapps.englifootball.engli.MyApplication;

import java.util.List;

/**
 * Created by Raafat on 19/12/2015.
 */
public class SpinnerAdapter extends ArrayAdapter<Stage> {

    private List<Stage> data;
    private Context ctx;
    private LayoutInflater inflater;
    private int res;
    public SpinnerAdapter(Context context, int resource, List<Stage> objects) {
        super(context, resource, objects);
        this.ctx = context;
        this.data = objects;
        this.inflater = LayoutInflater.from(ctx);
        this.res = resource;
    }

    @Override
    public int getCount() {
        return this.data.size();
    }

    @Override
    public Stage getItem(int position) {
        return this.data.get(position);
    }

    @Override
    public int getPosition(Stage item) {
        return this.data.indexOf(item);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = this.inflater.inflate(res, parent, false);

        AppCompatCheckedTextView textView = (AppCompatCheckedTextView)convertView;

        Stage currStage =this.getItem(position);

        if(textView != null) {
            textView.setText(currStage.getName());
            textView.setChecked(true);
            textView.setTextColor(ctx.getResources().getColor(R.color.nav_item_state_color));
            textView.setTypeface(MyApplication.font);
        }
        return convertView;
    }

//    @Override
//    public View getDropDownView(int position, View convertView, ViewGroup parent) {
//        if(convertView == null)
//            convertView = this.inflater.inflate(res, null, false);
//
//        AppCompatCheckedTextView textView = (AppCompatCheckedTextView)convertView;
//
//        Stage currStage =this.getItem(position);
//
//        if(textView != null) {
//            textView.setText(currStage.getName());
//            //textView.setChecked(true);
//            textView.setTextColor(ctx.getResources().getColor(R.color.nav_item_state_color));
//        }
//        return convertView;
//    }
}
