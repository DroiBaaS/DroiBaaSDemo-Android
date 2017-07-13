package com.droi.sdk.droibaasdemo.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.PermissionChecker;
import android.widget.Toast;

/**
 * Created by yaojiaqing on 2017/7/13.
 */

public class Util {

    protected static boolean hasStoragePermission(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasWriteExternalStoragePermission = PermissionChecker
                    .checkSelfPermission(context, "android.permission.WRITE_EXTERNAL_STORAGE");
            return hasWriteExternalStoragePermission == PackageManager.PERMISSION_GRANTED;
        } else {
            return checkPermissions(context, "android.permission.WRITE_EXTERNAL_STORAGE");
        }
    }

    /**
     * Check Permissions.
     *
     * @param context
     * @param permission
     * @return true or false
     */
    protected static boolean checkPermissions(Context context, String permission) {
        PackageManager pm = context.getPackageManager();
        return pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
    }


    public static void showMessage(Context context, int stringId) {
        CustomToast.showToast(context, stringId, Toast.LENGTH_LONG);
    }

    public static void showMessage(Context context, String msg) {
        CustomToast.showToast(context, msg, Toast.LENGTH_LONG);
    }

    public static void showMessage(Context context, CharSequence msg) {
        CustomToast.showToast(context, String.valueOf(msg), Toast.LENGTH_LONG);
    }

    private static class CustomToast {

        private static Toast mToast;
        private static Handler mHandler;
        private static Runnable r = new Runnable() {
            public void run() {
                mToast.cancel();
            }
        };

        public static void showToast(Context context, String text, int duration) {
            int delayMillis = 1000;

            if(context == null){
                return;
            }

            if (mHandler == null) {
                mHandler = new Handler(context.getMainLooper());
            }

            mHandler.removeCallbacks(r);
            if (mToast != null) {
                mToast.setText(text);
            } else {
                mToast = Toast.makeText(context, text, duration);
            }
            mHandler.removeCallbacks(r);
            mHandler.postDelayed(r, delayMillis);

            mToast.show();
        }

        public static void showToast(Context mContext, int resId, int duration) {
            showToast(mContext, mContext.getResources().getString(resId), duration);
        }
    }

}
