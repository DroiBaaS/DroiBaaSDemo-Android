package com.droi.sdk.droibaasdemo.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.core.DroiUser;
import com.droi.sdk.droibaasdemo.R;
import com.droi.sdk.droibaasdemo.activities.DownloadActivity;
import com.droi.sdk.droibaasdemo.activities.LoginActivity;
import com.droi.sdk.droibaasdemo.activities.ProfileActivity;
import com.droi.sdk.droibaasdemo.models.MyUser;
import com.droi.sdk.droibaasdemo.views.RoundImageView;
import com.droi.sdk.feedback.DroiFeedback;
import com.droi.sdk.selfupdate.DroiUpdate;
import com.squareup.picasso.Picasso;

/**
 * Created by chenpei on 2016/5/12.
 */
public class MineFragment extends Fragment implements View.OnClickListener {
    private static String TAG = "MineFragment";
    private Context mContext;
    private RoundImageView titleImg;
    private TextView nameTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        initUI(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshView();
        DroiAnalytics.onFragmentStart(getActivity(), "MineFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        DroiAnalytics.onFragmentEnd(getActivity(), "MineFragment");
    }

    /**
     * 当登录成功或者登出时刷新View
     */
    private void refreshView() {
        MyUser user = DroiUser.getCurrentUser(MyUser.class);
        if (user != null && user.isAuthorized() && !user.isAnonymous()) {
            nameTextView.setText(user.getUserId());
            if (user.getHeadIcon() != null) {
                user.getHeadIcon().getUriInBackground(new DroiCallback<Uri>() {
                    @Override
                    public void result(Uri uri, DroiError droiError) {
                        if (droiError.isOk()){
                            Picasso.with(mContext).load(uri).into(titleImg);
                        }
                    }
                });
            }
        } else {
            titleImg.setImageResource(R.drawable.profile_default_icon);
            nameTextView.setText(R.string.fragment_mine_login);
        }
    }

    private void initUI(View view) {
        view.findViewById(R.id.mine_frag_account).setOnClickListener(this);
        view.findViewById(R.id.mine_frag_update).setOnClickListener(this);
        view.findViewById(R.id.mine_frag_feedback).setOnClickListener(this);
        view.findViewById(R.id.mine_frag_download).setOnClickListener(this);
        view.findViewById(R.id.mine_frag_upload).setOnClickListener(this);
        view.findViewById(R.id.head_icon).setOnClickListener(this);
        titleImg = (RoundImageView) view.findViewById(R.id.head_icon);
        nameTextView = (TextView) view.findViewById(R.id.user_name);
    }

    @Override
    public void onClick(View v) {
        MyUser user = DroiUser.getCurrentUser(MyUser.class);
        switch (v.getId()) {
            case R.id.head_icon:
                if (user != null && user.isAuthorized() && !user.isAnonymous()) {
                    toProfile();
                } else {
                    toLogin();
                }
                break;
            case R.id.mine_frag_account:
                if (user != null && user.isAuthorized() && !user.isAnonymous()) {
                    toProfile();
                } else {
                    toLogin();
                }
                break;
            case R.id.mine_frag_update:
                //手动更新
                DroiUpdate.manualUpdate(mContext);
                break;
            case R.id.mine_frag_feedback:
                //如果使用自己的账户系统
                //DroiFeedback.setUserId("userId");
                //自定义部分颜色
                //DroiFeedback.setTitleBarColor(Color.GREEN);
                //DroiFeedback.setSendButtonColor(Color.GREEN,Color.GREEN);
                //打开反馈页面
                DroiFeedback.callFeedback(mContext);
                break;
            case R.id.mine_frag_download:
                toDownload();
                break;
            case R.id.mine_frag_upload:
                break;
            default:
                break;
        }
    }

    /**
     * 转到下载管理页面
     */
    private void toDownload() {
        Intent downloadIntent = new Intent(mContext, DownloadActivity.class);
        startActivity(downloadIntent);
    }

    /**
     * 转到登录页面
     */
    private void toLogin() {
        Intent loginIntent = new Intent(mContext, LoginActivity.class);
        startActivity(loginIntent);
    }

    /**
     * 转到个人信息页面
     */
    private void toProfile() {
        Intent profileIntent = new Intent(mContext, ProfileActivity.class);
        startActivity(profileIntent);
    }
}
