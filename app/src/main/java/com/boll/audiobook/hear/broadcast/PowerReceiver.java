package com.boll.audiobook.hear.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.boll.audiobook.hear.utils.SaveDataUtil;

public class PowerReceiver extends BroadcastReceiver {

    private final String action_shutdown = Intent.ACTION_SHUTDOWN;
    private final String action_reboot = Intent.ACTION_REBOOT;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action_shutdown.equals(action)) {
            //关机之前将是否计时暂停置为true，再次播放就会清空上次开始播放时间
            SaveDataUtil.getInstance(context).putBoolean("isTimingPause", true);
            SaveDataUtil.getInstance(context).putLong("playStartTime",0);
        } else if (action_reboot.equals(action)) {
            //重启

        }
    }

}