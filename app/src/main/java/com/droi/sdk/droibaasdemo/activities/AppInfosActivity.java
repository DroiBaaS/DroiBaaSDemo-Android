package com.droi.sdk.droibaasdemo.activities;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.droi.sdk.DroiError;
import com.droi.sdk.core.DroiCondition;
import com.droi.sdk.core.DroiQuery;
import com.droi.sdk.core.DroiQueryCallback;
import com.droi.sdk.droibaasdemo.R;
import com.droi.sdk.droibaasdemo.adapters.AppInfoAdapter;
import com.droi.sdk.droibaasdemo.models.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenpei on 2016/5/11.
 */
public class AppInfosActivity extends BaseActivity {
    public static final String INTENT_APP_TYPE = "type";
    public static final String INTENT_APP_NAME = "name";
    private ArrayList<AppInfo> mAppInfos;
    private AppInfoAdapter mAppInfoAdapter;
    private ProgressBar mProgressBar;
    private ListView mListView;
    private ImageButton backArrowButton;
    private TextView title;
    private int indexNum = 0;
    private boolean refreshing = false;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (mAppInfoAdapter != null) {
                        mProgressBar.setVisibility(View.GONE);
                        mAppInfoAdapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appinfos);
        init();
    }

    private void init() {
        initToolBar();
        initUI();
    }

    private void initToolBar() {
        title = (TextView) findViewById(R.id.top_bar_title);
        String type = getIntent().getStringExtra(INTENT_APP_TYPE);
        if (type != null && !type.isEmpty()) {
            title.setText(type);
        } else {
            title.setText("搜索\"" + getIntent().getStringExtra(INTENT_APP_NAME) + "\"");
        }
        backArrowButton = (ImageButton) findViewById(R.id.top_bar_back_btn);
        backArrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initUI() {
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mListView = (ListView) findViewById(R.id.appinfos_list);
        findViewById(R.id.empty).setVisibility(View.GONE);
        if (mAppInfos == null) {
            mAppInfos = new ArrayList<>();
        }
        if (mAppInfos.isEmpty()) {
            mProgressBar.setVisibility(View.VISIBLE);
            fetchAppInfoData();
        }
        if (mAppInfoAdapter == null) {
            mAppInfoAdapter = new AppInfoAdapter(this, mAppInfos);
        }
        mListView.setAdapter(mAppInfoAdapter);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                        if (!refreshing) {
                            refreshing = false;
                            fetchAppInfoData();
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppInfo appInfo = mAppInfos.get(position);
                Intent intent = new Intent(AppInfosActivity.this, AppInfoDetailActivity.class);
                intent.putExtra(AppInfoDetailActivity.APP_INFO_ID, appInfo.getAppId());
                startActivity(intent);
            }
        });
    }

    private void fetchAppInfoData() {
        Log.i("TEST", "initUI");
        DroiQuery query;
        String appType = getIntent().getStringExtra(INTENT_APP_TYPE);
        if (appType == null || appType.isEmpty()) {
            String appName = getIntent().getStringExtra(INTENT_APP_NAME);
            DroiCondition cond = DroiCondition.cond("name", DroiCondition.Type.CONTAINS, appName);
            DroiCondition cond1 = DroiCondition.cond("brief", DroiCondition.Type.CONTAINS, appName);
            query = DroiQuery.Builder.newBuilder().limit(10).offset(indexNum * 10).query(AppInfo.class).where(cond.or(cond1)).build();
        } else {
            DroiCondition cond = DroiCondition.cond("type", DroiCondition.Type.EQ, appType);
            query = DroiQuery.Builder.newBuilder().limit(10).offset(indexNum * 10).query(AppInfo.class).where(cond).build();
        }
        if (query == null) {
            mProgressBar.setVisibility(View.GONE);
            mListView.setEmptyView(findViewById(R.id.empty));
            return;
        }

        query.runQueryInBackground(new DroiQueryCallback<AppInfo>() {
            @Override
            public void result(List<AppInfo> list, DroiError droiError) {
                if (droiError.isOk()) {
                    if (list.size() > 0) {
                        refreshing = false;
                        mAppInfos.clear();
                        mAppInfos.addAll(list);
                        handler.sendEmptyMessage(0);
                        mAppInfoAdapter.notifyDataSetChanged();
                        indexNum++;
                    }
                    if (mAppInfos.size() == 0) {
                        mListView.setEmptyView(findViewById(R.id.empty));
                        mProgressBar.setVisibility(View.GONE);
                    }
                } else {
                    mListView.setEmptyView(findViewById(R.id.empty));
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}
