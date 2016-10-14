package com.droi.sdk.droibaasdemo.models;

import android.net.Uri;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiFile;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.core.DroiReference;

/**
 * Created by chenpei on 2016/5/11.
 */
public class AppType extends DroiObject {
    @DroiExpose
    private String name;
    @DroiExpose
    private String mainType;
    @DroiReference
    private DroiFile icon;

    private Uri iconUrl;

    public void setName(String name) {
        this.name = name;
    }

    public void setMainType(String mainType) {
        this.mainType = mainType;
    }

    public String getName() {
        return name;
    }

    public String getMainType() {
        return mainType;
    }

    public DroiFile getIcon() {
        return icon;
    }

    public void setIcon(DroiFile icon) {
        this.icon = icon;
    }

    public Uri getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(Uri iconUrl) {
        this.iconUrl = iconUrl;
    }
}
