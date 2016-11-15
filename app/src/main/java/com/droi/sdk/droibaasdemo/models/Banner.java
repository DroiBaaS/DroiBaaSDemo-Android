package com.droi.sdk.droibaasdemo.models;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;

/**
 * Created by chenpei on 2016/5/11.
 */
public class Banner extends DroiObject {
    @DroiExpose
    private String appId;
    @DroiExpose
    private String imgUrl;

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getAppId() {
        return appId;
    }
}
