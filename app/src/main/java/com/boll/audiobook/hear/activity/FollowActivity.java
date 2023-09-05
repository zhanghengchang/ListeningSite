package com.boll.audiobook.hear.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.boll.audiobook.hear.R;
import com.boll.audiobook.hear.network.request.AddRecordRequest;
import com.boll.audiobook.hear.network.response.BaseResponse;
import com.boll.audiobook.hear.network.response.UploadTokenResponse;
import com.boll.audiobook.hear.network.retrofit.ListenerLoader;
import com.boll.audiobook.hear.service.PlayService;
import com.boll.audiobook.hear.utils.Const;
import com.boll.audiobook.hear.utils.FileListUtil;
import com.boll.audiobook.hear.utils.FileUtil;
import com.boll.audiobook.hear.utils.RecordUtil;
import com.boll.audiobook.hear.utils.SaveDataUtil;
import com.boll.audiobook.hear.utils.WavMergeUtil;
import com.boll.evaluationlib.bean.EvaluateResult;
import com.boll.evaluationlib.constant.Constant;
import com.boll.evaluationlib.utils.EvaluationReceiver;
import com.boll.evaluationlib.utils.EvaluationUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.functions.Action1;

public class FollowActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "FollowActivity";

    private Context mContext;
    private ImageView iconBack;
    private TextView tvPosition;
    private TextView tvContent;
    private LinearLayout llPlay;
    private ImageView animPlay;
    private ImageView animRecord;
    private TextView tvRecord;

    private boolean isRecordDown;//录音按键是否按下

    private AnimationDrawable playAnim;
    private AnimationDrawable playAnim1;
    private AnimationDrawable recordAnim;

    private String content;//跟读内容
    private int position;//当前句子的位置
    private int count;

    private Gson gson;
    private LinearLayout llSentenceContent;
    private ImageView animPlay1;
    private LinearLayout llPlay1;
    private TextView tvResult;
    private ProgressBar fluency;
    private ProgressBar accuracy;
    private ProgressBar integrity;
    private TextView tvTotalScore;
    private LinearLayout llPlayBack;
    private LinearLayout llResultContent;
    private LinearLayout llRecordHint;

    private PlayService mPlayService;

    private String audioPath;

    private int resId;
    private List<Integer> isRecordedList;//记录每一句是否有录音 0：未录音 1：已录音

    private ListenerLoader mListenerLoader;

    private int TIME_OUT = 101;
    private int STOP_RECORD = 102;

    public static final String RECEIVER_ACTION = "com.boll.evaluation.result";

    private EvaluationReceiver receiver;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == TIME_OUT) {
                if (mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                    showToast("识别超时，请检查网络或重试！");
                }
            } else if (message.what == STOP_RECORD) {
                isRecordedList.set(position - 1, 1);
                SaveDataUtil.getInstance(mContext).putList("isRecordedList", isRecordedList);
                boolean isRecordComplete = true;//录音是否完整
                for (int i = 0; i < isRecordedList.size(); i++) {
                    if (isRecordedList.get(i) != 1) {
                        isRecordComplete = false;
                    }
                }
                //判断录音是否完整 如果完整则合并为一个音频上传
                if (isRecordComplete) {
                    File records = new File(Const.RECORDPATH);
                    File[] files = records.listFiles();
                    List<File> wavFiles = new ArrayList<>();

                    if (files != null && files.length == count) {
                        for (int i = 0; i < files.length; i++) {
                            wavFiles.add(files[i]);
                        }
                        List<File> ascFiles = FileListUtil.sortFileByName(wavFiles, "asc");
                        File merge = new File(Const.MERGEPATH);
                        if (!merge.exists()) {
                            merge.mkdirs();
                        }
                        FileOutputStream fos = null;
                        File mergeFile = new File(Const.MERGEPATH + "merge.wav");
                        if (mergeFile.exists()) mergeFile.delete();
                        try {
                            WavMergeUtil.mergeWav(ascFiles, mergeFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mListenerLoader.uploadToken().subscribe(new Action1<UploadTokenResponse>() {
                            @Override
                            public void call(UploadTokenResponse uploadTokenResponse) {
                                if (uploadTokenResponse.getCode() == 0) {
                                    String uploadToken = uploadTokenResponse.getData().getUploadToken();
                                    String key = uploadTokenResponse.getData().getFilePath();
                                    upload(uploadToken, key);
                                }
                            }
                        });
                    }
                } else {
                    Log.e(TAG, "录音不完整，未上传！");
                }
            } else if (message.what == Constant.EVALUATION_RESULT) {
                EvaluateResult result = (EvaluateResult) message.obj;
                mLoadingDialog.dismiss();
                if (result.getCode() == 1) {
                    //返回正常结果
                    audioPath = result.getAudioPath();

                    String folderPath = Const.RECORDPATH;
                    File destFile = new File(folderPath);
                    if (!destFile.exists()){
                        destFile.mkdirs();
                    }
                    FileUtil.copyFile(audioPath,folderPath + "record" + position + ".wav");

                    int fluencyScore = (int) (result.getFluency_score() * 20);
                    int accuracyScore = (int) (result.getAccuracy_score() * 20);
                    int integrityScore = (int) (result.getIntegrity_score() * 20);
                    int totalScore = (int) (result.getTotal_score() * 20);

                    llSentenceContent.setVisibility(View.GONE);
                    llRecordHint.setVisibility(View.GONE);
                    llResultContent.setVisibility(View.VISIBLE);

                    tvResult.setText(result.getContent());

                    fluency.setProgress(fluencyScore);
                    accuracy.setProgress(accuracyScore);
                    integrity.setProgress(integrityScore);
                    tvTotalScore.setText(totalScore + "");
                    Log.e(TAG,result.getContent() + "得分" + result.getTotal_score());
                } else {
                    //评测异常
                    int code = result.getCode();
                    String msg = result.getMsg();
                    Log.e(TAG, "评测异常   code    " + code + "    msg    " + msg);
                    Toast.makeText(getApplicationContext(), "识别失败,您再说一遍吧！", Toast.LENGTH_SHORT).show();
                }
            }
            return false;
        }
    });

    @Override
    public void setStatus() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(RECEIVER_ACTION);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public int initLayout() {
        return R.layout.activity_follow;
    }

    @Override
    public void initView() {
        mContext = this;
        iconBack = findViewById(R.id.icon_back);
        tvPosition = findViewById(R.id.tv_position);
        tvContent = findViewById(R.id.tv_content);
        llPlay = findViewById(R.id.ll_play);
        animPlay = findViewById(R.id.anim_play);
        animRecord = findViewById(R.id.anim_record);
        tvRecord = findViewById(R.id.tv_record);
        llSentenceContent = findViewById(R.id.ll_sentence_content);
        animPlay1 = findViewById(R.id.anim_play1);
        llPlay1 = findViewById(R.id.ll_play1);
        tvResult = findViewById(R.id.tv_result);
        fluency = findViewById(R.id.fluency);
        accuracy = findViewById(R.id.accuracy);
        integrity = findViewById(R.id.integrity);
        tvTotalScore = findViewById(R.id.tv_totalScore);
        llPlayBack = findViewById(R.id.ll_play_back);
        llResultContent = findViewById(R.id.ll_result_content);
        llRecordHint = findViewById(R.id.ll_record_hint);
        llPlay1.setOnClickListener(this);
        llPlayBack.setOnClickListener(this);
        iconBack.setOnClickListener(this);
        llPlay.setOnClickListener(this);

        playAnim = (AnimationDrawable) animPlay.getBackground();
        playAnim1 = (AnimationDrawable) animPlay1.getBackground();
        recordAnim = (AnimationDrawable) animRecord.getBackground();
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        resId = intent.getIntExtra("resId", 0);
        content = intent.getStringExtra("content");
        position = intent.getIntExtra("position", 1);
        count = intent.getIntExtra("count", 1);
        tvContent.setText(content);

        receiver = new EvaluationReceiver(mHandler);

        //上次跟读的资源id
        int lastResId = SaveDataUtil.getInstance(mContext).getInt("lastResId", 0);
        if (resId != lastResId) {
            //如果跟上次不是同一个音频则删除之前所有录音
            File records = new File(Const.RECORDPATH);
            File[] files = records.listFiles();
            if (files != null && files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    files[i].delete();
                }
            }
            isRecordedList = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                isRecordedList.add(0);
            }
            SaveDataUtil.getInstance(mContext).putList("isRecordedList", isRecordedList);
        } else {
            isRecordedList = SaveDataUtil.getInstance(mContext).getList("isRecordedList");
            if (isRecordedList == null || isRecordedList.size() == 0) {
                isRecordedList = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    isRecordedList.add(0);
                }
            }
        }

        tvPosition.setText(position + "/" + count);

        mPlayService = PlayService.getInstance();

        gson = new GsonBuilder()
                .serializeNulls() //输出null
                .setPrettyPrinting()//格式化输出
                .create();

        mListenerLoader = ListenerLoader.getInstance();
    }

    private void upload(String uploadToken, String key) {
        final long recordTime = System.currentTimeMillis();

        // 转化为指定格式的字符串
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String recordDate = sdf.format(new Date(recordTime));

        //config配置上传参数
        Configuration configuration = new Configuration.Builder()
                .connectTimeout(15)
                .responseTimeout(60).build();

        UploadOptions opt = new UploadOptions(null, null, true, new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
            }
        }, null);

        UploadManager uploadManager = new UploadManager(configuration);
        File file = new File(Const.MERGEPATH + "merge.wav");
        if (!file.exists()) {
            Log.e(TAG, "录音文件不存在！！！");
            return;
        }
        uploadManager.put(file, key, uploadToken, new UpCompletionHandler() {
            @Override
            public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {
                Log.e(TAG, "complete: " + jsonObject.toString());
                try {
                    String fileKey = jsonObject.getString("key");
                    if (responseInfo.isOK()) {
                        AddRecordRequest request = new AddRecordRequest();
                        request.setRecordTime(recordDate);
                        request.setAudioUrl(fileKey);
                        request.setResId(resId);
                        request.setType(1);

                        mListenerLoader.addSoundRecord(request).subscribe(new Action1<BaseResponse>() {
                            @Override
                            public void call(BaseResponse response) {
                                Log.e(TAG, "addSoundRecord: " + response.toString());
                                if (response.getCode() == 0) {
                                    Log.d(TAG, "添加录音记录成功");
                                } else {
                                    Log.e(TAG, "添加录音记录失败");
                                }
                            }
                        });
                        Log.e(TAG, fileKey + " === 上传成功");
                    } else {
                        Log.e(TAG, fileKey + " === 上传失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "上传失败");
                }
            }
        }, opt);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.icon_back:
                finish();
                break;
            case R.id.ll_play:
                animPlay.setBackground(getResources().getDrawable(R.drawable.anim_audio_play));
                playAnim = (AnimationDrawable) animPlay.getBackground();
                playAnim.start();
                mPlayService.playCurrent();
                break;
            case R.id.ll_play1:
                animPlay1.setBackground(getResources().getDrawable(R.drawable.anim_audio_play));
                playAnim1 = (AnimationDrawable) animPlay1.getBackground();
                playAnim1.start();
                mPlayService.playCurrent();
                break;
            case R.id.ll_play_back://回放
                Log.e(TAG, "audioPath: " + audioPath);
                RecordUtil.playWavWithMediaPlayer(audioPath);
                break;
            default:
                break;
        }
    }

    @Override
    public void onMessageEvent(Integer code) {
        super.onMessageEvent(code);
        if (code == 101) {
            playAnim.stop();
            Drawable frame = playAnim.getFrame(0);
            animPlay.setBackground(frame);
            playAnim1.stop();
            Drawable frame1 = playAnim1.getFrame(0);
            animPlay1.setBackground(frame1);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == 234) {//录音键按下
            if (!isRecordDown) {
                tvRecord.setVisibility(View.GONE);
                animRecord.setVisibility(View.VISIBLE);
                recordAnim.start();
                isRecordDown = true;
//                RecordUtil.startRecordAudio();

                mHandler.sendEmptyMessageDelayed(STOP_RECORD,500);
                EvaluationUtils.start(mContext, content);//开始评测
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if (event.getKeyCode() == 234) {
            if (isRecordDown) {
                tvRecord.setVisibility(View.VISIBLE);
                animRecord.setVisibility(View.GONE);
                recordAnim.stop();
                isRecordDown = false;
                mLoadingDialog.showDialog();
                //延时500毫秒停止录音防止录音不全
                mHandler.sendEmptyMessageDelayed(STOP_RECORD, 500);
                mHandler.sendEmptyMessageDelayed(TIME_OUT, 1000 * 30);

                EvaluationUtils.end(mContext);//结束评测
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (playAnim.isRunning()) {
            playAnim.stop();
        }
        finish();
    }

    private void back() {
        if (llSentenceContent.getVisibility() == View.VISIBLE) {
            finish();
        } else {
            llSentenceContent.setVisibility(View.VISIBLE);
            llRecordHint.setVisibility(View.VISIBLE);
            llResultContent.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SaveDataUtil.getInstance(mContext).putInt("lastResId", resId);
        mPlayService.isFollow = false;
        mPlayService.start();
    }

}