package com.boll.audiobook.hear.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.boll.audiobook.hear.activity.PlayActivity;
import com.boll.audiobook.hear.adapter.AudioAdapter;
import com.boll.audiobook.hear.entity.AudioBean;
import com.boll.audiobook.hear.network.response.AudioResponse;
import com.boll.audiobook.hear.service.PlayService;

import java.util.List;

/**
 * 听力场音频选集
 * created by zoro at 2023/5/26
 */
public class AudioListDialog extends BaseDialog implements PlayService.OnCurrentPlayIdListener {

    private Context mContext;
    private Activity mActivity;

    private TextView tvResCount;
    private RecyclerView audioListView;

    private List<AudioBean> mAudioBeans;
    private List<AudioResponse> mAudioList;
    private AudioAdapter mAudioAdapter;

    private PlayService mPlayService;

    public AudioListDialog(@NonNull Context context, Activity activity, List<AudioBean> audioBeans,
                           List<AudioResponse> audioList) {
        super(context, R.style.CustomDialog);
        mContext = context;
        mActivity = activity;
        mAudioBeans = audioBeans;
        mAudioList = audioList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_audio_list);
        initView();
        mPlayService = PlayService.getInstance();

        for (int i = 0; i < mAudioList.size(); i++) {
            int id = mAudioList.get(i).getId();
            if (id == mPlayService.currentId) {
                mAudioBeans.get(i).setPlaying(true);
            } else {
                mAudioBeans.get(i).setPlaying(false);
            }
        }
        mAudioAdapter.notifyDataSetChanged();

        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mPlayService.removeOnCurrentPlayIdListener(AudioListDialog.this);
            }
        });
    }

    public void showDialog(){
        this.show();
        mPlayService.addOnCurrentPlayIdListener(this);
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
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) (dm.heightPixels * 0.8));
        }
        tvResCount = findViewById(R.id.tv_resCount);
        audioListView = findViewById(R.id.audioListView);

        tvResCount.setText("播放列表（" + mAudioBeans.size() + "个音频）");

        RecyclerView.LayoutManager manager = new LinearLayoutManager(mContext);
        audioListView.setLayoutManager(manager);

        mAudioAdapter = new AudioAdapter(mAudioBeans, 1);
        audioListView.setAdapter(mAudioAdapter);

        mAudioAdapter.setOnClickListener(new AudioAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                PlayActivity activity = (PlayActivity) mActivity;
                activity.playNext(position);
            }

            @Override
            public void onDownload(int position) {
                //不需要下载
            }
        });
    }

    @Override
    public void onCurrentPlayId(int currentId) {
        for (int i = 0; i < mAudioList.size(); i++) {
            int id = mAudioList.get(i).getId();
            if (id == currentId) {
                mAudioBeans.get(i).setPlaying(true);
            } else {
                mAudioBeans.get(i).setPlaying(false);
            }
        }
        mAudioAdapter.notifyDataSetChanged();
    }
}
