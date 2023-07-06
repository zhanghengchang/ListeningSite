package com.boll.audiobook.hear.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.boll.audiobook.hear.R;

/**
 * 收藏动画
 * created by zoro at 2023/5/24
 */
public class CollectDialog extends BaseDialog {

    private ImageView animCollect;
    private AnimationDrawable animationDrawable;

    public CollectDialog(@NonNull Context context) {
        super(context, R.style.CustomDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_collect);
        setCanceledOnTouchOutside(false);
        animCollect = findViewById(R.id.anim_collect);
        animationDrawable = (AnimationDrawable) animCollect.getBackground();
    }

    @Override
    public void showDialog() {
        super.showDialog();
        animationDrawable.start();
    }

}
