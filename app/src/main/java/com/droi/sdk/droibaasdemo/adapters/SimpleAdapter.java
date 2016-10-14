package com.droi.sdk.droibaasdemo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.droi.sdk.droibaasdemo.R;

import java.util.ArrayList;

public class SimpleAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> mList;

    public SimpleAdapter(Context context, ArrayList<String> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_simple, parent, false);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(mList.get(position));

        return convertView;
    }

    static class ViewHolder {
        TextView textView;
    }
}
