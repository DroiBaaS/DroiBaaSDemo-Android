package com.droi.sdk.droibaasdemo.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.droi.sdk.droibaasdemo.R;
import com.droi.sdk.droibaasdemo.download.DownloadInfo;
import com.droi.sdk.droibaasdemo.download.DownloadManager;
import com.droi.sdk.droibaasdemo.download.DownloadState;
import com.droi.sdk.droibaasdemo.download.DownloadViewHolder;
import com.droi.sdk.droibaasdemo.utils.CommonUtils;
import com.squareup.picasso.Picasso;

import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;

@ContentView(R.layout.activity_download)
public class DownloadActivity extends BaseActivity {

    @ViewInject(R.id.download_list_view)
    private ListView downloadList;

    private Context mContext;
    private DownloadManager downloadManager;
    private DownloadListAdapter downloadListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        mContext = this;
        downloadList = (ListView) findViewById(R.id.download_list_view);
        downloadManager = DownloadManager.getInstance();
        downloadListAdapter = new DownloadListAdapter();
        downloadList.setAdapter(downloadListAdapter);
        TextView title = (TextView) findViewById(R.id.top_bar_title);
        title.setText(getString(R.string.download_manager));
        ImageButton backArrowButton = (ImageButton) findViewById(R.id.top_bar_back_btn);
        backArrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 下载列表Adapter
     */
    private class DownloadListAdapter extends BaseAdapter {

        private Context mContext;
        private final LayoutInflater mInflater;

        private DownloadListAdapter() {
            mContext = getBaseContext();
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            if (downloadManager == null) return 0;
            return downloadManager.getDownloadListCount();
        }

        @Override
        public Object getItem(int i) {
            return downloadManager.getDownloadInfo(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            DownloadItemViewHolder holder = null;
            DownloadInfo downloadInfo = downloadManager.getDownloadInfo(i);
            if (view == null) {
                view = mInflater.inflate(R.layout.item_download, null);
                holder = new DownloadItemViewHolder(view, downloadInfo);
                view.setTag(holder);
                holder.refresh();
            } else {
                holder = (DownloadItemViewHolder) view.getTag();
                holder.update(downloadInfo);
            }

            if (downloadInfo.getState().value() < DownloadState.FINISHED.value()) {
                try {
                    downloadManager.startDownload(
                            downloadInfo.getUrl(),
                            downloadInfo.getLabel(),
                            downloadInfo.getFileSavePath(),
                            downloadInfo.getIcon(),
                            downloadInfo.getName(),
                            downloadInfo.getVersion(),
                            downloadInfo.getSize(),
                            downloadInfo.isAutoResume(),
                            downloadInfo.isAutoRename(),
                            holder);
                } catch (DbException ex) {
                    Toast.makeText(x.app(), "添加下载失败", Toast.LENGTH_LONG).show();
                }
            }
            return view;
        }
    }

    /**
     * ListView的ViewHolder
     */
    public class DownloadItemViewHolder extends DownloadViewHolder {
        @ViewInject(R.id.app_name)
        TextView name;
        @ViewInject(R.id.app_version)
        TextView version;
        @ViewInject(R.id.app_detail_size)
        TextView size;
        @ViewInject(R.id.app_download_pb)
        ProgressBar progressBar;
        @ViewInject(R.id.app_download_button)
        TextView installButton;
        @ViewInject(R.id.app_icon)
        ImageView iconView;

        public DownloadItemViewHolder(View view, DownloadInfo downloadInfo) {
            super(view, downloadInfo);
            refresh();
        }

        @Event(R.id.app_download_button)
        private void downloadButtonEvent(View view) {
            DownloadState state = downloadInfo.getState();
            switch (state) {
                case WAITING:
                case STARTED:
                    downloadManager.stopDownload(downloadInfo);
                    break;
                case ERROR:
                case STOPPED:
                    try {
                        downloadManager.startDownload(
                                downloadInfo.getUrl(),
                                downloadInfo.getLabel(),
                                downloadInfo.getFileSavePath(),
                                downloadInfo.getIcon(),
                                downloadInfo.getName(),
                                downloadInfo.getVersion(),
                                downloadInfo.getSize(),
                                downloadInfo.isAutoResume(),
                                downloadInfo.isAutoRename(),
                                this);
                    } catch (DbException ex) {
                        Toast.makeText(x.app(), "添加下载失败", Toast.LENGTH_LONG).show();
                    }
                    break;
                case FINISHED:
                    Toast.makeText(x.app(), "安装中", Toast.LENGTH_LONG).show();
                    CommonUtils.installApk(mContext, downloadInfo.getFileSavePath());
                    break;
                default:
                    break;
            }
        }

        private void removeEvent(View view) {
            try {
                downloadManager.removeDownload(downloadInfo);
                downloadListAdapter.notifyDataSetChanged();
            } catch (DbException e) {
                Toast.makeText(x.app(), "移除任务失败", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void update(DownloadInfo downloadInfo) {
            super.update(downloadInfo);
            refresh();
        }

        @Override
        public void onWaiting() {
            refresh();
        }

        @Override
        public void onStarted() {
            refresh();
        }

        @Override
        public void onLoading(long total, long current) {
            refresh();
        }

        @Override
        public void onSuccess(File result) {
            refresh();
        }

        @Override
        public void onError(Throwable ex, boolean isOnCallback) {
            refresh();
        }

        @Override
        public void onCancelled(Callback.CancelledException cex) {
            refresh();
        }

        /**
         * 刷新下载列表的状态
         */
        public void refresh() {
            name.setText(downloadInfo.getName());
            version.setText(downloadInfo.getVersion());
            size.setText(CommonUtils.formatSize(downloadInfo.getSize()));
            progressBar.setProgress(downloadInfo.getProgress());
            Picasso.with(mContext).load(downloadInfo.getIcon()).into(iconView);
            installButton.setVisibility(View.VISIBLE);
            installButton.setText(x.app().getString(R.string.stop));
            DownloadState state = downloadInfo.getState();
            switch (state) {
                case WAITING:
                case STARTED:
                    installButton.setText(x.app().getString(R.string.stop));
                    break;
                case ERROR:
                case STOPPED:
                    installButton.setText(x.app().getString(R.string.start));
                    break;
                case FINISHED:
                    installButton.setText(x.app().getString(R.string.install));
                    break;
                default:
                    installButton.setText(x.app().getString(R.string.start));
                    break;
            }
        }
    }
}
