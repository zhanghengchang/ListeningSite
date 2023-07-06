package com.boll.audiobook.hear.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.boll.audiobook.hear.R;

/**
 * created by zoro at 2023/2/10
 */
public class LoadingDialog extends BaseDialog {

    public LoadingDialog(@NonNull Context context) {
        super(context, R.style.CustomDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        setCanceledOnTouchOutside(false);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void showDialog() {
        super.showDialog();
    }
}
