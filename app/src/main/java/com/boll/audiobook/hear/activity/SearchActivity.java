package com.boll.audiobook.hear.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.boll.audiobook.hear.R;
import com.boll.audiobook.hear.audio.YuyinReceiver;
import com.boll.xirilib.XiriUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SearchActivity extends BaseActivity implements View.OnClickListener,
        YuyinReceiver.OnYuYinToTextListener {

    private static final String TAG = "SearchActivity";

    private ImageView iconBack;
    private LinearLayout llBackground;
    private LinearLayout llHint;
    private TextView tvKeyword1;
    private TextView tvKeyword2;
    private TextView tvKeyword3;
    private TextView tvKeyword4;
    private LinearLayout llResult;
    private TextView tvAudioResult;
    private TextView tvBubble;
    private ImageView animLy;

    private Gson gson;

    private AnimationDrawable lyAnim;

    private boolean isRecordDown;

    private boolean isJumpResult;//是否跳转到了结果页面

    private int COUNTDOWN_CODE = 1001;

    private int countdown = 5;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == COUNTDOWN_CODE) {
                Log.e(TAG, "开始倒计时 " + countdown);
                if (countdown > 0) {
                    tvBubble.setVisibility(View.VISIBLE);
                    tvBubble.setText(countdown + "s后停止录音");
                    countdown--;
                    mHandler.sendEmptyMessageDelayed(COUNTDOWN_CODE,1000);
                } else {
                    tvBubble.setVisibility(View.INVISIBLE);
                    isRecordDown = false;
                    lyAnim.stop();
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
    }

    @Override
    public int initLayout() {
        return R.layout.activity_search;
    }

    @Override
    public void initView() {
        iconBack = findViewById(R.id.icon_back);
        llBackground = findViewById(R.id.ll_background);
        llHint = findViewById(R.id.ll_hint);
        tvKeyword1 = findViewById(R.id.tv_keyword1);
        tvKeyword2 = findViewById(R.id.tv_keyword2);
        tvKeyword3 = findViewById(R.id.tv_keyword3);
        tvKeyword4 = findViewById(R.id.tv_keyword4);
        tvAudioResult = findViewById(R.id.tv_audio_result);
        llResult = findViewById(R.id.ll_result);
        tvBubble = findViewById(R.id.tv_bubble);
        animLy = findViewById(R.id.anim_ly);
        iconBack.setOnClickListener(this);
        tvKeyword1.setOnClickListener(this);
        tvKeyword2.setOnClickListener(this);
        tvKeyword3.setOnClickListener(this);
        tvKeyword4.setOnClickListener(this);

        lyAnim = (AnimationDrawable) animLy.getBackground();
    }

    @Override
    public void initData() {
        YuyinReceiver.register(this);
        YuyinReceiver.setOnYuYinToTextListener(this);

        gson = new GsonBuilder()
                .serializeNulls() //输出null
                .setPrettyPrinting()//格式化输出
                .create();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isJumpResult = false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == 234) {
            if (!isRecordDown) {
                mHandler.sendEmptyMessageDelayed(COUNTDOWN_CODE, 1000 * 10);
                llBackground.setBackgroundResource(R.mipmap.background_subcategory);
                llHint.setVisibility(View.GONE);
                llResult.setVisibility(View.VISIBLE);
                isRecordDown = true;
                lyAnim.start();

                XiriUtil.performBeginRecord(this);
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == 234) {
            if (isRecordDown) {
                mHandler.removeMessages(COUNTDOWN_CODE);
                tvBubble.setVisibility(View.INVISIBLE);
                isRecordDown = false;
                lyAnim.stop();

                XiriUtil.performEndRecord(this);
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        String normValue = "";
        switch (view.getId()) {
            case R.id.icon_back:
                finish();
                break;
            case R.id.tv_keyword1:
                normValue = "1级-pink";
                break;
            case R.id.tv_keyword2:
                normValue = "Cars";
                break;
            case R.id.tv_keyword3:
                normValue = "2级-red";
                break;
            case R.id.tv_keyword4:
                normValue = "Cats";
                break;
            default:
                break;
        }
        if (view.getId() != R.id.icon_back && !isJumpResult) {
            isJumpResult = true;
            Intent intent = new Intent(this, SearchResultActivity.class);
            intent.putExtra("normValue", normValue);
            startActivity(intent);
        }
    }

//    @Override
//    public void onIntermediateResult(String json) {
//        IntermediateBean intermediateBean = gson.fromJson(json, IntermediateBean.class);
//        if (intermediateBean != null) {
//            int size = intermediateBean.getIflyos_responses().size();
//            if (size > 0) {
//                for (int i = 0; i < size; i++) {
//                    String name = intermediateBean.getIflyos_responses().get(i).getHeader().getName();
//                    String intermediate = "recognizer.intermediate_text";//识别中标识
//                    if (TextUtils.equals(name, intermediate)) {
//                        String text = intermediateBean.getIflyos_responses().get(i).getPayload().getText();
//                        runOnUiThread(() -> {
//                            tvAudioResult.setText(text);
//                        });
//                        Log.e(TAG, "识别中: " + text);
//                    }
//                }
//            }
//        }
//    }

//    @Override
//    public void onAudioResult(String json) {
//        String normValue = "";
//        AudioResult audioResult = gson.fromJson(json, AudioResult.class);
//        if (audioResult != null) {
//            int size = audioResult.getIflyos_responses().size();
//            if (size > 0) {
//                List<AudioResult.IflyosResponsesBean> iflyos_responses =
//                        audioResult.getIflyos_responses();
//                for (int i = 0; i < iflyos_responses.size(); i++) {
//                    String name = iflyos_responses.get(i).getHeader().getName();
//                    String custom = "interceptor.custom";
//                    if (TextUtils.equals(name, custom)) {
//                        isJumpResult = true;
//                        normValue = iflyos_responses.get(i).getPayload().getNormValue();
//                        Intent intent = new Intent(this, SearchResultActivity.class);
//                        intent.putExtra("normValue", normValue);
//                        startActivity(intent);
//                        Log.d(TAG, "normValue: " + normValue);
//                    }
//                }
//            }
//        }
//        //如果没有语音结果
//        if (!isJumpResult) {
//            isJumpResult = true;
//            Intent intent = new Intent(this, SearchResultActivity.class);
//            intent.putExtra("normValue", "");
//            startActivity(intent);
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(COUNTDOWN_CODE);

        YuyinReceiver.unregister(this);
    }

    @Override
    public void onYuYinToText(String content) {
        runOnUiThread(() -> {
            tvAudioResult.setText(content);
        });
        Intent intent = new Intent(this, SearchResultActivity.class);
        intent.putExtra("normValue", content);
        startActivity(intent);
    }

}