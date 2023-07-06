package com.boll.audiobook.hear.fragment;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.boll.audiobook.hear.R;

/**
 * 封面界面
 * created by zoro at 2023/5/29
 */
public class CoverFragment extends Fragment {

    private static final String TAG = "CoverFragment";

    private Context mContext;
    private ImageView imgCover;
    private String coverUrl;

    public CoverFragment(Context context,String coverUrl) {
        mContext = context;
        this.coverUrl = coverUrl;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cover, container, false);
        initView(view);
        Glide.with(mContext).load(coverUrl)
                .error(R.mipmap.icon_default)//异常时候显示的图片
                .placeholder(R.mipmap.icon_default)//加载成功前显示的图片
                .fallback(R.mipmap.icon_default)//url为空的时候,显示的图片
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(12)))
                .into(imgCover);
        Log.d(TAG, "coverUrl: " + coverUrl);
        return view;
    }

    private void initView(View view) {
        imgCover = view.findViewById(R.id.img_cover);
    }

}
