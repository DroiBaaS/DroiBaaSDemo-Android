package com.droi.sdk.droibaasdemo.activities;

import android.support.v7.app.AppCompatActivity;

import com.droi.sdk.analytics.DroiAnalytics;

/**
 * Created by chenpei on 2016/5/11.
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onResume() {
        super.onResume();
        //方式二:
        DroiAnalytics.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //方式二:
        DroiAnalytics.onPause(this);
    }
}
