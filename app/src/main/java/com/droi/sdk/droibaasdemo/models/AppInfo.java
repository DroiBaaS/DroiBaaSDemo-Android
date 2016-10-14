package com.droi.sdk.droibaasdemo.models;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiFile;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.core.DroiQueryAnnotation;

/**
 * Created by chenpei on 2016/5/11.
 */
@DroiQueryAnnotation
public class AppInfo extends DroiObject {
    @DroiExpose
    private String appId;
    @DroiExpose
    private String icon;
    @DroiExpose
    private String name;
    @DroiExpose
    private int count;
    @DroiExpose
    private int rating;
    @DroiExpose
    private int size;
    @DroiExpose
    private String intro;
    @DroiExpose
    private String brief;
    /*    @DroiExpose
        public Date updateTime;*/
    @DroiExpose
    private String version;
    @DroiExpose
    private String apkUrl;
    @DroiExpose
    private String type;

    private DroiFile apkFile;
    @DroiExpose
    private String mainType;
    @DroiExpose
    private String imageUrl;

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setApkFile(DroiFile apkFile) {
        this.apkFile = apkFile;
    }

    public void setMainType(String mainType) {
        this.mainType = mainType;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAppId() {
        return appId;
    }

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public int getRating() {
        return rating;
    }

    public int getCount() {
        return count;
    }

    public int getSize() {
        return size;
    }

    public String getVersion() {
        return version;
    }

   /* public Date getUpdateTime() {
        return updateTime;
    }*/

    public String getIntro() {
        return intro;
    }

    public String getBrief() {
        return brief;
    }

    public String getType() {
        return type;
    }

    public DroiFile getApkFile() {
        return apkFile;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getMainType() {
        return mainType;
    }
}
