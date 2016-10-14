package com.droi.sdk.droibaasdemo.models;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiObject;

/**
 * Created by chenpei on 16/7/6.
 */
public class Add {
    // Input
    public static class Request extends DroiObject {
        @DroiExpose
        public float num1;

        @DroiExpose
        public float num2;
    }

    // Output
    public static class Response extends DroiObject {
        @DroiExpose
        public float result;
    }
}