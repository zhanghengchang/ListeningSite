package com.boll.audiobook.hear.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.boll.audiobook.hear.R;

/**
 * 主界面分级标题
 * created by zoro at 2023/5/11
 */
public class LevelTitle extends LinearLayout {

    private TextView tvLevel;
    private ImageView mImageView;

    private ClickListener mClickListener;

    public LevelTitle(Context context) {
        super(context);
        initView(context, null);
    }

    public LevelTitle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public LevelTitle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.layout_leveltitle, this, true);
        tvLevel = findViewById(R.id.tv_level);
        mImageView = findViewById(R.id.icon_more);

//        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "font/ResourceHanRoundedCN-Regular.ttf");
//        tvLevel.setTypeface(typeface);

        mImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onClick();
            }
        });
    }

    public void setTvLevel(String name) {
        tvLevel.setText(name);
    }

    public void setClickListener(ClickListener clickListener){
        mClickListener = clickListener;
    }

    public interface ClickListener {
        void onClick();
    }

}
