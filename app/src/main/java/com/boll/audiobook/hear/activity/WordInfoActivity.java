package com.boll.audiobook.hear.activity;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boll.audiobook.hear.network.response.DictEngResponse;
import com.boll.audiobook.hear.R;

import java.io.IOException;
import java.util.List;

public class WordInfoActivity extends BaseActivity implements View.OnClickListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private Context mContext;

    private DictEngResponse mDictEngResponse;
    private ImageView iconBack;
    private TextView tvWord;
    private TextView tvPhoneticEn;//英式音标
    private LinearLayout llPhoneticEn;
    private TextView tvPhoneticUs;//美式音标
    private LinearLayout llPhoneticUs;
    private TextView tvExplanations;
    private TextView tvSynonyms;//同义词、近义词
    private LinearLayout llSynonyms;
    private TextView tvDeriveList;//派生词、联想词
    private LinearLayout llDeriveList;
    private TextView tvPhrases;//短语词组
    private LinearLayout llPhrases;

    private String phoneticEnUrl;//英式音标发音路径
    private String phoneticUsUrl;//美式音标发音路径

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

    @Override
    public void setStatus() {

    }

    @Override
    public int initLayout() {
        return R.layout.activity_word_info;
    }

    @Override
    public void initView() {
        mContext = this;
        iconBack = findViewById(R.id.icon_back);
        tvWord = findViewById(R.id.tv_word);
        tvPhoneticEn = findViewById(R.id.tv_phoneticEn);
        llPhoneticEn = findViewById(R.id.ll_phoneticEn);
        tvPhoneticUs = findViewById(R.id.tv_phoneticUs);
        llPhoneticUs = findViewById(R.id.ll_phoneticUs);
        tvExplanations = findViewById(R.id.tv_explanations);
        tvSynonyms = findViewById(R.id.tv_synonyms);
        llSynonyms = findViewById(R.id.ll_synonyms);
        tvDeriveList = findViewById(R.id.tv_deriveList);
        llDeriveList = findViewById(R.id.ll_deriveList);
        tvPhrases = findViewById(R.id.tv_phrases);
        llPhrases = findViewById(R.id.ll_phrases);
        iconBack.setOnClickListener(this);
        llPhoneticEn.setOnClickListener(this);
        llPhoneticUs.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mDictEngResponse = (DictEngResponse) getIntent().getSerializableExtra("DictEngResponse");
        DictEngResponse.EnglishDictEntitiesBean bean = mDictEngResponse.getEnglishDictEntities().get(0);

        phoneticEnUrl = bean.getPhoneticEnAudio();
        phoneticUsUrl = bean.getPhoneticUsAudio();

        initIfNecessary();

        String word = bean.getWord();
        tvWord.setText(word);

        String phoneticEn = bean.getPhoneticEn();
        if (!TextUtils.isEmpty(phoneticEn)) {
            tvPhoneticEn.setText("英/" + phoneticEn);
        } else {
            llPhoneticEn.setVisibility(View.GONE);
        }

        String phoneticUs = bean.getPhoneticUs();
        if (!TextUtils.isEmpty(phoneticUs)) {
            tvPhoneticUs.setText("美/" + phoneticUs);
        } else {
            llPhoneticUs.setVisibility(View.GONE);
        }

        List<DictEngResponse.EnglishDictEntitiesBean.ExplanationsBean> explanations = bean.getExplanations();
        if (explanations != null && explanations.size() > 0) {
            StringBuilder explanation = new StringBuilder();
            for (int i = 0; i < explanations.size(); i++) {
                String pos = explanations.get(i).getPos();
                String meaning = explanations.get(i).getMeaning();
                if (i < explanations.size() - 1) {
                    explanation.append(i + 1 + "." + pos + "." + meaning + "\n");
                } else {
                    explanation.append(i + 1 + "." + pos + "." + meaning);
                }

            }
            tvExplanations.setText(explanation);
        } else {
            tvExplanations.setVisibility(View.GONE);
        }

        List<String> synonyms = bean.getSynonyms();
        if (synonyms != null && synonyms.size() > 0) {
            StringBuilder synonym = new StringBuilder();
            for (int i = 0; i < synonyms.size(); i++) {
                String s = synonyms.get(i);
                if (i < synonyms.size() - 1) {
                    if ((i + 1) % 2 == 0) {
                        synonym.append(s + "\n");
                    } else {
                        synonym.append(s + "\t");
                    }
                } else {
                    synonym.append(s);
                }
            }
            tvSynonyms.setText(synonym);
        } else {
            llSynonyms.setVisibility(View.GONE);
        }

        List<String> deriveList = bean.getDeriveList();
        if (deriveList != null && deriveList.size() > 0) {
            StringBuilder derive = new StringBuilder();
            for (int i = 0; i < deriveList.size(); i++) {
                String s = deriveList.get(i);
                if (i < deriveList.size() - 1) {
                    if ((i + 1) % 2 == 0) {
                        derive.append(s + "\n");
                    } else {
                        derive.append(s + "\t");
                    }
                } else {
                    derive.append(s);
                }
            }
            tvDeriveList.setText(derive);
        } else {
            llDeriveList.setVisibility(View.GONE);
        }

        List<String> phrases = bean.getPhrases();
        if (phrases != null && phrases.size() > 0) {
            StringBuilder phrase = new StringBuilder();
            for (int i = 0; i < phrases.size(); i++) {
                String s = phrases.get(i);
                if (i < phrases.size() - 1) {
                    phrase.append(s + "\n");
                } else {
                    phrase.append(s);
                }
            }
            tvPhrases.setText(phrase);
        } else {
            llPhrases.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.icon_back:
                finish();
                break;
            case R.id.ll_phoneticEn:
                play(phoneticEnUrl);
                break;
            case R.id.ll_phoneticUs:
                play(phoneticUsUrl);
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