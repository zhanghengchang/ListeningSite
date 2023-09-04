package com.boll.audiobook.hear.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.boll.audiolib.view.LrcView;
import com.boll.audiobook.hear.R;
import com.boll.audiobook.hear.service.PlayService;
import com.boll.audiobook.hear.view.SearchWordDialog;


/**
 * 字幕界面
 * created by zoro at 2023/6/19
 */
public class CaptionFragment extends Fragment {

    private static final String TAG = "CaptionFragment";

    private Context mContext;
    public LrcView lrcView;

    public onCreatedListener mOnCreatedListener;

    public CaptionFragment(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_caption, container, false);
        initView(view);
        mOnCreatedListener.onCreated();
        return view;
    }

    private void initView(View view) {
        lrcView = view.findViewById(R.id.lrc_view);
    }

    public void setOnCreatedListener(onCreatedListener onCreatedListener){
        mOnCreatedListener = onCreatedListener;
    }

    public interface onCreatedListener {

        void onCreated();

    }

}
