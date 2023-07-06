package com.boll.audiobook.hear.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.boll.audiobook.hear.R;
import com.boll.audiobook.hear.activity.LocalPlayActivity;
import com.boll.audiobook.hear.activity.PlayActivity;
import com.boll.audiobook.hear.adapter.AudioAdapter;
import com.boll.audiobook.hear.adapter.LocalAudioAdapter;
import com.boll.audiobook.hear.entity.AudioBean;
import com.boll.audiobook.hear.entity.LocalAudioBean;
import com.boll.audiobook.hear.network.response.AudioResponse;
import com.boll.audiobook.hear.service.PlayService;

import java.util.List;

/**
 * 本地音频选集
 * created by zoro at 2023/5/26
 */
public class LocalAudioListDialog extends BaseDialog {

    private Context mContext;
    private Activity mActivity;

    private TextView tvResCount;
    private RecyclerView audioListView;

    private int position;
    private List<LocalAudioBean> mLocalAudioBeans;
    private LocalAudioAdapter mLocalAudioAdapter;

    public LocalAudioListDialog(@NonNull Context context, Activity activity, List<LocalAudioBean> localAudioBeans,int position) {
        super(context, R.style.CustomDialog);
        mContext = context;
        mActivity = activity;
        mLocalAudioBeans = localAudioBeans;
        this.position = position;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_local_audio_list);
        initView();
        refreshList(position);
    }

    public void refreshList(int position) {
        for (int i = 0; i < mLocalAudioBeans.size(); i++) {
            if (i == position) {
                mLocalAudioBeans.get(i).setPlaying(true);
            } else {
                mLocalAudioBeans.get(i).setPlaying(false);
            }
        }
        mLocalAudioAdapter.notifyDataSetChanged();
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
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) (dm.heightPixels * 0.9));
        }
        tvResCount = findViewById(R.id.tv_resCount);
        audioListView = findViewById(R.id.audioListView);

        tvResCount.setText("播放列表（" + mLocalAudioBeans.size() + "个音频）");

        RecyclerView.LayoutManager manager = new LinearLayoutManager(mContext);
        audioListView.setLayoutManager(manager);

        mLocalAudioAdapter = new LocalAudioAdapter(mLocalAudioBeans);
        audioListView.setAdapter(mLocalAudioAdapter);

        mLocalAudioAdapter.setOnClickListener(new LocalAudioAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                //切换音频播放
                LocalPlayActivity activity = (LocalPlayActivity) mActivity;
                activity.playAudio(position);
            }
        });
    }

}
