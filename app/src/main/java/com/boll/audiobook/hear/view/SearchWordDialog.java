package com.boll.audiobook.hear.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.boll.audiobook.hear.activity.WordInfoActivity;
import com.boll.audiobook.hear.network.request.DictRequest;
import com.boll.audiobook.hear.network.response.DictEngResponse;
import com.boll.audiobook.hear.network.response.TokenResponse;
import com.boll.audiobook.hear.utils.SaveDataUtil;
import com.boll.audiobook.hear.network.retrofit.DictLoader;
import com.boll.audiobook.hear.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

import rx.functions.Action1;

/**
 * created by zoro at 2023/6/5
 */
public class SearchWordDialog extends BaseDialog implements View.OnClickListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    private static final String TAG = "SearchWordDialog";

    private Activity mActivity;

    private OnWordClickListener mOnWordClickListener;

    private DictLoader mDictLoader;
    private Gson gson;

    private LinearLayout llWords;
    private LinearLayout llWordsContent;
    private TextView tvWord;
    private ImageView iconWordPlay;
    private TextView tvExplanations;
    private LinearLayout llWordInfo;

    private DictEngResponse mDictEngResponse;

    private String phoneticEnUrl;//英式音标发音路径
    private MediaPlayer mPlayer;
    private boolean hasPrepared;

    private OnDismissListener mOnDismissListener;

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

    public void start() {
        if (null != mPlayer && hasPrepared) {
            mPlayer.start();
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
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        hasPrepared = false;
        return false;
    }

    public SearchWordDialog(@NonNull Context context, Activity activity) {
        super(context, R.style.CustomDialog);
        mActivity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_searchword);
        initIfNecessary();
        initView();
    }

    public void setOnWordClickListener(OnWordClickListener onWordClickListener) {
        mOnWordClickListener = onWordClickListener;
    }

    private void initView() {
        // 获取屏幕信息
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);

        // 设置弹框位置
        Window window = this.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        window.setAttributes(params);
        window.setGravity(Gravity.CENTER);

        Dialog dialog = this;
        if (dialog != null) {
            // 设置弹框大小
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.9), ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        mDictLoader = DictLoader.getInstance();
        gson = new GsonBuilder()
                .serializeNulls() //输出null
                .setPrettyPrinting()//格式化输出
                .create();
        llWords = findViewById(R.id.ll_words);
        llWordsContent = findViewById(R.id.ll_words_content);
        tvWord = findViewById(R.id.tv_word);
        iconWordPlay = findViewById(R.id.icon_word_play);
        tvExplanations = findViewById(R.id.tv_explanations);
        llWordInfo = findViewById(R.id.ll_word_info);
        iconWordPlay.setOnClickListener(this);

        llWordInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDictEngResponse != null) {
                    Intent intent = new Intent(mActivity, WordInfoActivity.class);
                    intent.putExtra("DictEngResponse", mDictEngResponse);
                    mActivity.startActivity(intent);
                }
            }
        });

        this.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mOnDismissListener.onDismiss();
            }
        });
    }

    public void showDialog(String sentence) {
        super.showDialog();
        llWords.removeAllViews();
        String[] words = sentence.split(" ");
        for (int i = 0; i < words.length; i++) {
            TextView textView = new TextView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(params);
            textView.setTextSize(24);
            textView.setTextColor(getContext().getResources().getColor(R.color.C_4E4E4E));
            textView.setText(words[i]);
            int finalI = i;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String word = words[finalI].replaceAll(",", "")
                            .replaceAll("\\.", "");
                    mOnWordClickListener.onWordClick(word);
                    TokenResponse tokenResponse = SaveDataUtil.getInstance(mActivity)
                            .getObject("tokenResponse");
                    DictRequest dictRequest = new DictRequest();
                    dictRequest.setToken(tokenResponse.getToken());
                    dictRequest.setLangType("ENG");
                    dictRequest.setKeywordStr(word);
                    mDictLoader.searchWord(dictRequest)
                            .subscribe(new Action1<DictEngResponse>() {
                                @Override
                                public void call(DictEngResponse dictEngResponse) {
                                    if (dictEngResponse.getCode() == 200) {
                                        phoneticEnUrl = dictEngResponse.getEnglishDictEntities().get(0).getPhoneticEnAudio();
                                        mDictEngResponse = dictEngResponse;
                                        String toJson = gson.toJson(dictEngResponse);
                                        llWordsContent.setVisibility(View.GONE);
                                        llWordInfo.setVisibility(View.VISIBLE);
                                        List<DictEngResponse.EnglishDictEntitiesBean> englishDictEntities
                                                = dictEngResponse.getEnglishDictEntities();
                                        if (englishDictEntities.size() > 0) {
                                            tvWord.setText(englishDictEntities.get(0).getWord());
                                            List<DictEngResponse.EnglishDictEntitiesBean.ExplanationsBean>
                                                    explanations = englishDictEntities
                                                    .get(0).getExplanations();
                                            StringBuilder explanation = new StringBuilder();
                                            for (int j = 0; j < explanations.size(); j++) {
                                                String pos = explanations.get(j).getPos();
                                                String meaning = explanations.get(j).getMeaning();
                                                if (j < explanations.size() - 1) {
                                                    explanation.append(j + 1 + "." + pos + "." + meaning + "\n");
                                                } else {
                                                    explanation.append(j + 1 + "." + pos + "." + meaning);
                                                }
                                            }
                                            tvExplanations.setText(explanation.toString());
                                        } else {
                                            iconWordPlay.setVisibility(View.GONE);
                                        }
                                    }else {
                                        Toast.makeText(mActivity,"查词失败，请重试",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            });
                }
            });
            llWords.addView(textView);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.icon_word_play:
                play(phoneticEnUrl);
                break;
            default:
                break;
        }
    }

    public interface OnWordClickListener {

        /**
         * 单词点击查词
         *
         * @param word
         */
        void onWordClick(String word);

    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }

    public interface OnDismissListener {

        void onDismiss();

    }

}
