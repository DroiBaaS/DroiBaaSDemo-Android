package com.droi.sdk.droibaasdemo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.droibaasdemo.R;
import com.droi.sdk.droibaasdemo.download.DownloadManager;
import com.droi.sdk.droibaasdemo.models.AppInfo;
import com.droi.sdk.droibaasdemo.utils.CommonUtils;
import com.squareup.picasso.Picasso;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenpei on 2016/5/11.
 */
public class AppInfoAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<AppInfo> mAppInfos;

    public AppInfoAdapter(Context mContext, ArrayList<AppInfo> mAppInfos) {
        this.mAppInfos = mAppInfos;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mAppInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mAppInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_app_info_list, parent, false);
            holder = new ViewHolder();
            holder.iconView = (ImageView) convertView.findViewById(R.id.app_icon);
            holder.appNameView = (TextView) convertView.findViewById(R.id.app_name);
            holder.installCountView = (TextView) convertView.findViewById(R.id.app_detail_install_count);
            holder.installButtonView = (TextView) convertView.findViewById(R.id.item_app_install_button);
            holder.ratingBar = (RatingBar) convertView.findViewById(R.id.app_rating_bar);
            holder.briefView = (TextView) convertView.findViewById(R.id.item_app_brief);
            holder.sizeView = (TextView) convertView.findViewById(R.id.app_detail_size);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final AppInfo item = mAppInfos.get(position);
        Picasso.with(mContext).load(item.getIcon()).into(holder.iconView);
        holder.appNameView.setText(item.getName());
        holder.ratingBar.setRating(item.getRating());
        holder.installCountView.setText(CommonUtils.formatCont(item.getCount()));
        holder.briefView.setText(item.getBrief());
        holder.sizeView.setText(CommonUtils.formatSize(item.getSize()));
        holder.installButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //下载
                String apkUrl = item.getApkUrl();
                String label = item.getObjectId() + item.getVersion();
                String path = "/sdcard/" + mContext.getPackageName() + "/" + label + ".apk";
                Toast.makeText(mContext, mContext.getString(R.string.downloading), Toast.LENGTH_SHORT).show();
                //计算事件
                String eventId = "download";
                Map<String, String> kv = new HashMap<String, String>();
                kv.put("name", item.getName());
                DroiAnalytics.onCalculateEvent(mContext, eventId, kv, item.getRating());
                try {
                    DownloadManager.getInstance().startDownload(apkUrl, label, path, item.getIcon(), item.getName(), item.getVersion(), item.getSize(), true, false, null);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });
        return convertView;
    }

    static class ViewHolder {
        ImageView iconView;
        TextView appNameView;
        RatingBar ratingBar;
        TextView installCountView;
        TextView installButtonView;
        TextView briefView;
        TextView sizeView;
    }
}
