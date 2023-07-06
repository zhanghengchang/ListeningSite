package com.boll.audiobook.hear.utils;

import android.util.Log;

import com.boll.audiobook.hear.BuildConfig;

public class LogUtil {

    public static void i(String tag, String msg) {
        if (BuildConfig.BUILD_TYPE.equals("debug")) {
            Log.i(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (BuildConfig.BUILD_TYPE.equals("debug")) {
            Log.d(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (BuildConfig.BUILD_TYPE.equals("debug")) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (BuildConfig.BUILD_TYPE.equals("debug")) {
            Log.e(tag, msg);
        }
    }
}
