package com.droi.sdk.droibaasdemo.utils;

import android.text.TextUtils;

/**
 * Created by yaojiaqing on 2017/7/13.
 */

public class StringUtil {
    public static final boolean isStringNullOrEmpty(String str) {
        return (str == null || TextUtils.isEmpty(str.trim()));
    }
}
