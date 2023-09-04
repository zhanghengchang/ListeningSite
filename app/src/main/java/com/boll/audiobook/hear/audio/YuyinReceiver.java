package com.boll.audiobook.hear.audio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

public class YuyinReceiver extends BroadcastReceiver {

    private static String YUYIN_ACTION = "com.boll.action.YUYIN";
    private static YuyinReceiver receiver;

    private static OnYuYinToTextListener mOnYuYinToTextListener;

    public static void register(Context context) {
        if (null != context) {
            if (null == receiver) {
                receiver = new YuyinReceiver();
                IntentFilter itf = new IntentFilter(YUYIN_ACTION);
                context.registerReceiver(receiver, itf);
            }
        }
    }

    public static void unregister(Context context) {
        if (null != context) {
            if (null != receiver) {
                context.unregisterReceiver(receiver);
                receiver = null;
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String content = intent.getStringExtra("content");
        Log.e("YuyinReceiver", "content = " + content);
        mOnYuYinToTextListener.onYuYinToText(content);
    }

    public static void setOnYuYinToTextListener(OnYuYinToTextListener onYuYinToTextListener){
        mOnYuYinToTextListener = onYuYinToTextListener;
    }

    public interface OnYuYinToTextListener{
        void onYuYinToText(String content);
    }
}
