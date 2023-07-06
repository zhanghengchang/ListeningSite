package com.boll.audiobook.hear.app;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.boll.audiobook.hear.evs.utils.EvsSdk;
import com.boll.audiobook.hear.utils.HeadUtil;
import com.github.gzuliyujiang.oaid.DeviceIdentifier;

import org.litepal.LitePal;

/**
 * created by zoro at 2023/5/8
 */
public class MyApplication extends Application {

    public static MyApplication mInstance;
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        DeviceIdentifier.register(this);
        MultiDex.install(this);

        mInstance = this;
        context = getApplicationContext();
        EvsSdk.Companion.initAuth(this, HeadUtil.getSerialNumber(), "d0bfad2a-0603-4798-a1d7-2cf3d9f2fab1");

        LitePal.initialize(this);
    }

}
