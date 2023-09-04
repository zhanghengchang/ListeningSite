package com.boll.audiobook.hear.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.boll.audiobook.hear.R;
import com.boll.audiobook.hear.activity.LocalPlayActivity;
import com.boll.audiobook.hear.activity.MyWorkPlayActivity;
import com.boll.audiobook.hear.utils.SaveDataUtil;

/**
 * created by zoro at 2023/6/9
 */
public class SettingDialog extends BaseDialog implements View.OnClickListener {

    private Activity mActivity;

    private Context mContext;

    private ImageView iconClose;
    private LinearLayout llRepeat;
    private LinearLayout llCaption;
    private TextView tvRepeat1;
    private TextView tvRepeat2;
    private TextView tvRepeat3;
    private TextView tvRepeat4;
    private TextView tvRepeat5;
    private TextView tvInterval1;
    private TextView tvInterval2;
    private TextView tvInterval3;
    private TextView tvInterval4;
    private TextView tvInterval5;
    private Switch swAutoNext;
    private TextView tvOriginal;
    private TextView tvBilingual;
    private TextView tvTranslation;
    private TextView tvOrder;
    private TextView tvCycle;
    private TextView tvRandom;
    private TextView tvSpeed05;
    private TextView tvSpeed06;
    private TextView tvSpeed07;
    private TextView tvSpeed08;
    private TextView tvSpeed09;
    private TextView tvSpeed1;
    private TextView tvSpeed125;
    private TextView tvSpeed15;
    private TextView tvClose;
    private TextView tvClose1;
    private TextView tvClose2;
    private TextView tvClose3;
    private TextView tvClose4;
    private TextView tvClose5;
    private TextView tvClose6;
    private TextView tvClose7;

    private int repeatCount = 2;//复读播放次数
    private int intervalTime = 2;//复读间隔时间
    private boolean autoNext = false;//自动播放下一句
    private int captionType = 1;//1：原文，2：双语，3：译文
    private int playMode = 1;//1：顺序播放，2：单曲循环，3：随机播放
    private float playSpeed = 1.0f;
    private int timingClose = 1;//定时关闭类型

    private OnDismissListener mOnDismissListener;

    public SettingDialog(@NonNull Context context, Activity activity) {
        super(context, R.style.CustomDialog);
        mContext = context;
        mActivity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_setting);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        initView();
    }

    private void initView() {
        // 获取屏幕信息
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);

        // 设置弹框位置
        Window window = this.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        window.setAttributes(params);
        window.setGravity(Gravity.BOTTOM);

        Dialog dialog = this;
        if (dialog != null) {
            // 设置弹框大小
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dm.heightPixels);
        }

        iconClose = findViewById(R.id.icon_close);
        llRepeat = findViewById(R.id.ll_repeat);
        llCaption = findViewById(R.id.ll_caption);
        tvRepeat1 = findViewById(R.id.tv_repeat1);
        tvRepeat2 = findViewById(R.id.tv_repeat2);
        tvRepeat3 = findViewById(R.id.tv_repeat3);
        tvRepeat4 = findViewById(R.id.tv_repeat4);
        tvRepeat5 = findViewById(R.id.tv_repeat5);
        tvInterval1 = findViewById(R.id.tv_interval1);
        tvInterval2 = findViewById(R.id.tv_interval2);
        tvInterval3 = findViewById(R.id.tv_interval3);
        tvInterval4 = findViewById(R.id.tv_interval4);
        tvInterval5 = findViewById(R.id.tv_interval5);
        swAutoNext = findViewById(R.id.sw_auto_next);
        tvOriginal = findViewById(R.id.tv_original);
        tvBilingual = findViewById(R.id.tv_bilingual);
        tvTranslation = findViewById(R.id.tv_translation);
        tvOrder = findViewById(R.id.tv_order);
        tvCycle = findViewById(R.id.tv_cycle);
        tvRandom = findViewById(R.id.tv_random);
        tvSpeed05 = findViewById(R.id.tv_speed0_5);
        tvSpeed06 = findViewById(R.id.tv_speed0_6);
        tvSpeed07 = findViewById(R.id.tv_speed0_7);
        tvSpeed08 = findViewById(R.id.tv_speed0_8);
        tvSpeed09 = findViewById(R.id.tv_speed0_9);
        tvSpeed1 = findViewById(R.id.tv_speed1);
        tvSpeed125 = findViewById(R.id.tv_speed1_25);
        tvSpeed15 = findViewById(R.id.tv_speed1_5);
        tvClose = findViewById(R.id.tv_close);
        tvClose1 = findViewById(R.id.tv_close1);
        tvClose2 = findViewById(R.id.tv_close2);
        tvClose3 = findViewById(R.id.tv_close3);
        tvClose4 = findViewById(R.id.tv_close4);
        tvClose5 = findViewById(R.id.tv_close5);
        tvClose6 = findViewById(R.id.tv_close6);
        tvClose7 = findViewById(R.id.tv_close7);

        iconClose.setOnClickListener(this);
        tvRepeat1.setOnClickListener(this);
        tvRepeat2.setOnClickListener(this);
        tvRepeat3.setOnClickListener(this);
        tvRepeat4.setOnClickListener(this);
        tvRepeat5.setOnClickListener(this);
        tvInterval1.setOnClickListener(this);
        tvInterval2.setOnClickListener(this);
        tvInterval3.setOnClickListener(this);
        tvInterval4.setOnClickListener(this);
        tvInterval5.setOnClickListener(this);
        tvOriginal.setOnClickListener(this);
        tvBilingual.setOnClickListener(this);
        tvTranslation.setOnClickListener(this);
        tvOrder.setOnClickListener(this);
        tvCycle.setOnClickListener(this);
        tvRandom.setOnClickListener(this);
        tvSpeed05.setOnClickListener(this);
        tvSpeed06.setOnClickListener(this);
        tvSpeed07.setOnClickListener(this);
        tvSpeed08.setOnClickListener(this);
        tvSpeed09.setOnClickListener(this);
        tvSpeed1.setOnClickListener(this);
        tvSpeed125.setOnClickListener(this);
        tvSpeed15.setOnClickListener(this);
        tvClose.setOnClickListener(this);
        tvClose1.setOnClickListener(this);
        tvClose2.setOnClickListener(this);
        tvClose3.setOnClickListener(this);
        tvClose4.setOnClickListener(this);
        tvClose5.setOnClickListener(this);
        tvClose6.setOnClickListener(this);
        tvClose7.setOnClickListener(this);

        if (mActivity instanceof LocalPlayActivity
                || mActivity instanceof MyWorkPlayActivity) {
            llRepeat.setVisibility(View.GONE);
            llCaption.setVisibility(View.GONE);
        }

        swAutoNext.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                autoNext = isChecked;
            }
        });

        int count = SaveDataUtil.getInstance(mContext).getInt("repeatCount", 2);
        View view1 = null;
        if (count == 1) {
            view1 = tvRepeat1;
        } else if (count == 2) {
            view1 = tvRepeat2;
        } else if (count == 3) {
            view1 = tvRepeat3;
        } else if (count == 4) {
            view1 = tvRepeat4;
        } else if (count == 5) {
            view1 = tvRepeat5;
        }
        setRepeat(view1);

        int intervalTime = SaveDataUtil.getInstance(mContext).getInt("intervalTime", 2);
        View view2 = null;
        if (intervalTime == 0) {
            view2 = tvInterval1;
        } else if (intervalTime == 1) {
            view2 = tvInterval2;
        } else if (intervalTime == 2) {
            view2 = tvInterval3;
        } else if (intervalTime == 3) {
            view2 = tvInterval4;
        } else if (intervalTime == 4) {
            view2 = tvInterval5;
        }
        setInterval(view2);

        boolean autoNext = SaveDataUtil.getInstance(mContext).getBoolean("autoNext", false);
        swAutoNext.setChecked(autoNext);

        int captionType = SaveDataUtil.getInstance(mContext).getInt("captionType", 1);
        View view3 = null;
        if (captionType == 1) {
            view3 = tvOriginal;
        } else if (captionType == 2) {
            view3 = tvBilingual;
        } else if (captionType == 3) {
            view3 = tvTranslation;
        }
        setCaption(view3);

        int playMode = SaveDataUtil.getInstance(mContext).getInt("playMode", 1);
        View view4 = null;
        if (playMode == 1) {
            view4 = tvOrder;
        } else if (playMode == 2) {
            view4 = tvCycle;
        } else if (playMode == 3) {
            view4 = tvRandom;
        }
        setPlayMode(view4);

        float playSpeed = SaveDataUtil.getInstance(mContext).getFloat("playSpeed", 1.0f);
        View view5 = null;
        if (playSpeed == 0.5f) {
            view5 = tvSpeed05;
        } else if (playSpeed == 0.6f) {
            view5 = tvSpeed06;
        } else if (playSpeed == 0.7f) {
            view5 = tvSpeed07;
        } else if (playSpeed == 0.8f) {
            view5 = tvSpeed08;
        } else if (playSpeed == 0.9f) {
            view5 = tvSpeed09;
        } else if (playSpeed == 1.0f) {
            view5 = tvSpeed1;
        } else if (playSpeed == 1.25f) {
            view5 = tvSpeed125;
        } else if (playSpeed == 1.5f) {
            view5 = tvSpeed15;
        }
        setSpeed(view5);

        int timingClose = SaveDataUtil.getInstance(mContext).getInt("timingClose", 1);
        View view6 = null;
        if (timingClose == 1) {
            view6 = tvClose;
        } else if (timingClose == 2) {
            view6 = tvClose1;
        } else if (timingClose == 3) {
            view6 = tvClose2;
        } else if (timingClose == 4) {
            view6 = tvClose3;
        } else if (timingClose == 5) {
            view6 = tvClose4;
        } else if (timingClose == 6) {
            view6 = tvClose5;
        } else if (timingClose == 7) {
            view6 = tvClose6;
        } else if (timingClose == 8) {
            view6 = tvClose7;
        }
        setTimingClose(view6);
    }

    /**
     * 设置复读次数
     *
     * @param view
     */
    private void setRepeat(View view) {
        tvRepeat1.setTextColor(mContext.getResources().getColor(R.color.C_666666));
        tvRepeat2.setTextColor(mContext.getResources().getColor(R.color.C_666666));
        tvRepeat3.setTextColor(mContext.getResources().getColor(R.color.C_666666));
        tvRepeat4.setTextColor(mContext.getResources().getColor(R.color.C_666666));
        tvRepeat5.setTextColor(mContext.getResources().getColor(R.color.C_666666));

        tvRepeat1.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_normal));
        tvRepeat2.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_normal));
        tvRepeat3.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_normal));
        tvRepeat4.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_normal));
        tvRepeat5.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_normal));

        ((TextView) view).setTextColor(mContext.getResources().getColor(R.color.white));
        view.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_seleted));

        switch (view.getId()) {
            case R.id.tv_repeat1:
                repeatCount = 1;
                break;
            case R.id.tv_repeat2:
                repeatCount = 2;
                break;
            case R.id.tv_repeat3:
                repeatCount = 3;
                break;
            case R.id.tv_repeat4:
                repeatCount = 4;
                break;
            case R.id.tv_repeat5:
                repeatCount = 5;
                break;
            default:
                break;
        }
    }

    /**
     * 设置间隔时间
     *
     * @param view
     */
    private void setInterval(View view) {
        tvInterval1.setTextColor(mContext.getResources().getColor(R.color.C_666666));
        tvInterval2.setTextColor(mContext.getResources().getColor(R.color.C_666666));
        tvInterval3.setTextColor(mContext.getResources().getColor(R.color.C_666666));
        tvInterval4.setTextColor(mContext.getResources().getColor(R.color.C_666666));
        tvInterval5.setTextColor(mContext.getResources().getColor(R.color.C_666666));

        tvInterval1.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_normal));
        tvInterval2.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_normal));
        tvInterval3.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_normal));
        tvInterval4.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_normal));
        tvInterval5.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_normal));

        ((TextView) view).setTextColor(mContext.getResources().getColor(R.color.white));
        view.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_seleted));

        switch (view.getId()) {
            case R.id.tv_interval1:
                intervalTime = 0;
                break;
            case R.id.tv_interval2:
                intervalTime = 1;
                break;
            case R.id.tv_interval3:
                intervalTime = 2;
                break;
            case R.id.tv_interval4:
                intervalTime = 3;
                break;
            case R.id.tv_interval5:
                intervalTime = 4;
                break;
            default:
                break;
        }
    }

    /**
     * 设置字幕类型
     *
     * @param view
     */
    private void setCaption(View view) {
        tvOriginal.setTextColor(mContext.getResources().getColor(R.color.C_4E4E4E));
        tvBilingual.setTextColor(mContext.getResources().getColor(R.color.C_4E4E4E));
        tvTranslation.setTextColor(mContext.getResources().getColor(R.color.C_4E4E4E));

        tvOriginal.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_normal));
        tvBilingual.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_normal));
        tvTranslation.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_normal));

        ((TextView) view).setTextColor(mContext.getResources().getColor(R.color.white));
        view.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_seleted));

        switch (view.getId()) {
            case R.id.tv_original:
                captionType = 1;
                break;
            case R.id.tv_bilingual:
                captionType = 2;
                break;
            case R.id.tv_translation:
                captionType = 3;
                break;
            default:
                break;
        }
    }

    /**
     * 设置播放模式
     *
     * @param view
     */
    private void setPlayMode(View view) {
        tvOrder.setTextColor(mContext.getResources().getColor(R.color.C_4E4E4E));
        tvCycle.setTextColor(mContext.getResources().getColor(R.color.C_4E4E4E));
        tvRandom.setTextColor(mContext.getResources().getColor(R.color.C_4E4E4E));

        tvOrder.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_normal));
        tvCycle.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_normal));
        tvRandom.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_normal));

        ((TextView) view).setTextColor(mContext.getResources().getColor(R.color.white));
        view.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_seleted));

        switch (view.getId()) {
            case R.id.tv_order:
                playMode = 1;
                break;
            case R.id.tv_cycle:
                playMode = 2;
                break;
            case R.id.tv_random:
                playMode = 3;
                break;
            default:
                break;
        }
    }

    /**
     * 设置播放倍速
     *
     * @param view
     */
    private void setSpeed(View view) {
        tvSpeed05.setTextColor(mContext.getResources().getColor(R.color.C_4E4E4E));
        tvSpeed06.setTextColor(mContext.getResources().getColor(R.color.C_4E4E4E));
        tvSpeed07.setTextColor(mContext.getResources().getColor(R.color.C_4E4E4E));
        tvSpeed08.setTextColor(mContext.getResources().getColor(R.color.C_4E4E4E));
        tvSpeed09.setTextColor(mContext.getResources().getColor(R.color.C_4E4E4E));
        tvSpeed1.setTextColor(mContext.getResources().getColor(R.color.C_4E4E4E));
        tvSpeed125.setTextColor(mContext.getResources().getColor(R.color.C_4E4E4E));
        tvSpeed15.setTextColor(mContext.getResources().getColor(R.color.C_4E4E4E));

        tvSpeed05.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_normal));
        tvSpeed06.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_normal));
        tvSpeed07.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_normal));
        tvSpeed08.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_normal));
        tvSpeed09.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_normal));
        tvSpeed1.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_normal));
        tvSpeed125.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_normal));
        tvSpeed15.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_normal));

        ((TextView) view).setTextColor(mContext.getResources().getColor(R.color.white));
        view.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_seleted));

        switch (view.getId()) {
            case R.id.tv_speed0_5:
                playSpeed = 0.5f;
                break;
            case R.id.tv_speed0_6:
                playSpeed = 0.6f;
                break;
            case R.id.tv_speed0_7:
                playSpeed = 0.7f;
                break;
            case R.id.tv_speed0_8:
                playSpeed = 0.8f;
                break;
            case R.id.tv_speed0_9:
                playSpeed = 0.9f;
                break;
            case R.id.tv_speed1:
                playSpeed = 1.0f;
                break;
            case R.id.tv_speed1_25:
                playSpeed = 1.25f;
                break;
            case R.id.tv_speed1_5:
                playSpeed = 1.5f;
                break;
            default:
                break;
        }
    }

    /**
     * 设置定时关闭
     *
     * @param view
     */
    private void setTimingClose(View view) {
        tvClose.setTextColor(mContext.getResources().getColor(R.color.C_4E4E4E));
        tvClose1.setTextColor(mContext.getResources().getColor(R.color.C_4E4E4E));
        tvClose2.setTextColor(mContext.getResources().getColor(R.color.C_4E4E4E));
        tvClose3.setTextColor(mContext.getResources().getColor(R.color.C_4E4E4E));
        tvClose4.setTextColor(mContext.getResources().getColor(R.color.C_4E4E4E));
        tvClose5.setTextColor(mContext.getResources().getColor(R.color.C_4E4E4E));
        tvClose6.setTextColor(mContext.getResources().getColor(R.color.C_4E4E4E));
        tvClose7.setTextColor(mContext.getResources().getColor(R.color.C_4E4E4E));

        tvClose.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_normal));
        tvClose1.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_normal));
        tvClose2.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_normal));
        tvClose3.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_normal));
        tvClose4.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_normal));
        tvClose5.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_normal));
        tvClose6.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_normal));
        tvClose7.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_normal));

        ((TextView) view).setTextColor(mContext.getResources().getColor(R.color.white));
        view.setBackground(mContext.getResources().getDrawable(R.drawable.bg_tv_seleted));

        switch (view.getId()) {
            case R.id.tv_close:
                timingClose = 1;
                break;
            case R.id.tv_close1:
                timingClose = 2;
                break;
            case R.id.tv_close2:
                timingClose = 3;
                break;
            case R.id.tv_close3:
                timingClose = 4;
                break;
            case R.id.tv_close4:
                timingClose = 5;
                break;
            case R.id.tv_close5:
                timingClose = 6;
                break;
            case R.id.tv_close6:
                timingClose = 7;
                break;
            case R.id.tv_close7:
                timingClose = 8;
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.icon_close:
                dismiss();
                //保存所选择的设置
                SaveDataUtil.getInstance(mContext).putInt("repeatCount", repeatCount);
                SaveDataUtil.getInstance(mContext).putInt("intervalTime", intervalTime);
                SaveDataUtil.getInstance(mContext).putBoolean("autoNext", autoNext);
                SaveDataUtil.getInstance(mContext).putInt("captionType", captionType);
                SaveDataUtil.getInstance(mContext).putInt("playMode", playMode);
                SaveDataUtil.getInstance(mContext).putFloat("playSpeed", playSpeed);
                SaveDataUtil.getInstance(mContext).putInt("timingClose", timingClose);

                long startTiming = System.currentTimeMillis();//开始计时时间
                SaveDataUtil.getInstance(mContext).putLong("startTiming", startTiming);

                mOnDismissListener.onDismiss();
                break;
            case R.id.tv_repeat1:
            case R.id.tv_repeat2:
            case R.id.tv_repeat3:
            case R.id.tv_repeat4:
            case R.id.tv_repeat5:
                setRepeat(view);
                break;
            case R.id.tv_interval1:
            case R.id.tv_interval2:
            case R.id.tv_interval3:
            case R.id.tv_interval4:
            case R.id.tv_interval5:
                setInterval(view);
                break;
            case R.id.tv_original:
            case R.id.tv_bilingual:
            case R.id.tv_translation:
                setCaption(view);
                break;
            case R.id.tv_order:
            case R.id.tv_cycle:
            case R.id.tv_random:
                setPlayMode(view);
                break;
            case R.id.tv_speed0_5:
            case R.id.tv_speed0_6:
            case R.id.tv_speed0_7:
            case R.id.tv_speed0_8:
            case R.id.tv_speed0_9:
            case R.id.tv_speed1:
            case R.id.tv_speed1_25:
            case R.id.tv_speed1_5:
                setSpeed(view);
                break;
            case R.id.tv_close:
            case R.id.tv_close1:
            case R.id.tv_close2:
            case R.id.tv_close3:
            case R.id.tv_close4:
            case R.id.tv_close5:
            case R.id.tv_close6:
            case R.id.tv_close7:
                setTimingClose(view);
                break;
            default:
                break;
        }
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }

    public interface OnDismissListener {

        void onDismiss();

    }

}
