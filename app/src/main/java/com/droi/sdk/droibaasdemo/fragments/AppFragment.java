package com.droi.sdk.droibaasdemo.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.droi.sdk.DroiError;
import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.core.DroiCondition;
import com.droi.sdk.core.DroiQuery;
import com.droi.sdk.core.DroiQueryCallback;
import com.droi.sdk.droibaasdemo.R;
import com.droi.sdk.droibaasdemo.activities.AppInfoDetailActivity;
import com.droi.sdk.droibaasdemo.activities.SearchActivity;
import com.droi.sdk.droibaasdemo.adapters.AppInfoAdapter;
import com.droi.sdk.droibaasdemo.adapters.HotAppAdapter;
import com.droi.sdk.droibaasdemo.models.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenpei on 2016/5/11.
 */
public class AppFragment extends Fragment implements
        AdapterView.OnItemClickListener {
    private Context mContext;
    private ArrayList<AppInfo> mAppInfos;
    private AppInfoAdapter mAppInfoAdapter;
    private ArrayList<AppInfo> mHotAppInfos;
    private HotAppAdapter mHotAppAdapter;
    private int indexNum = 0;
    private boolean refreshing = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        View view = inflater.inflate(R.layout.fragment_app, container, false);
        initToolBar(view);
        initListView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        DroiAnalytics.onFragmentStart(getActivity(), "AppFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        DroiAnalytics.onFragmentEnd(getActivity(), "AppFragment");
    }

    private void initToolBar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        final TextView search = (TextView) toolbar.findViewById(R.id.toolbar_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initListView(View view) {
        ListView listview = (ListView) view.findViewById(R.id.fragment_list_view);
        listview.addHeaderView(getHeadView());
        listview.addFooterView(LayoutInflater.from(getActivity()).inflate(R.layout.view_footer, null));
        if (mAppInfos == null) {
            mAppInfos = new ArrayList<>();
        }
        if (mAppInfos.isEmpty()) {
            fetchAppInfoData();
        }
        mAppInfoAdapter = new AppInfoAdapter(mContext, mAppInfos);
        listview.setAdapter(mAppInfoAdapter);
        listview.setOnItemClickListener(this);
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                        if (!refreshing) {
                            refreshing = true;
                            fetchAppInfoData();
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private View getHeadView() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.view_head_app, null);
        initHotApp(view);
        return view;
    }

    private void initHotApp(View view) {
        GridView gridView = (GridView) view.findViewById(R.id.grid_view);
        if (mHotAppInfos == null) {
            mHotAppInfos = new ArrayList<>();
        }
        if (mHotAppInfos.isEmpty()) {
            fetchHotAppInfo();
        }
        mHotAppAdapter = new HotAppAdapter(mContext, mHotAppInfos);
        gridView.setAdapter(mHotAppAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, AppInfoDetailActivity.class);
                intent.putExtra(AppInfoDetailActivity.APP_INFO_ID, mHotAppInfos.get(position).getAppId());
                startActivity(intent);
            }
        });
    }

    private void fetchAppInfoData() {
        DroiCondition cond = DroiCondition.cond("mainType", DroiCondition.Type.EQ, "app");
        DroiQuery query = DroiQuery.Builder.newBuilder().limit(10).offset(indexNum * 10).query(AppInfo.class).where(cond).build();
        query.runQueryInBackground(new DroiQueryCallback<AppInfo>() {
            @Override
            public void result(List<AppInfo> list, DroiError droiError) {
                refreshing = false;
                if (droiError.isOk()) {
                    if (list.size() > 0) {
                        if (indexNum == 0) {
                            mAppInfos.clear();
                        }
                        mAppInfos.addAll(list);
                        mAppInfoAdapter.notifyDataSetChanged();
                        indexNum++;
                    }
                } else {
                    //做请求失败处理
                }
            }
        });
    }

    private void fetchHotAppInfo() {
        DroiCondition cond = DroiCondition.cond("mainType", DroiCondition.Type.EQ, "app");
        DroiQuery query = DroiQuery.Builder.newBuilder().query(AppInfo.class).where(cond).orderBy("count", false).limit(3).build();
        query.runQueryInBackground(new DroiQueryCallback<AppInfo>() {
            @Override
            public void result(List<AppInfo> list, DroiError droiError) {
                mHotAppAdapter.notifyDataSetChanged();
                if (droiError.isOk()) {
                    if (list.size() > 0) {
                        mHotAppInfos.clear();
                        mHotAppInfos.addAll(list);
                        mHotAppAdapter.notifyDataSetChanged();
                    }
                } else {
                    //做请求失败处理
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(mContext, AppInfoDetailActivity.class);
        position = position - 1;
        intent.putExtra(AppInfoDetailActivity.APP_INFO_ID, mAppInfos.get(position).getAppId());
        startActivity(intent);
    }
}
