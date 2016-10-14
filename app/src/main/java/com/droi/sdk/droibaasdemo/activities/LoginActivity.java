package com.droi.sdk.droibaasdemo.activities;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.droibaasdemo.R;
import com.droi.sdk.droibaasdemo.fragments.LoginFragment;

public class LoginActivity extends FragmentActivity {
    static FragmentManager fm;
    static Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_login);
        fm = getSupportFragmentManager();
        displayLoginFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DroiAnalytics.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DroiAnalytics.onPause(this);
    }

    private static void displayLoginFragment() {
        FragmentTransaction transaction = fm.beginTransaction();
        Fragment loginFragment = new LoginFragment();
        transaction.replace(R.id.droi_login_container, loginFragment);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

