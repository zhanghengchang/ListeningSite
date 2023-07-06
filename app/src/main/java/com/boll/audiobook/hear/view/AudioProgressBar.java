package com.boll.audiobook.hear.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.boll.audiobook.hear.R;

/**
 * created by zoro at 2023/5/12
 */
public class AudioProgressBar extends LinearLayout {

    private TextView tvCurrentTime;
    private SeekBar mSeekBar;
    private TextView tvTotalTime;

    private OnSeekBarChangeListener mOnSeekBarChangeListener;

    public AudioProgressBar(Context context) {
        super(context);
        initView(context, null);
    }

    public AudioProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public AudioProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.layout_audio_progress, this, true);
        tvCurrentTime = findViewById(R.id.tv_current_time);
        mSeekBar = findViewById(R.id.seekBar);
        tvTotalTime = findViewById(R.id.tv_total_time);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mOnSeekBarChangeListener.onProgressChanged(seekBar,progress,fromUser);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mOnSeekBarChangeListener.onStartTrackingTouch(seekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mOnSeekBarChangeListener.onStopTrackingTouch(seekBar);
            }
        });
    }

    public void setEnable(boolean enabled){
        mSeekBar.setEnabled(enabled);
    }

    public void setCurrentTime(String time) {
        tvCurrentTime.setText(time);
    }

    public void setTotalTime(String time) {
        tvTotalTime.setText(time);
    }

    public void setProgress(int progress) {
        mSeekBar.setProgress(progress);
    }

    public int getProgress() {
        return mSeekBar.getProgress();
    }

    public void setMax(int duration){
        mSeekBar.setMax(duration);
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener){
        mOnSeekBarChangeListener = onSeekBarChangeListener;
    }

    public interface OnSeekBarChangeListener {

        void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser);

        void onStartTrackingTouch(SeekBar seekBar);

        void onStopTrackingTouch(SeekBar seekBar);

    }

}
