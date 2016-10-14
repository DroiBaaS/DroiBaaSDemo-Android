package com.droi.sdk.droibaasdemo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.droi.sdk.droibaasdemo.R;
import com.droi.sdk.droibaasdemo.download.DownloadManager;
import com.droi.sdk.droibaasdemo.models.AppInfo;
import com.droi.sdk.droibaasdemo.utils.CommonUtils;
import com.squareup.picasso.Picasso;

import org.xutils.ex.DbException;

import java.util.ArrayList;

/**
 * Created by chenpei on 2016/5/11.
 */
public class HotAppAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<AppInfo> mHotAppInfos;

    public HotAppAdapter(Context mContext, ArrayList<AppInfo> mHotAppInfos) {
        this.mContext = mContext;
        this.mHotAppInfos = mHotAppInfos;
    }

    @Override
    public int getCount() {
        return mHotAppInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mHotAppInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_app_info_grid, parent, false);
            holder = new ViewHolder();
            holder.nameView = (TextView) convertView.findViewById(R.id.app_name);
            holder.iconView = (ImageView) convertView.findViewById(R.id.app_icon);
            holder.installCountView = (TextView) convertView.findViewById(R.id.app_detail_install_count);
            holder.installButtonView = (TextView) convertView.findViewById(R.id.item_app_install_button);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final AppInfo item = mHotAppInfos.get(position);
        Picasso.with(mContext).load(item.getIcon()).into(holder.iconView);
        holder.nameView.setText(item.getName());
        holder.installCountView.setText(CommonUtils.formatCont(item.getCount()));
        holder.installButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //下载
                String apkUrl = item.getApkUrl();
                String label = item.getObjectId() + item.getVersion();
                String path = "/sdcard/" + mContext.getPackageName() + "/" + label + ".apk";
                Toast.makeText(mContext, mContext.getString(R.string.downloading), Toast.LENGTH_SHORT).show();
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
        TextView nameView;
        ImageView iconView;
        TextView installCountView;
        TextView installButtonView;
    }
}
