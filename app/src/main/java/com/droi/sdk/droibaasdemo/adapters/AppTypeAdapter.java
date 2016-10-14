package com.droi.sdk.droibaasdemo.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.droi.sdk.droibaasdemo.R;
import com.droi.sdk.droibaasdemo.models.AppType;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by chenpei on 2016/5/11.
 */
public class AppTypeAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<AppType> mList;

    public AppTypeAdapter(Context mContext, ArrayList<AppType> mList) {
        this.mContext = mContext;
        this.mList = mList;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_app_type, parent, false);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.item_app_type_name);
            holder.imageView = (ImageView) convertView.findViewById(R.id.item_app_type_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (mList.get(position).getIcon() != null) {
            if (mList.get(position).getIconUrl() == null) {
                Log.i("TEST", "null" + position);
                mList.get(position).getIcon().getUriInBackground(new DroiCallback<Uri>() {
                    @Override
                    public void result(Uri uri, DroiError droiError) {
                        mList.get(position).setIconUrl(uri);
                        Picasso.with(mContext).load(uri).into(holder.imageView);
                    }
                });
            } else {
                Log.i("TEST", "!=null" + position);
                Picasso.with(mContext).load(mList.get(position).getIconUrl()).into(holder.imageView);
            }
        }
        holder.textView.setText(mList.get(position).getName());
        return convertView;

    }

    static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}
