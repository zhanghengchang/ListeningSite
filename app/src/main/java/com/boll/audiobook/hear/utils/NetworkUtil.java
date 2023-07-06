package com.boll.audiobook.hear.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Set;

/**
 * @author Created by zhc on 2021/7/23
 */
public class NetworkUtil {

    private static final String TAG = "NetworkUtil";

    // 判断是否有网络连接
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static long getLastModified(String urlPath){
        URLConnection conn = null;
        try {
            URL url = new URL(urlPath);
            conn = url.openConnection();
            conn.setConnectTimeout(30 * 1000);
            conn.setReadTimeout(30 * 1000);
            Map headers = conn.getHeaderFields();
            Set<String> keys = headers.keySet();
            for( String key : keys ){
                String val = conn.getHeaderField(key);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conn.getLastModified();
    }

}
