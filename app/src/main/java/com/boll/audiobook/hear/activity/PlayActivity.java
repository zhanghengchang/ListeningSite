package com.boll.audiobook.hear.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewpager2.widget.ViewPager2;

import com.boll.audiolib.entity.LrcEntry;
import com.boll.audiolib.util.DownloadUtil;
import com.boll.audiolib.util.GetContentUtil;
import com.boll.audiolib.util.SrtToLrcUtil;
import com.boll.audiolib.util.TimeUtil;
import com.boll.audiolib.view.LrcView;
import com.boll.audiobook.hear.R;
import com.boll.audiobook.hear.adapter.ContentPagerAdapter;
import com.boll.audiobook.hear.entity.AudioBean;
import com.boll.audiobook.hear.fragment.CaptionFragment;
import com.boll.audiobook.hear.fragment.CoverFragment;
import com.boll.audiobook.hear.network.request.RecentPlayRequest;
import com.boll.audiobook.hear.network.response.AudioResponse;
import com.boll.audiobook.hear.network.response.BaseResponse;
import com.boll.audiobook.hear.network.response.ResUrlResponse;
import com.boll.audiobook.hear.network.retrofit.ListenerLoader;
import com.boll.audiobook.hear.service.PlayService;
import com.boll.audiobook.hear.utils.Const;
import com.boll.audiobook.hear.utils.HeadUtil;
import com.boll.audiobook.hear.utils.SaveDataUtil;
import com.boll.audiobook.hear.utils.TokenUtil;
import com.boll.audiobook.hear.utils.UUIDHexGenerator;
import com.boll.audiobook.hear.view.AudioListDialog;
import com.boll.audiobook.hear.view.AudioProgressBar;
import com.boll.audiobook.hear.view.CollectDialog;
import com.boll.audiobook.hear.view.SearchWordDialog;
import com.boll.audiobook.hear.view.SettingDialog;
import com.github.gzuliyujiang.oaid.DeviceID;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rx.functions.Action1;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PlayActivity";

    private Context mContext;

    private TextView tvTitle;
    private ImageView iconBack;
    private ImageView iconSetting;
    private AudioProgressBar mAudioProgressBar;
    private LinearLayout llAudioCollect;
    private LinearLayout llFollow;
    private LinearLayout llAb;
    private LinearLayout llSingle;
    private LinearLayout llAudioList;
    private ImageView iconCollect;
    private ImageView iconAb;
    private ImageView iconSingle;

    private int audioId;//当前播放音频的id
    private int albumId;//当前专辑id
    private String title;//标题
    private String coverUrl;//封面url
    private String audioUrl;//音频地址
    private String captionUrl;//字幕地址
    private int position;//当前音频列表播放位置
    private List<AudioResponse> audioList;
    private List<AudioBean> mAudioBeans;

    private CollectDialog mCollectDialog;
    private AudioListDialog mAudioListDialog;
    private SearchWordDialog mSearchWordDialog;
    private SettingDialog mSettingDialog;

    private boolean isCollect;//是否是收藏状态

    private boolean isPreDown;//上一曲按键是否按下
    private boolean isNextDown;//下一曲按键是否按下
    private boolean leftDown;//上一句按键是否按下
    private boolean rightDown;//下一句按键是否按下
    private boolean isPauseDown;//暂停键是否按下
    private boolean isSingleDown;//单句复读按键是否按下

    private ExecutorService mExecutorService;

    private String totalTime;//音频总时长
    private long totalDuration;

    private Handler handler = new Handler();

    private ListenerLoader mListenerLoader;

    private PlayService mPlayService;

    private int repeatCount;//记录复读次数

    private CoverFragment coverFragment;
    private CaptionFragment captionFragment;

    private ViewPager2 viewPager;
    private ContentPagerAdapter mContentPagerAdapter;
    private List<Fragment> fragments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_play);
        initView();
        initData();
    }

    public void initView() {
        mContext = this;
        tvTitle = findViewById(R.id.tv_title);
        iconBack = findViewById(R.id.icon_back);
        iconSetting = findViewById(R.id.icon_setting);
        viewPager = findViewById(R.id.viewPager);
        mAudioProgressBar = findViewById(R.id.audioProgress);
        llAudioCollect = findViewById(R.id.ll_audio_collect);
        llFollow = findViewById(R.id.ll_follow);
        llAb = findViewById(R.id.ll_ab);
        llSingle = findViewById(R.id.ll_single);
        llAudioList = findViewById(R.id.ll_audio_list);
        iconCollect = findViewById(R.id.icon_collect);
        iconAb = findViewById(R.id.icon_ab);
        iconSingle = findViewById(R.id.icon_single);
        iconBack.setOnClickListener(this);
        iconSetting.setOnClickListener(this);
        llAudioCollect.setOnClickListener(this);
        llFollow.setOnClickListener(this);
        llAb.setOnClickListener(this);
        llSingle.setOnClickListener(this);
        llAudioList.setOnClickListener(this);

        Intent intent = getIntent();
        albumId = intent.getIntExtra("albumId", 0);
        title = intent.getStringExtra("title");
        coverUrl = intent.getStringExtra("coverUrl");
        position = intent.getIntExtra("position", 0);
        audioList = (List<AudioResponse>) intent.getSerializableExtra("audioList");

        Const.CURRENT_TITLE = title;
        Const.CURRENT_COVER_URL = coverUrl;

//        try {
//            //url包含中文需要转码
//            //对路径进行编码 然后替换路径中所有空格 编码之后空格变成“+”而空格的编码表示是“%20” 所以将所有的“+”替换成“%20”就可以了
//            coverUrl = URLEncoder.encode(coverUrl, "utf-8").replaceAll("\\+", "%20");
//            //编码之后的路径中的“/”也变成编码的东西了 所有还有将其替换回来 这样才是完整的路径
//            coverUrl = coverUrl.replaceAll("%3A", ":").replaceAll("%2F", "/");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        coverFragment = new CoverFragment(this, coverUrl);
        captionFragment = new CaptionFragment(this);

        captionFragment.setOnCreatedListener(new CaptionFragment.onCreatedListener() {
            @Override
            public void onCreated() {
                mExecutorService.execute(loadRunnable);
            }
        });

        fragments = new ArrayList<>();
        fragments.add(coverFragment);
        fragments.add(captionFragment);

        mContentPagerAdapter = new ContentPagerAdapter(this, fragments);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(mContentPagerAdapter);

        mAudioProgressBar.setOnSeekBarChangeListener(new AudioProgressBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mPlayService.hasPrepared) {
                    mPlayService.seekTo(seekBar.getProgress());
                    captionFragment.lrcView.updateTime(seekBar.getProgress());
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    public void initData() {
        Const.UA = TokenUtil.getUserLoginToken(this);

        mAudioBeans = new ArrayList<>();
        mListenerLoader = ListenerLoader.getInstance();

        isCollect = audioList.get(position).getIsCollect();
        collectAudioState(isCollect);

        mCollectDialog = new CollectDialog(mContext);
        mAudioListDialog = new AudioListDialog(mContext, this, mAudioBeans, audioList);
        mSettingDialog = new SettingDialog(mContext, this);
        tvTitle.setText(title);

        audioId = audioList.get(position).getId();
        audioUrl = audioList.get(position).getAudioUrl();//音频url
        captionUrl = audioList.get(position).getCaptionUrl();//字幕url
        mExecutorService = Executors.newFixedThreadPool(5);

        try {
            captionUrl = URLEncoder.encode(captionUrl, "utf-8").replaceAll("\\+", "%20");
            captionUrl = captionUrl.replaceAll("%3A", ":").replaceAll("%2F", "/");

//            audioUrl = URLEncoder.encode(audioUrl, "utf-8").replaceAll("\\+", "%20");
//            audioUrl = audioUrl.replaceAll("%3A", ":").replaceAll("%2F", "/");

//            coverUrl = URLEncoder.encode(coverUrl, "utf-8").replaceAll("\\+", "%20");
//            coverUrl = coverUrl.replaceAll("%3A", ":").replaceAll("%2F", "/");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        mSettingDialog.setOnDismissListener(new SettingDialog.OnDismissListener() {
            @Override
            public void onDismiss() {
                int captionType = SaveDataUtil.getInstance(mContext).getInt("captionType", 1);
                captionFragment.lrcView.loadLrcByUrl(captionUrl,captionType);

                float speed = SaveDataUtil.getInstance(mContext).getFloat("playSpeed", 1.0f);
                mPlayService.setPlayerSpeed(speed);
            }
        });

        Intent sIntent = new Intent(this, PlayService.class);
        startService(sIntent);
        mPlayService = PlayService.getInstance();
        addAudioList(mPlayService.currentId);
        mPlayService.setOnStateListener(new PlayService.OnStateListener() {
            @Override
            public void onCompletion() {
                boolean lastPause = SaveDataUtil.getInstance(mContext).getBoolean("isTimingPause", false);
                if (lastPause) return;
                int timingClose = SaveDataUtil.getInstance(mContext).getInt("timingClose", 1);
                if (timingClose == 2 || timingClose == 3 || timingClose == 4) {
                    mPlayService.playCount++;
                }
                RecentPlayRequest request = new RecentPlayRequest();
                request.setType(1);
                request.setAlbumId(albumId);
                mListenerLoader.addRecentPlay(request).subscribe(new Action1<BaseResponse>() {
                    @Override
                    public void call(BaseResponse response) {
                        if (response.getCode() == 0) {
                            Log.e(TAG, "添加最近播放记录 专辑id：" + albumId);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
                int playMode = SaveDataUtil.getInstance(mContext).getInt("playMode", 1);
                if (playMode == 1) {
                    //顺序播放
                    position++;
                } else if (playMode == 2) {
                    //单曲循环
                } else if (playMode == 3) {
                    //随机播放
                    Random rand = new Random();
                    int randNumber = rand.nextInt(audioList.size());
                    position = randNumber;
                }
                if (position >= audioList.size()) {
                    position = 0;
                }

                if (timingClose == 2 && mPlayService.playCount >= 1) {
                    mPlayService.playCount = 0;
                } else if (timingClose == 3 && mPlayService.playCount >= 2) {
                    mPlayService.playCount = 0;
                } else if (timingClose == 4 && mPlayService.playCount >= 3) {
                    mPlayService.playCount = 0;
                } else {
                    playNext(position);
                }
            }

            @Override
            public void onPlayLeft() {
                playLeft();
            }

            @Override
            public void onPlayRight() {
                playRight();
            }

            @Override
            public void onSentenceLeft() {

            }

            @Override
            public void onSentenceRight() {

            }
        });
    }

    private void addAudioList(int currentId) {
        for (int i = 0; i < audioList.size(); i++) {
            AudioBean audioBean = new AudioBean();
            audioBean.setNum(i + 1);
            audioBean.setAudioName(audioList.get(i).getTitle());
            int duration = audioList.get(i).getDuration();
            if (duration < 60) {
                String durationStr = duration < 10 ? "00:0" + duration : "00:" + duration;
                audioBean.setDuration(durationStr);
            } else {
                int min = duration / 60;
                int sec = duration % 60;
                String minStr = min < 10 ? "0" + min : "" + min;
                String secStr = sec < 10 ? "0" + sec : "" + sec;
                audioBean.setDuration(minStr + ":" + secStr);
            }
            int id = audioList.get(i).getId();
            if (id == currentId) {
                audioBean.setPlaying(true);
            } else {
                audioBean.setPlaying(false);
            }

            String audioUrl = audioList.get(i).getAudioUrl();
            String[] strings = audioUrl.split("\\?");
            if (strings.length == 2) {
                audioUrl = strings[0];
            }
            String[] split = audioUrl.split("/");
            String fileName = split[split.length - 1];
            if (DownloadUtil.existsFile(Const.MP3PATH + fileName)) {
                audioBean.setDownloadState(1);
            } else {
                audioBean.setDownloadState(0);
            }
            mAudioBeans.add(audioBean);
        }
    }

    /**
     * 更新收藏图标状态
     */
    public void collectAudioState(boolean isCollect) {
        if (isCollect) {
            iconCollect.setImageResource(R.mipmap.icon_audio_collect);
        } else {
            iconCollect.setImageResource(R.mipmap.icon_audio_nocollect);
        }
    }

    /**
     * 播放下一曲
     */
    public void playNext(int position) {
        if (position < audioList.size()) {
            mAudioProgressBar.setProgress(0);
            mAudioProgressBar.setCurrentTime("00:00");
            resetAB();
            audioId = audioList.get(position).getId();
            audioUrl = audioList.get(position).getAudioUrl();
            coverUrl = audioList.get(position).getCover();
            captionUrl = audioList.get(position).getCaptionUrl();
            title = audioList.get(position).getTitle();
            tvTitle.setText(title);

            try {
                captionUrl = URLEncoder.encode(captionUrl, "utf-8").replaceAll("\\+", "%20");
                captionUrl = captionUrl.replaceAll("%3A", ":").replaceAll("%2F", "/");

//                audioUrl = URLEncoder.encode(audioUrl, "utf-8").replaceAll("\\+", "%20");
//                audioUrl = audioUrl.replaceAll("%3A", ":").replaceAll("%2F", "/");

//                coverUrl = URLEncoder.encode(coverUrl, "utf-8").replaceAll("\\+", "%20");
//                coverUrl = coverUrl.replaceAll("%3A", ":").replaceAll("%2F", "/");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (coverFragment != null) {
                coverFragment.loadImg(coverUrl);
            }

            Const.CURRENT_TITLE = title;
            Const.CURRENT_COVER_URL = coverUrl;

            mExecutorService.execute(loadRunnable);
        }
        this.position = position;
        isCollect = audioList.get(position).getIsCollect();
        collectAudioState(isCollect);
    }

    /**
     * 重置ab复读
     */
    private void resetAB() {
        mPlayService.abState = 0;
        mPlayService.isCheckA = false;
        mPlayService.isCheckB = false;
        iconAb.setImageResource(R.mipmap.icon_ab_normal);
        captionFragment.lrcView.setPointAPosition(-1);
        captionFragment.lrcView.setPointBPosition(-1);
        mPlayService.aPosition = -1;
        mPlayService.bPosition = -1;
        mAudioProgressBar.setEnable(true);
    }

    private Runnable loadRunnable = new Runnable() {
        @Override
        public void run() {
            totalDuration = audioList.get(position).getDuration() * 1000;
            totalTime = TimeUtil.formatTime(totalDuration);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAudioProgressBar.setMax((int) totalDuration);
                    mAudioProgressBar.setProgress(0);
                    mAudioProgressBar.setTotalTime(totalTime);
                }
            });

            int captionType = SaveDataUtil.getInstance(mContext).getInt("captionType", 1);
            Log.e(TAG, "captionUrl: " + captionUrl);
            captionFragment.lrcView.loadLrcByUrl(captionUrl,captionType);
            captionFragment.lrcView.setOnTapListener(new LrcView.OnTapListener() {
                @Override
                public void onTapSentence(String sentence) {
                    mPlayService.pause();
                    //点击字幕区域查词
                    mSearchWordDialog = new SearchWordDialog(mContext, PlayActivity.this);
                    mSearchWordDialog.showDialog(sentence);
                    mSearchWordDialog.setOnWordClickListener(new SearchWordDialog.OnWordClickListener() {
                        @Override
                        public void onWordClick(String word) {

                        }
                    });
                    mSearchWordDialog.setOnDismissListener(new SearchWordDialog.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            PlayService.getInstance().start();
                        }
                    });
                }
            });

            captionFragment.lrcView.setDraggable(true, (view, time) -> {
                mPlayService.seekTo((int) time);
                return true;
            });

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!mPlayService.isSingle && mPlayService.abState != 2 && !mPlayService.isPlaying()) {
                        mPlayService.abState = 0;
                        mPlayService.play(audioUrl, audioId);
                    } else {
                        //后台当前播放如果和点击进去的曲目一样则接着播放 状态不变
                        if (audioId == mPlayService.currentId) {
                            if (mPlayService.abState == 2) {
                                captionFragment.lrcView.setPointAPosition(mPlayService.aPosition);
                                captionFragment.lrcView.setPointBPosition(mPlayService.bPosition);
                            }
                            if (mPlayService.isSingle) {
                                captionFragment.lrcView.setSinglePosition(mPlayService.currentLine);
                            }
                            if (mPlayService.isPause()) {
                                mPlayService.start();
                            }
                        } else {//如果不是同一曲目 则清除之前状态
                            resetAB();
                            mPlayService.isSingle = false;
                            mPlayService.play(audioUrl, audioId);
                        }
                    }
                    updateAbRepeatState();
                    updateSingleRepeatState();
                }
            });
            handler.post(playRunnable);
        }
    };

    private Runnable playRunnable = new Runnable() {
        @Override
        public void run() {
            if (mPlayService.isPlaying()) {
                long time = mPlayService.getCurrentPosition();//当前播放进度
                if (mPlayService.isFollow) {
                    if (time >= mPlayService.currentEndTime) {
                        mPlayService.pause();
                        EventBus.getDefault().post(101);
                    }
                }
                if (mPlayService.isSingle || mPlayService.abState == 2) {
                    //单句复读 或 AB复读
                    if (time >= mPlayService.currentEndTime) {
                        int delayed = SaveDataUtil.getInstance(mContext).getInt("intervalTime", 2);
                        int count = SaveDataUtil.getInstance(mContext).getInt("repeatCount", 2);
                        if (repeatCount >= count) {//复读设定的次数
                            mPlayService.pause();
                            repeatCount = 0;
                            String currentTime = TimeUtil.formatTime(time);
                            mAudioProgressBar.setCurrentTime(currentTime);
                            boolean autoNext = SaveDataUtil.getInstance(mContext).getBoolean("autoNext", false);
                            if (autoNext && mPlayService.isSingle) {//如果是单句复读开启了自动下一句
                                mPlayService.start();
                                mPlayService.isSingle = false;
                                captionFragment.lrcView.setSinglePosition(-1);
                                updateSingleRepeatState();
                            }
                        } else if (repeatCount > 0) {
                            mPlayService.pause();
                            //复读间隔
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mPlayService.start();
                                    mPlayService.seekTo((int) mPlayService.currentStartTime);
                                    captionFragment.lrcView.updateTime(mPlayService.currentStartTime);
                                    mAudioProgressBar.setProgress((int) mPlayService.currentStartTime);
                                    String currentTime = TimeUtil.formatTime(time);
                                    mAudioProgressBar.setCurrentTime(currentTime);
                                }
                            }, delayed * 1000);
                        } else {
                            mPlayService.seekTo((int) mPlayService.currentStartTime);
                            captionFragment.lrcView.updateTime(mPlayService.currentStartTime);
                            mAudioProgressBar.setProgress((int) mPlayService.currentStartTime);
                            String currentTime = TimeUtil.formatTime(time);
                            mAudioProgressBar.setCurrentTime(currentTime);
                        }
                        repeatCount++;
                    } else {
                        captionFragment.lrcView.updateTime(time);
                        mAudioProgressBar.setProgress((int) time);
                        String currentTime = TimeUtil.formatTime(time);
                        mAudioProgressBar.setCurrentTime(currentTime);
                    }
                } else {
                    captionFragment.lrcView.updateTime(time);
                    String currentTime = TimeUtil.formatTime(time);
                    mAudioProgressBar.setCurrentTime(currentTime);
                    mAudioProgressBar.setProgress((int) time);
                }
            }
            handler.postDelayed(this, 100);
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.icon_back:
                finish();
                break;
            case R.id.icon_setting:
                mSettingDialog.showDialog();
                break;
            case R.id.ll_audio_collect:
                AudioResponse audioResponse = audioList.get(position);
                int id = audioResponse.getId();
                if (!isCollect) {
                    mListenerLoader.collectAudio(id).subscribe(new Action1<BaseResponse>() {
                        @Override
                        public void call(BaseResponse baseResponse) {
                            Log.d(TAG, "collectAudio: " + baseResponse.toString());
                        }
                    });
                    mCollectDialog.showDialog();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mCollectDialog.dismiss();
                                }
                            });
                        }
                    }, 350);
                    isCollect = true;
                } else {
                    mListenerLoader.cancelCollectAudio(id).subscribe(new Action1<BaseResponse>() {
                        @Override
                        public void call(BaseResponse baseResponse) {
                            Log.d(TAG, "cancelCollectAudio: " + baseResponse.toString());
                        }
                    });
                    isCollect = false;
                }
                collectAudioState(isCollect);
                break;
            case R.id.ll_follow:
                startFollow();
                break;
            case R.id.ll_ab:
                if (!mPlayService.hasPrepared || captionFragment.lrcView.getLrcEntrySize() == 0)
                    return;
                clickAbRepeat();
                break;
            case R.id.ll_single:
                if (!mPlayService.hasPrepared || captionFragment.lrcView.getLrcEntrySize() == 0)
                    return;
                clickSingleRepeat();
                break;
            case R.id.ll_audio_list:
                mAudioListDialog.showDialog();
                break;
            default:
                break;
        }
    }

    private void startFollow() {
        if (!mPlayService.hasPrepared || captionFragment.lrcView.getLrcEntrySize() == 0) return;
        LrcEntry lrcEntry = captionFragment.lrcView.getLrcEntry(captionFragment.lrcView.mCurrentLine);
        int lrcEntrySize = captionFragment.lrcView.getLrcEntrySize();
        mPlayService.currentStartTime = lrcEntry.getTime();
        if (captionFragment.lrcView.mCurrentLine == lrcEntrySize - 1) {
            mPlayService.currentEndTime = totalDuration;
        } else {
            mPlayService.currentEndTime = captionFragment.lrcView.getLrcEntry(captionFragment.lrcView.mCurrentLine + 1).getTime();
        }
        String content = lrcEntry.getText();
        Intent intent = new Intent(this, FollowActivity.class);
        intent.putExtra("resId", audioId);
        intent.putExtra("position", captionFragment.lrcView.mCurrentLine + 1);
        intent.putExtra("count", captionFragment.lrcView.getLrcEntrySize());
        intent.putExtra("content", content);
        startActivity(intent);
        if (mPlayService.isPlaying()) {
            mPlayService.pause();
        }
    }

    /**
     * 更新ab复读状态
     */
    private void updateAbRepeatState() {
        if (!mPlayService.isPlaying() && !mPlayService.isPause()) return;
        if (mPlayService.abState == 0) {//未选中
            iconAb.setImageResource(R.mipmap.icon_ab_normal);
            mAudioProgressBar.setEnable(true);
        } else if (mPlayService.abState == 2) {//选中
            iconAb.setImageResource(R.mipmap.icon_ab_selected);
            mAudioProgressBar.setEnable(false);
        }
    }

    /**
     * 更新单句复读状态
     */
    private void updateSingleRepeatState() {
        if (!mPlayService.isPlaying() && !mPlayService.isPause()) return;
        if (!mPlayService.isSingle) {
            iconSingle.setImageResource(R.mipmap.icon_single_normal);
            mAudioProgressBar.setEnable(true);
        } else {
            iconSingle.setImageResource(R.mipmap.icon_single_selected);
            mAudioProgressBar.setEnable(false);
        }
    }

    /**
     * 点击ab复读
     */
    private void clickAbRepeat() {
        if (mPlayService.abState == 0) {//选择A点
            iconAb.setImageResource(R.mipmap.icon_a_selected);
            mPlayService.abState = 1;
            mAudioProgressBar.setEnable(false);
            mPlayService.aPosition = captionFragment.lrcView.mCurrentLine;
            captionFragment.lrcView.setPointAPosition(mPlayService.aPosition);
            LrcEntry lrcEntry = captionFragment.lrcView.getLrcEntry(captionFragment.lrcView.mCurrentLine);
            mPlayService.currentStartTime = lrcEntry.getTime();
        } else if (mPlayService.abState == 1) {//选中B点
            iconAb.setImageResource(R.mipmap.icon_ab_selected);
            mPlayService.abState = 2;
            mAudioProgressBar.setEnable(false);
            mPlayService.currentLine = captionFragment.lrcView.mCurrentLine;
            mPlayService.bPosition = captionFragment.lrcView.mCurrentLine;
            captionFragment.lrcView.setPointBPosition(mPlayService.bPosition);
            int lrcEntrySize = captionFragment.lrcView.getLrcEntrySize();
            if (captionFragment.lrcView.mCurrentLine == lrcEntrySize - 1) {
                mPlayService.currentEndTime = totalDuration;
            } else {
                mPlayService.currentEndTime = captionFragment.lrcView.getLrcEntry(captionFragment.lrcView.mCurrentLine + 1).getTime();
            }
        } else {//取消选择
            resetAB();
            if (mPlayService.isPause()) {
                mPlayService.start();
            }
            repeatCount = 0;
        }
    }

    /**
     * 点击单句复读
     */
    private void clickSingleRepeat() {
        if (!mPlayService.isSingle) {
            iconSingle.setImageResource(R.mipmap.icon_single_selected);
            mPlayService.isSingle = true;
            mAudioProgressBar.setEnable(false);
            mPlayService.currentLine = captionFragment.lrcView.mCurrentLine;
            captionFragment.lrcView.setSinglePosition(captionFragment.lrcView.mCurrentLine);
            LrcEntry lrcEntry = captionFragment.lrcView.getLrcEntry(captionFragment.lrcView.mCurrentLine);
            int lrcEntrySize = captionFragment.lrcView.getLrcEntrySize();
            mPlayService.currentStartTime = lrcEntry.getTime();
            if (captionFragment.lrcView.mCurrentLine == lrcEntrySize - 1) {
                mPlayService.currentEndTime = totalDuration;
            } else {
                mPlayService.currentEndTime = captionFragment.lrcView.getLrcEntry(captionFragment.lrcView.mCurrentLine + 1).getTime();
            }
        } else {
            iconSingle.setImageResource(R.mipmap.icon_single_normal);
            mPlayService.isSingle = false;
            mAudioProgressBar.setEnable(true);
            captionFragment.lrcView.setSinglePosition(-1);
            if (mPlayService.isPause()) {
                mPlayService.start();
            }
            repeatCount = 0;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case 233://单句复读
                if (!isSingleDown) {
                    clickSingleRepeat();
                    isSingleDown = true;
                }
                break;
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
                if (!leftDown && mPlayService.abState == 0) {
                    if (captionFragment.lrcView.mCurrentLine != 0) {
                        LrcEntry lrcEntry = captionFragment.lrcView.getLrcEntry(captionFragment.lrcView.mCurrentLine - 1);
                        leftOrRight(lrcEntry);
                    }
                    leftDown = true;
                }
                mAudioProgressBar.setEnable(false);
                break;
            case 22://下一句
                if (!rightDown && mPlayService.abState == 0) {
                    int lrcEntrySize = captionFragment.lrcView.getLrcEntrySize();
                    if (captionFragment.lrcView.mCurrentLine != lrcEntrySize - 1) {
                        LrcEntry lrcEntry = captionFragment.lrcView.getLrcEntry(captionFragment.lrcView.mCurrentLine + 1);
                        leftOrRight(lrcEntry);
                    }
                    rightDown = true;
                }
                mAudioProgressBar.setEnable(false);
                break;
            case 23://暂停
                if (!isPauseDown) {
                    mPlayService.playOrPause();
                    isPauseDown = true;
                }
                break;
            default:
                break;
        }
        Log.d(TAG, "dispatchKeyEvent: " + event.getKeyCode());
        return super.dispatchKeyEvent(event);
    }

    /**
     * 上一曲
     */
    public void playLeft() {
        position--;
        if (position < 0) {
            position = audioList.size() - 1;
        }
        playNext(position);
    }

    /**
     * 下一曲
     */
    public void playRight() {
        position++;
        if (position < audioList.size()) {
        } else {
            position = 0;
        }
        playNext(position);
    }

    /**
     * 上一句或下一句
     *
     * @param lrcEntry
     */
    private void leftOrRight(LrcEntry lrcEntry) {
        mPlayService.isSingle = false;
        captionFragment.lrcView.setSinglePosition(-1);
        updateSingleRepeatState();
        long time = lrcEntry.getTime();
        mPlayService.currentStartTime = time;
        mPlayService.seekTo((int) mPlayService.currentStartTime);
        captionFragment.lrcView.updateTime(time);
        String currentTime = TimeUtil.formatTime(time);
        mAudioProgressBar.setCurrentTime(currentTime);
        mAudioProgressBar.setProgress((int) time);
        if (mPlayService.isPause()) {
            mPlayService.start();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (event.getKeyCode()) {
            case 233://单句复读
                if (isSingleDown) {
                    isSingleDown = false;
                }
                break;
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
                    leftDown = false;
                }
                mAudioProgressBar.setEnable(true);
                break;
            case 22://下一句
                if (rightDown) {
                    rightDown = false;
                }
                mAudioProgressBar.setEnable(true);
                break;
            case 23://暂停
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
    protected void onDestroy() {
        super.onDestroy();
    }
}