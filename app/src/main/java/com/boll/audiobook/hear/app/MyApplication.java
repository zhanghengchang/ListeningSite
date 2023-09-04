package com.boll.audiobook.hear.app;

import android.content.Context;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import com.github.gzuliyujiang.oaid.DeviceIdentifier;

/**
 * created by zoro at 2023/5/8
 */
public class MyApplication extends MultiDexApplication {

    public static MyApplication mInstance;
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        DeviceIdentifier.register(this);
        MultiDex.install(this);

        mInstance = this;
        context = getApplicationContext();
    }

}
