package com.droi.sdk.droibaasdemo.models;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiFile;
import com.droi.sdk.core.DroiUser;

/**
 * Created by chenpei on 2016/5/26.
 */
public class MyUser extends DroiUser {
    @DroiExpose
    private String phoneNumber;
    @DroiExpose
    private String address;
    @DroiExpose
    private DroiFile headIcon;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public DroiFile getHeadIcon() {
        return headIcon;
    }

    public void setHeadIcon(DroiFile headIcon) {
        this.headIcon = headIcon;
    }
}
