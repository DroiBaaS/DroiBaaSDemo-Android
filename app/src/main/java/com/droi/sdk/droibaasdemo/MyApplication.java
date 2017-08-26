package com.droi.sdk.droibaasdemo;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.droi.sdk.DroiError;
import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.analytics.SendPolicy;
import com.droi.sdk.core.Core;
import com.droi.sdk.core.DroiCloudCache;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.core.DroiPermission;
import com.droi.sdk.core.DroiPreference;
import com.droi.sdk.droibaasdemo.models.Add;
import com.droi.sdk.droibaasdemo.models.AppInfo;
import com.droi.sdk.droibaasdemo.models.AppType;
import com.droi.sdk.droibaasdemo.models.Banner;
import com.droi.sdk.droibaasdemo.models.MyUser;
import com.droi.sdk.feedback.DroiFeedback;
import com.droi.sdk.feedback.PicassoEngine;
import com.droi.sdk.oauth.DroiOauth;
import com.droi.sdk.push.DroiMessageHandler;
import com.droi.sdk.push.DroiPush;
import com.droi.sdk.selfupdate.DroiUpdate;
import com.droi.sdk.selfupdate.UpdateUIStyle;


import org.xutils.x;

import java.io.UnsupportedEncodingException;

/**
 * Created by chenpei on 2016/5/11.
 */
public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    private static Context mContext;
    private Toast toast = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Core");
        mContext = this;
        //初始化
        Core.initialize(this);
        //注册DroiObject
        DroiObject.registerCustomClass(AppInfo.class);
        DroiObject.registerCustomClass(AppType.class);
        DroiObject.registerCustomClass(MyUser.class);
        DroiObject.registerCustomClass(Banner.class);
        DroiObject.registerCustomClass(Add.Request.class);
        DroiObject.registerCustomClass(Add.Response.class);

        Log.i(TAG, "DroiPush");
        //初始化
        DroiPush.initialize(this, "tz7uaHGrJTTXfssSGzJexEXWznSjXZjonSkv8iyWF7WV4-IDLTjFoLJgBcmge0ZR");
        //设置静默时间为0:30 到 8:00
        DroiPush.setSilentTime(this, 0, 30, 8, 0);

        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                //使用DroiPreference设置是否打开push功能
                boolean enablePush = DroiPreference.instance().getBoolean("push", true);
                Log.i(TAG, "DroiPreference:" + enablePush);
                DroiPush.setPushableState(getApplicationContext(), enablePush);
                //DroiCloudCache
                // 设定数据
                DroiCloudCache.set("keyName", "54678");
                // 由云端取回设定数据
                DroiError error = new DroiError();
                String value = DroiCloudCache.get("keyName", error);
                Log.i(TAG, "DroiCloudCache:" + value);
            }
        };
        thread.start();

        //透传消息
        DroiPush.setMessageHandler(new DroiMessageHandler() {
            @Override
            public void onHandleCustomMessage(Context context, String s) {
                String st = decodeFromBase64Data(s);
                showToastInUiThread(st);
            }
        });

        Log.i(TAG, "DroiAnalytics");
        //初始化
        DroiAnalytics.initialize(this);
        //方式一:
        DroiAnalytics.enableActivityLifecycleCallbacks(this);
        //默认为true；关闭可以设为false
        DroiAnalytics.setCrashReport(true);
        //Logger必须在DroiAnalytics初始化后使用
        Log.i(TAG, "上传log");
        //发送策略，默认实时
        DroiAnalytics.setDefaultSendPolicy(SendPolicy.SCHEDULE);
        //设置非实时策略下，是否只在wifi下发送以及发送间隔(分钟)
        DroiAnalytics.setScheduleConfig(false, 5);

        Log.i(TAG, "DroiOauth");
        //初始化
        DroiOauth.initialize(this);
        //设置语言
        DroiOauth.setLanguage("zh_CN");

        Log.i(TAG, "DroiUpdate");
        //初始化
        DroiUpdate.initialize(this, "Mbz3QgAN3gg8hqkU8H1tpqhP-kh3jQ4_MrUW3amrQAtVJBXZUyyqWuM5i4UJsdf5");
        //是否只在wifi下更新，默认true
        DroiUpdate.setUpdateOnlyWifi(true);
        //UI类型，默认BOTH
        DroiUpdate.setUpdateUIStyle(UpdateUIStyle.STYLE_BOTH);

        Log.i(TAG, "DroiFeedback");
        //初始化
        DroiFeedback.initialize(this, "Yt9PtNjOfgBsdr3qZmkezvf2XTncJ9BRv70K02vaKE3HzqVrCa5QHvmMGqdnzoEJ");
        DroiFeedback.setImageEngine(new PicassoEngine());

        //权限设置
        DroiPermission permission = DroiPermission.getDefaultPermission();
        if (permission == null)
            permission = new DroiPermission();
        // 设置默认权限为所有用户可读不可写
        permission.setPublicReadPermission(true);
        permission.setPublicWritePermission(true);
        DroiPermission.setDefaultPermission(permission);

        // 设置目前登录的用户权限是可读可写
        /*DroiUser user = DroiUser.getCurrentUser();
        permission.setUserReadPermission(user.getObjectId(), true);
        permission.setUserWritePermission(user.getObjectId(), true);*/

        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);
    }

    private void showToastInUiThread(final String stringRes) {

        Handler mainThread = new Handler(Looper.getMainLooper());
        mainThread.post(new Runnable() {
            @Override
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(mContext.getApplicationContext(),
                            stringRes, Toast.LENGTH_SHORT);
                } else {
                    toast.setText(stringRes);
                }
                toast.show();
            }
        });
    }

    public static String decodeFromBase64Data(String data) {
        String result = null;
        if (TextUtils.isEmpty(data)) {
            return null;
        }

        byte[] bytes = Base64.decode(data, Base64.DEFAULT);
        try {
            result = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {

        }
        return result;
    }

}
