package com.boll.audiobook.hear.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.boll.audiobook.hear.audio.util.TimeUtil;
import com.boll.audiobook.hear.network.request.DurationRequest;
import com.boll.audiobook.hear.network.response.BaseResponse;
import com.boll.audiobook.hear.network.retrofit.ListenerLoader;
import com.boll.audiobook.hear.utils.Const;
import com.boll.audiobook.hear.utils.SaveDataUtil;
import com.kuaige.player.player.IjkAudioPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * 后台播放服务
 */
public class PlayService extends Service implements /*MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener,*/
        IMediaPlayer.OnErrorListener, IMediaPlayer.OnPreparedListener, IMediaPlayer.OnCompletionListener {

    private static final String TAG = "PlayService";

    private Context mContext;
//    private MediaPlayer mPlayer;

    private IjkAudioPlayer ijkAudioPlayer;

    public boolean hasPrepared;

    private OnStateListener mOnStateListener;
    private List<OnCurrentPlayIdListener> mOnCurrentPlayIdListeners = new ArrayList<>();

    public int abState;//AB复读键的状态 0：未选中 1：选择A点 2：选择B点
    public boolean isSingle;//是否选中单句复读

    public boolean isCheckA;//是否已选中A点
    public boolean isCheckB;//是否已选中B点

    public int currentLine;//当前播放行数

    public int aPosition;
    public int bPosition;

    public long currentStartTime;//当前播放句子的开始时间
    public long currentEndTime;//当前播放句子的结束时间

    public int currentId;

    private boolean isPause;//记录当前是否是暂停状态

    public int playCount;//记录播放了几曲

    public boolean isFollow;

    private static final int TIMING_CODE = 101;

    private ListenerLoader mListenerLoader;

    private Handler mHandler = new Handler(Looper.getMainLooper(),new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            //计时定时关闭
            if (msg.what == TIMING_CODE) {
                int timingClose = SaveDataUtil.getInstance(mContext).getInt("timingClose", 1);
                long startTiming = SaveDataUtil.getInstance(mContext).getLong("startTiming", 0);
                long endTiming = System.currentTimeMillis();
                if (timingClose == 1 || timingClose == 2
                        || timingClose == 3 || timingClose == 4) {
                    Log.d(TAG, "未设置定时关闭时间");
                } else {
                    int diff = TimeUtil.getDiffTimeMin(startTiming, endTiming);
                    Log.d(TAG, "已经播放: " + diff + "分钟");
                    if ((timingClose == 5 && diff >= 15)
                            || (timingClose == 6 && diff >= 20)
                            || (timingClose == 7 && diff >= 30)
                            || (timingClose == 8 && diff >= 60)) {
//                        if (mPlayer != null) {
//                            mPlayer.pause();
//                        }
                        if(ijkAudioPlayer != null){
                            ijkAudioPlayer.pause();
                        }
                        //记录是计时暂停 置一个标记
                        SaveDataUtil.getInstance(mContext).putBoolean("isTimingPause", true);
                        Log.e(TAG, "计时暂停");
                    }
                }
                mHandler.sendEmptyMessageDelayed(TIMING_CODE, 60 * 1000);
            }
            return false;
        }
    });

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
        hasPrepared = false;
        Log.e(TAG, "onError: " + i);
        return false;
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        hasPrepared = true;
        start();
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        if (hasPrepared) {
            hasPrepared = false;
            mOnStateListener.onCompletion();
        }
    }

    private static class SingletonHolder {
        private static final PlayService INSTANCE = new PlayService();
    }

    public static PlayService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initIfNecessary();
        Log.d(TAG, "初始化播放服务");
        mContext = this;
        mHandler.sendEmptyMessage(TIMING_CODE);
        Const.isStartPlayService = true;
    }

    private void initIfNecessary() {
//        if (null == mPlayer) {
//            mPlayer = new MediaPlayer();
//            mPlayer.setOnErrorListener(this);
//            mPlayer.setOnCompletionListener(this);
//            mPlayer.setOnPreparedListener(this);
//        }
        if (ijkAudioPlayer == null) {
            ijkAudioPlayer = new IjkAudioPlayer();
            ijkAudioPlayer.init();
            ijkAudioPlayer.getPlayer().setOnErrorListener(this);
            ijkAudioPlayer.getPlayer().setOnPreparedListener(this);
            ijkAudioPlayer.getPlayer().setOnCompletionListener(this);
        }
    }

    /**
     * 是否正在播放
     *
     * @return
     */
    public boolean isPlaying() {
//        if (null != mPlayer && hasPrepared) {
//            return mPlayer.isPlaying();
//        }
        if (null != ijkAudioPlayer && hasPrepared) {
            return ijkAudioPlayer.getPlayer().isPlaying();
        }
        return false;
    }

    /**
     * 设置播放倍速
     *
     * @param speed
     */
    public void setPlayerSpeed(float speed) {
//        if (android.os.Build.VERSION.SDK_INT >=
//                android.os.Build.VERSION_CODES.M) {
//            PlaybackParams playbackParams = mPlayer.getPlaybackParams();
//            playbackParams.setSpeed(speed);
//            mPlayer.setPlaybackParams(playbackParams);
//        }
        if (ijkAudioPlayer != null){
            ijkAudioPlayer.setSpeed(speed);
        }
    }

    /**
     * 播放在线或本地MP3
     *
     * @param url
     * @param id  当前播放音频的id
     */
    public void play(String url, int id) {
        float speed = SaveDataUtil.getInstance(mContext).getFloat("playSpeed", 1.0f);
        Log.d(TAG, "playUrl: " + url);
        hasPrepared = false;// 开始播放前将flag置为不可操作
        currentId = id;
        if (mOnCurrentPlayIdListeners.size() > 0) {
            for (int i = 0; i < mOnCurrentPlayIdListeners.size(); i++) {
                mOnCurrentPlayIdListeners.get(i).onCurrentPlayId(id);
            }
        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    mPlayer.reset();
//                    mPlayer.setDataSource(url);
//                    mPlayer.prepareAsync();
//                    setPlayerSpeed(speed);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
        initIfNecessary();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ijkAudioPlayer.reset();
                    ijkAudioPlayer.setPathAndPrepare(url);
                    setPlayerSpeed(speed);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 播放或暂停
     */
    public void playOrPause() {
        if (isPlaying()) {
            pause();
        } else {
            start();
        }
    }

    /**
     * 上一曲
     */
    public void playLeft() {
//        if (null != mPlayer && hasPrepared) {
//            mOnStateListener.onPlayLeft();
//        }
        if (null != ijkAudioPlayer && hasPrepared) {
            mOnStateListener.onPlayLeft();
        }
    }

    /**
     * 下一曲
     */
    public void playRight() {
//        if (null != mPlayer && hasPrepared) {
//            mOnStateListener.onPlayRight();
//        }
        if (null != ijkAudioPlayer && hasPrepared) {
            mOnStateListener.onPlayRight();
        }
    }

    /**
     * 上一句
     */
    public void playSentenceLeft() {
//        if (null != mPlayer && hasPrepared) {
//            mOnStateListener.onSentenceLeft();
//        }
        if (null != mOnStateListener && hasPrepared) {
            mOnStateListener.onSentenceLeft();
        }
    }

    /**
     * 下一句
     */
    public void playSentenceRight() {
//        if (null != mPlayer && hasPrepared) {
//            mOnStateListener.onSentenceRight();
//        }
        if (null != ijkAudioPlayer && hasPrepared) {
            mOnStateListener.onSentenceRight();
        }
    }

    /**
     * 开始播放
     */
    public void start() {
//        if (null != mPlayer && hasPrepared) {
//            boolean lastPause = SaveDataUtil.getInstance(mContext).getBoolean("isTimingPause", false);
//            long playStartTime = SaveDataUtil.getInstance(mContext).getLong("playStartTime", 0);
//            if (playStartTime == 0) {
//                long startTime = System.currentTimeMillis();
//                SaveDataUtil.getInstance(mContext).putLong("playStartTime", startTime);
//            }
//            if (lastPause) {
//                Log.e(TAG, "继续播放 重新计时");
//                //如果上次是计时暂停，则此次重新开始计时
//                long startTiming = System.currentTimeMillis();
//                SaveDataUtil.getInstance(mContext).putLong("startTiming", startTiming);
//                SaveDataUtil.getInstance(mContext).putBoolean("isTimingPause", false);
//            }
//            mPlayer.start();
//            isPause = false;
//        }
        if (null != ijkAudioPlayer && hasPrepared) {
            boolean lastPause = SaveDataUtil.getInstance(mContext).getBoolean("isTimingPause", false);
            long playStartTime = SaveDataUtil.getInstance(mContext).getLong("playStartTime", 0);
            if (playStartTime == 0) {
                long startTime = System.currentTimeMillis();
                SaveDataUtil.getInstance(mContext).putLong("playStartTime", startTime);
            }
            if (lastPause) {
                Log.e(TAG, "继续播放 重新计时");
                //如果上次是计时暂停，则此次重新开始计时
                long startTiming = System.currentTimeMillis();
                SaveDataUtil.getInstance(mContext).putLong("startTiming", startTiming);
                SaveDataUtil.getInstance(mContext).putBoolean("isTimingPause", false);
            }
            ijkAudioPlayer.start();
            isPause = false;
        }
    }

    /**
     * 暂停并上传本次听音时长
     */
    public void pause() {
//        if (null != mPlayer && hasPrepared) {
//            long playStartTime = SaveDataUtil.getInstance(mContext).getLong("playStartTime", 0);
//            long endTime = System.currentTimeMillis();
//            if (playStartTime != 0){
//                int diff = TimeUtil.getDiffTimeMin(playStartTime, endTime);
//                Log.e(TAG, "diff: " + diff);
//                if (diff > 1) {
//                    int diffSec = TimeUtil.getDiffTimeSec(playStartTime, endTime);
//                    Log.e(TAG, "playStartTime: " + playStartTime + ",endTime: " + endTime);
//                    DurationRequest request = new DurationRequest();
//                    request.setDuration(diffSec);
//                    mListenerLoader = ListenerLoader.getInstance();
//                    SaveDataUtil.getInstance(mContext).putLong("playStartTime", 0);
//                    mListenerLoader.addAudioDuration(request).subscribe(new Action1<BaseResponse>() {
//                        @Override
//                        public void call(BaseResponse response) {
//                            if (response.getCode() == 0) {
//                                Log.e(TAG, "已上报听音时长" + diffSec + "秒");
//                            } else {
//                                Log.e(TAG, response.getMsg());
//                            }
//                        }
//                    }, new Action1<Throwable>() {
//                        @Override
//                        public void call(Throwable throwable) {
//                            throwable.printStackTrace();
//                        }
//                    });
//                }
//            }
//            mPlayer.pause();
//            isPause = true;
//        }
        if (null != ijkAudioPlayer && hasPrepared) {
            long playStartTime = SaveDataUtil.getInstance(mContext).getLong("playStartTime", 0);
            long endTime = System.currentTimeMillis();
            if (playStartTime != 0){
                int diff = TimeUtil.getDiffTimeMin(playStartTime, endTime);
                Log.e(TAG, "diff: " + diff);
                if (diff > 1) {
                    int diffSec = TimeUtil.getDiffTimeSec(playStartTime, endTime);
                    Log.e(TAG, "playStartTime: " + playStartTime + ",endTime: " + endTime);
                    DurationRequest request = new DurationRequest();
                    request.setDuration(diffSec);
                    mListenerLoader = ListenerLoader.getInstance();
                    SaveDataUtil.getInstance(mContext).putLong("playStartTime", 0);
                    mListenerLoader.addAudioDuration(request).subscribe(new Action1<BaseResponse>() {
                        @Override
                        public void call(BaseResponse response) {
                            if (response.getCode() == 0) {
                                Log.e(TAG, "已上报听音时长" + diffSec + "秒");
                            } else {
                                Log.e(TAG, response.getMsg());
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    });
                }
            }
            ijkAudioPlayer.pause();
            isPause = true;
        }
    }

    /**
     * 单纯暂停播放
     */
    public void pauseAudio() {
//        if (null != mPlayer && hasPrepared) {
//            mPlayer.pause();
//            isPause = true;
//        }
        if (null != ijkAudioPlayer && hasPrepared) {
            ijkAudioPlayer.pause();
            isPause = true;
        }
    }

    /**
     * 播放当前句子
     */
    public void playCurrent() {
//        if (null != mPlayer && hasPrepared) {
//            isFollow = true;
//            mPlayer.seekTo((int) currentStartTime);
//            if (!mPlayer.isPlaying()) {
//                mPlayer.start();
//            }
//        }
        if (null != ijkAudioPlayer && hasPrepared) {
            isFollow = true;
            ijkAudioPlayer.seekTo((int) currentStartTime);
            if (!ijkAudioPlayer.isPlaying()) {
                ijkAudioPlayer.start();
            }
        }
    }

    /**
     * 判断是否暂停
     *
     * @return
     */
    public boolean isPause() {
        return isPause;
    }

    /**
     * 跳转制定位置播放
     *
     * @param position
     */
    public void seekTo(int position) {
//        if (null != mPlayer && hasPrepared) {
//            mPlayer.seekTo(position);
//        }
        if (null != ijkAudioPlayer && hasPrepared) {
            ijkAudioPlayer.seekTo(position);
        }
    }

    /**
     * 停止播放
     */
    public void stop() {
//        if (null != mPlayer && hasPrepared) {
//            mPlayer.stop();
//        }
        if (null != ijkAudioPlayer && hasPrepared) {
            ijkAudioPlayer.stop();
        }
    }

    /**
     * 释放播放器
     */
    public void release() {
        hasPrepared = false;
//        mPlayer.stop();
//        mPlayer.release();
//        mPlayer = null;
        ijkAudioPlayer.stop();
        ijkAudioPlayer.release();
        ijkAudioPlayer = null;
    }

    /**
     * 获取当前播放进度
     *
     * @return
     */
    public long getCurrentPosition() {
//        if (null != mPlayer && hasPrepared) {
//            return mPlayer.getCurrentPosition();
//        }
        if (null != ijkAudioPlayer && hasPrepared) {
            return ijkAudioPlayer.getCurrentPosition();
        }
        return 0;
    }

    public long getDuration(){
//        if (null != mPlayer && hasPrepared) {
//            return mPlayer.getDuration();
//        }
        if (null != ijkAudioPlayer && hasPrepared) {
            return ijkAudioPlayer.getDuration();
        }
        return 0;
    }

//    @Override
//    public void onPrepared(MediaPlayer mp) {
//        hasPrepared = true;
//        start();
//    }
//
//    @Override
//    public void onCompletion(MediaPlayer mp) {
//        if (hasPrepared) {
////            hasPrepared = false;
//            mOnStateListener.onCompletion();
//        }
//    }
//
//    @Override
//    public boolean onError(MediaPlayer mp, int what, int extra) {
//        hasPrepared = false;
//        Log.e(TAG, "onError: " + what);
//        return false;
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setOnStateListener(OnStateListener onStateListener) {
        mOnStateListener = onStateListener;
    }

    public void addOnCurrentPlayIdListener(OnCurrentPlayIdListener onCurrentPlayIdListener) {
        mOnCurrentPlayIdListeners.add(onCurrentPlayIdListener);
    }

    public void removeOnCurrentPlayIdListener(OnCurrentPlayIdListener onCurrentPlayIdListener) {
        mOnCurrentPlayIdListeners.remove(onCurrentPlayIdListener);
    }

    public interface OnStateListener {

        /**
         * 一曲播放结束
         */
        void onCompletion();

        /**
         * 播放上一曲
         */
        void onPlayLeft();

        /**
         * 播放下一曲
         */
        void onPlayRight();

        /**
         * 播放上一句
         */
        void onSentenceLeft();

        /**
         * 播放下一句
         */
        void onSentenceRight();
    }

    public interface OnCurrentPlayIdListener {

        /**
         * 当前播放音频Id
         *
         * @param currentId
         */
        void onCurrentPlayId(int currentId);

    }

}