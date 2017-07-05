package com.mardawang.android.mylocationdemo.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mardawang.android.mylocationdemo.R;

import java.util.ArrayList;

/**
 * Created by mardawang on 2017/7/5.
 */

public class LocationAdapter extends BaseAdapter {

    private ArrayList<String> locationlist;
    Context mcontext;

    public LocationAdapter(Context context, ArrayList<String> locationlist) {
        mcontext = context;
        this.locationlist = locationlist;
    }

    @Override
    public int getCount() {
        return locationlist == null ? 0 : locationlist.size();
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
        if (convertView == null) {
            convertView = LayoutInflater.from(mcontext).inflate(R.layout.item_location_nearby, null);
        }
        TextView tv_location = (TextView) convertView.findViewById(R.id.tv_location);
        tv_location.setText(locationlist.get(position));
        return convertView;
    }
}
