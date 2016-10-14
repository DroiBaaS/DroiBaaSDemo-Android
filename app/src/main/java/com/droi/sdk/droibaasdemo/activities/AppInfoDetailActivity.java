package com.droi.sdk.droibaasdemo.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.droi.sdk.DroiError;
import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.core.DroiCondition;
import com.droi.sdk.core.DroiQuery;
import com.droi.sdk.core.DroiQueryCallback;
import com.droi.sdk.droibaasdemo.R;
import com.droi.sdk.droibaasdemo.download.DownloadManager;
import com.droi.sdk.droibaasdemo.models.AppInfo;
import com.droi.sdk.droibaasdemo.utils.CommonUtils;
import com.squareup.picasso.Picasso;

import org.xutils.ex.DbException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenpei on 2016/5/11.
 */
public class AppInfoDetailActivity extends BaseActivity {

    public static final String APP_INFO_ID = "id";
    private ImageView iconView;
    private TextView appName;
    private RatingBar appRatingbar;
    private TextView appInstallCount;
    private TextView appSize;
    private TextView appIntro;
    private TextView appVersion;
    private TextView appTime;
    private TextView appSize2;
    private TextView title;
    private ImageButton backArrowButton;
    private ImageView imageView;
    private Button installView;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appinfo_detail);
        mContext = this;
        String appInfoId = getIntent().getStringExtra(APP_INFO_ID);
        initUI();
        fetchAppInfoDetailById(appInfoId);
    }

    private void initUI() {
        iconView = (ImageView) findViewById(R.id.app_icon);
        appName = (TextView) findViewById(R.id.app_name);
        appRatingbar = (RatingBar) findViewById(R.id.app_rating_bar);
        appInstallCount = (TextView) findViewById(R.id.app_detail_install_count);
        appSize = (TextView) findViewById(R.id.app_detail_size);
        appIntro = (TextView) findViewById(R.id.app_intro);
        appVersion = (TextView) findViewById(R.id.app_version);
        appTime = (TextView) findViewById(R.id.app_time);
        appSize2 = (TextView) findViewById(R.id.app_size);
        backArrowButton = (ImageButton) findViewById(R.id.top_bar_back_btn);
        imageView = (ImageView) findViewById(R.id.app_detail_image);
        installView = (Button) findViewById(R.id.app_detail_install_button);
        title = (TextView) findViewById(R.id.top_bar_title);
        //点击back箭头，退出该页面
        backArrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void fetchAppInfoDetailById(final String appInfoId) {
        DroiCondition cond = DroiCondition.cond("appId", DroiCondition.Type.EQ, appInfoId);
        DroiQuery query = DroiQuery.Builder.newBuilder().query(AppInfo.class).where(cond).build();
        query.runQueryInBackground(new DroiQueryCallback<AppInfo>() {
            @Override
            public void result(List<AppInfo> list, DroiError droiError) {
                if (droiError.isOk()) {
                    final AppInfo appInfo = list.get(0);
                    title.setText(appInfo.getName());
                    Picasso.with(mContext).load(appInfo.getIcon()).into(iconView);
                    appName.setText(appInfo.getName());
                    appRatingbar.setRating(appInfo.getRating());
                    appInstallCount.setText(CommonUtils.formatCont(appInfo.getCount()));
                    appSize.setText(CommonUtils.formatSize(appInfo.getSize()));
                    appSize2.setText(CommonUtils.formatSize(appInfo.getSize()));
                    appIntro.setText(appInfo.getIntro());
                    appVersion.setText(appInfo.getVersion());
                    Picasso.with(mContext).load(appInfo.getImageUrl()).into(imageView);
                    installView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //下载
                            String apkUrl = appInfo.getApkUrl();
                            String label = appInfo.getObjectId() + appInfo.getVersion();
                            String path = "/sdcard/" + mContext.getPackageName() + "/" + label + ".apk";
                            Toast.makeText(mContext, getString(R.string.downloading), Toast.LENGTH_SHORT).show();
                            try {
                                String eventId = "download";
                                Map<String, String> kv = new HashMap<String, String>();
                                kv.put("name", appInfo.getName());
                                kv.put("size", appInfo.getSize() + "");
                                DroiAnalytics.onCalculateEvent(mContext, eventId, kv, appInfo.getRating());
                                DownloadManager.getInstance().startDownload(apkUrl, label, path, appInfo.getIcon(), appInfo.getName(), appInfo.getVersion(), appInfo.getSize(), true, false, null);
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    //appTime.setText(appInfo.getUpdateTime().toString());
                } else {

                }
            }
        });
    }
}
