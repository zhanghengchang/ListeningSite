package com.boll.audiobook.hear.activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import com.boll.audiolib.util.TimeUtil;
import com.boll.audiobook.hear.R;
import com.boll.audiobook.hear.entity.LocalAudioBean;
import com.boll.audiobook.hear.service.PlayService;
import com.boll.audiobook.hear.utils.SaveDataUtil;
import com.boll.audiobook.hear.view.AudioProgressBar;
import com.boll.audiobook.hear.view.LocalAudioListDialog;
import com.boll.audiobook.hear.view.MarqueeTextView;
import com.boll.audiobook.hear.view.SettingDialog;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.mp3.MP3FileReader;
import org.jaudiotagger.tag.FieldKey;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 外部本地资源播放
 */
public class LocalPlayActivity extends BaseActivity implements MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, View.OnClickListener {

    private static final String TAG = "LocalPlayActivity";

    private Context mContext;

    private int position;
    private List<String> playPathList;
    private List<String> playTitles;

    private String playPath;

    private ImageView iconBack;
    private MarqueeTextView tvTitle;
    private ImageView iconSetting;
    private AudioProgressBar audioProgress;
    private LinearLayout llAudioList;

    private SettingDialog mSettingDialog;
    private LocalAudioListDialog mAudioListDialog;
    private List<LocalAudioBean> localAudioBeans;

    private static final int TIMING_CODE = 101;

    private boolean isPreDown;//上一曲按键是否按下
    private boolean isNextDown;//下一曲按键是否按下
    private boolean isPauseDown;//暂停键是否按下
    private boolean leftDown;//左键是否按下
    private boolean rightDown;//右键是否按下

    public int playCount;//记录播放了几曲

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == TIMING_CODE) {
                int timingClose = SaveDataUtil.getInstance(mContext).getInt("timingClose", 1);
                long startTiming = SaveDataUtil.getInstance(mContext).getLong("startTiming", 0);
                long endTiming = System.currentTimeMillis();
                if (timingClose == 1 || timingClose == 2
                        || timingClose == 3 || timingClose == 4) {
                    Log.d(TAG, "未设置定时关闭时间");
                } else {
//                    int diff = TimeUtil.diffToMin(timeDiff);
                    int diff = TimeUtil.getDiffTime(startTiming, endTiming);
                    Log.d(TAG, "已经播放: " + diff + "分钟");
                    if ((timingClose == 5 && diff >= 15)
                            || (timingClose == 6 && diff >= 20)
                            || (timingClose == 7 && diff >= 30)
                            || (timingClose == 8 && diff >= 60)) {
                        if (mPlayer != null) {
                            mPlayer.pause();
                        }
                        //记录是计时暂停 置一个标记
                        SaveDataUtil.getInstance(mContext).putBoolean("isTimingPause", true);
                        Log.e(TAG, "计时暂停");
                    }
                }
                handler.sendEmptyMessageDelayed(TIMING_CODE, 60 * 1000);
            }
            return false;
        }
    });

    private MediaPlayer mPlayer;
    private boolean hasPrepared;

    private void initIfNecessary() {
        if (null == mPlayer) {
            mPlayer = new MediaPlayer();
            mPlayer.setOnErrorListener(this);
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnPreparedListener(this);
        }
    }

    /**
     * 播放在线或本地MP3
     *
     * @param path
     */
    public void play(String path) {
        hasPrepared = false; // 开始播放前将flag置为不可操作
        try {
            mPlayer.reset();
            mPlayer.setDataSource(path);
            mPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放或暂停
     */
    public void playOrPause() {
        if (mPlayer.isPlaying()) {
            pause();
        } else {
            start();
        }
    }

    public void start() {
        if (null != mPlayer && hasPrepared) {
            boolean lastPause = SaveDataUtil.getInstance(mContext).getBoolean("isTimingPause", false);
            if (lastPause) {
                Log.e(TAG, "继续播放 重新计时");
                //如果上次是计时暂停，则此次重新开始计时
                long startTiming = System.currentTimeMillis();
                SaveDataUtil.getInstance(mContext).putLong("startTiming", startTiming);
                SaveDataUtil.getInstance(mContext).putBoolean("isTimingPause", false);
            }
            mPlayer.start();
            int duration = mPlayer.getDuration();
            String totalTime = TimeUtil.formatTime(duration);
            audioProgress.setMax((int) duration);
            audioProgress.setProgress(0);
            audioProgress.setTotalTime(totalTime);
        }
    }

    public void pause() {
        if (null != mPlayer && hasPrepared) {
            mPlayer.pause();
        }
    }

    public void seekTo(int position) {
        if (null != mPlayer && hasPrepared) {
            mPlayer.seekTo(position);
        }
    }


    /**
     * 设置播放倍速
     *
     * @param speed
     */
    public void setPlayerSpeed(float speed) {
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.M) {
            PlaybackParams playbackParams = mPlayer.getPlaybackParams();
            playbackParams.setSpeed(speed);
            mPlayer.setPlaybackParams(playbackParams);
        }
    }

    public void release() {
        hasPrepared = false;
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        hasPrepared = true;
        start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        hasPrepared = false;
        boolean lastPause = SaveDataUtil.getInstance(mContext).getBoolean("isTimingPause", false);
        if (lastPause) return;
        int timingClose = SaveDataUtil.getInstance(mContext).getInt("timingClose", 1);
        if (timingClose == 2 || timingClose == 3 || timingClose == 4) {
            playCount++;
        }
        Log.e(TAG, "playCount: " + playCount);
        if ((timingClose == 2 && playCount >= 1)
                || (timingClose == 3 && playCount >= 2)
                || (timingClose == 4 && playCount >= 3)) {
            Log.e(TAG, "播放已达设置的暂停次数");
            playCount = 0;
            return;
        }
        int playMode = SaveDataUtil.getInstance(mContext).getInt("playMode", 1);
        if (playMode == 1) {
            //顺序播放
            position++;
        } else if (playMode == 2) {
            //单曲循环
        } else if (playMode == 3) {
            //随机播放
            Random rand = new Random();
            int randNumber = rand.nextInt(playPathList.size());
            position = randNumber;
        }
        Log.e(TAG, "position: " + position);
        if (position >= playPathList.size()) {
            position = 0;
        }
        playAudio(position);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        hasPrepared = false;
        return false;
    }


    @Override
    public void setStatus() {

    }

    @Override
    public int initLayout() {
        return R.layout.activity_local_play;
    }

    @Override
    public void initView() {
        mContext = this;
        iconBack = findViewById(R.id.icon_back);
        tvTitle = findViewById(R.id.tv_title);
        iconSetting = findViewById(R.id.icon_setting);
        audioProgress = findViewById(R.id.audioProgress);
        llAudioList = findViewById(R.id.ll_audio_list);
        iconBack.setOnClickListener(this);
        iconSetting.setOnClickListener(this);
        llAudioList.setOnClickListener(this);

        mSettingDialog = new SettingDialog(this, this);
        mSettingDialog.setOnDismissListener(new SettingDialog.OnDismissListener() {
            @Override
            public void onDismiss() {
                float speed = SaveDataUtil.getInstance(mContext).getFloat("playSpeed", 1.0f);
                setPlayerSpeed(speed);
            }
        });

        audioProgress.setOnSeekBarChangeListener(new AudioProgressBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (hasPrepared) {
                    seekTo(seekBar.getProgress());
                }
            }
        });
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        playPathList = intent.getStringArrayListExtra("playPathList");
        initIfNecessary();

        playTitles = new ArrayList<>();

        if (playPathList != null && playPathList.size() > 0) {
            if (PlayService.getInstance().isPlaying()) {
                PlayService.getInstance().pauseAudio();
            }
            for (int i = 0; i < playPathList.size(); i++) {
                playPath = playPathList.get(i);
                File file = new File(playPath);
                MP3FileReader reader = new MP3FileReader();
                AudioFile audioFile = null;
                try {
                    audioFile = reader.read(file);
                    String title = audioFile.getTag().getFirst(FieldKey.TITLE);
                    playTitles.add(title);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (playPath.contains("/")) {
                        String[] split = playPath.split("/");
                        String title = split[split.length - 1];
                        playTitles.add(title);
                    }
                }
            }
            localAudioBeans = new ArrayList<>();
            for (int i = 0; i < playTitles.size(); i++) {
                LocalAudioBean localAudioBean = new LocalAudioBean();
                localAudioBean.setNum(i + 1);
                localAudioBean.setPlaying(false);
                localAudioBean.setTitle(playTitles.get(i));
                localAudioBeans.add(localAudioBean);
            }
            mAudioListDialog = new LocalAudioListDialog(mContext,this,localAudioBeans,position);
            playAudio(position);
            handler.post(playRunnable);
            handler.sendEmptyMessage(TIMING_CODE);
        } else {
            showToast("没有找到要播放的资源");
        }
    }

    public void playAudio(int position) {
        this.position = position;
        if (mAudioListDialog.isShowing()){
            mAudioListDialog.refreshList(position);
        }
        for (int i = 0; i < playPathList.size(); i++) {
            if (i == position) {
                playPath = playPathList.get(i);
            }
        }
        Log.e(TAG, "playPath: " + playPath);
        String title = playTitles.get(position);
        tvTitle.setText(title);
        play(playPath);
        float speed = SaveDataUtil.getInstance(mContext).getFloat("playSpeed", 1.0f);
        setPlayerSpeed(speed);
    }

    private Runnable playRunnable = new Runnable() {
        @Override
        public void run() {
            if (null != mPlayer && hasPrepared) {
                int currentPosition = mPlayer.getCurrentPosition();
                String currentTime = TimeUtil.formatTime(currentPosition);
                audioProgress.setProgress(currentPosition);
                audioProgress.setCurrentTime(currentTime);
            }
            handler.postDelayed(this, 100);
        }
    };

    /**
     * 上一曲
     */
    public void playLeft() {
        position--;
        if (position < 0) {
            position = playPathList.size() - 1;
        }
        playAudio(position);
    }

    /**
     * 下一曲
     */
    public void playRight() {
        position++;
        if (position < playPathList.size()) {
        } else {
            position = 0;
        }
        playAudio(position);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case 19://上一曲
                if (!isPreDown) {
                    playLeft();
                    isPreDown = true;
                }
                break;
            case 20://下一曲
                if (!isNextDown) {
                    playRight();
                    isNextDown = true;
                }
                break;
            case 21://上一句
                if (!leftDown) {
                    audioProgress.setEnable(false);
                    leftDown = true;
                }
                break;
            case 22://下一句
                if (!rightDown) {
                    audioProgress.setEnable(false);
                    rightDown = true;
                }
                break;
            case 66://暂停
                if (!isPauseDown) {
                    playOrPause();
                    isPauseDown = true;
                }
                break;
            default:
                break;
        }
        Log.d(TAG, "dispatchKeyEvent: " + event.getKeyCode());
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (event.getKeyCode()) {
            case 19://上一曲
                if (isPreDown) {
                    isPreDown = false;
                }
                break;
            case 20://下一曲
                if (isNextDown) {
                    isNextDown = false;
                }
                break;
            case 21://上一句
                if (leftDown) {
                    audioProgress.setEnable(true);
                    leftDown = false;
                }
                break;
            case 22://下一句
                if (rightDown) {
                    audioProgress.setEnable(true);
                    rightDown = false;
                }
                break;
            case 66://暂停
                if (isPauseDown) {
                    isPauseDown = false;
                }
                break;
            default:
                break;
        }
        Log.d(TAG, "onKeyUp: " + keyCode);
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.icon_back:
                finish();
                break;
            case R.id.icon_setting:
                mSettingDialog.showDialog();
                break;
            case R.id.ll_audio_list:
                mAudioListDialog.showDialog();
                mAudioListDialog.refreshList(position);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
    }

}