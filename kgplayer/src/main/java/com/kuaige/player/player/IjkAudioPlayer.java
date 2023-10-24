package com.kuaige.player.player;

import android.media.AudioManager;
import android.os.Build;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class IjkAudioPlayer {

    private IjkMediaPlayer ijkMediaPlayer;

    public IjkMediaPlayer getPlayer() {
        return ijkMediaPlayer;
    }

    public void init() {
        if (ijkMediaPlayer == null) {
            ijkMediaPlayer = new IjkMediaPlayer();
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", 0);
            // >6.0的情况 使用 避免变速变调
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", 0);
//            } else {
//                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", 1);
//            }
        }
        ijkMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public void setPathAndPrepare(String uri) {
        try {
            ijkMediaPlayer.setDataSource(uri);
            ijkMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long getCurrentPosition() {
        if (ijkMediaPlayer != null) {
            return ijkMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public long getDuration() {
        if (ijkMediaPlayer != null) {
            return ijkMediaPlayer.getDuration();
        }
        return 0;
    }

    public void seekTo(long progress) {
        if (ijkMediaPlayer != null) {
            ijkMediaPlayer.seekTo(progress);
        }
    }

    public void setSpeed(float speed) {
        if (ijkMediaPlayer != null) {
            ijkMediaPlayer.setSpeed(speed);
        }
    }

    public boolean isPlaying() {
        if (ijkMediaPlayer != null) {
            return ijkMediaPlayer.isPlaying();
        }
        return false;
    }

    public void start() {
        if (ijkMediaPlayer != null) {
            ijkMediaPlayer.start();
        }
    }

    public void pause() {
        if (ijkMediaPlayer != null) {
            ijkMediaPlayer.pause();
        }
    }

    public void stop() {
        if (ijkMediaPlayer != null) {
            ijkMediaPlayer.stop();
        }
    }

    /**
     * 耗时
     */
    public void reset() {
        if (ijkMediaPlayer != null) {
            ijkMediaPlayer.reset();
        }
    }

    public void release() {
        if (ijkMediaPlayer != null) {
            ijkMediaPlayer.reset();
            ijkMediaPlayer.release();
            ijkMediaPlayer = null;
        }
    }

}
