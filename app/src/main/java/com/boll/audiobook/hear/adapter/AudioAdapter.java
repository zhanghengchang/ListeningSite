package com.boll.audiobook.hear.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.boll.audiobook.hear.entity.AudioBean;
import com.boll.audiobook.hear.R;

import java.util.List;

/**
 * created by zoro at 2023/5/12
 */
public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder> {

    private Context mContext;

    private List<AudioBean> mAudioBeans;

    private OnClickListener mOnClickListener;

    private int type;//0：专辑页需要下载图标；1：播放页弹框隐藏下载图标

    private AnimationDrawable downloadAnimation;
    private AnimationDrawable playAnimation;

    public AudioAdapter(List<AudioBean> audioBeans, int type) {
        mAudioBeans = audioBeans;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_audio, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        AudioBean audioBean = mAudioBeans.get(position);
        holder.tvNum.setText(String.valueOf(audioBean.getNum()));
        holder.tvAudioName.setText(audioBean.getAudioName());
        holder.tvDuration.setText(audioBean.getDuration());
        if (type == 0) {
            if (audioBean.getDownloadState() == 0) {
                holder.iconDownload.setBackground(mContext.getResources().getDrawable(R.mipmap.icon_download));
            } else if (audioBean.getDownloadState() == 1) {
                holder.iconDownload.setBackground(mContext.getResources().getDrawable(R.mipmap.icon_downloaded));
            } else {
                holder.iconDownload.setBackground(mContext.getResources().getDrawable(R.drawable.anim_download));
                downloadAnimation = (AnimationDrawable) holder.iconDownload.getBackground();
                downloadAnimation.start();
            }
        } else {
            if (audioBean.isPlaying()) {
                holder.tvAudioName.setTextColor(mContext.getResources().getColor(R.color.C_1DCFFF));
            } else {
                holder.tvAudioName.setTextColor(mContext.getResources().getColor(R.color.C_4E4E4E));
            }
            holder.iconDownload.setVisibility(View.GONE);
        }
        if (audioBean.isPlaying()) {
            holder.iconPlaying.setVisibility(View.VISIBLE);
            holder.iconPlaying.setBackground(mContext.getResources().getDrawable(R.drawable.anim_item_play));
            playAnimation = (AnimationDrawable) holder.iconPlaying.getBackground();
            playAnimation.start();
        } else {
            holder.iconPlaying.setVisibility(View.GONE);
        }

        holder.llAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onClick(position);
            }
        });

        holder.iconDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onDownload(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAudioBeans.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llAudio;
        TextView tvNum;
        TextView tvAudioName;
        TextView tvDuration;
        ImageView iconPlaying;
        ImageView iconDownload;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            llAudio = itemView.findViewById(R.id.ll_audio);
            tvNum = itemView.findViewById(R.id.tv_num);
            tvAudioName = itemView.findViewById(R.id.tv_audio_name);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            iconPlaying = itemView.findViewById(R.id.icon_playing);
            iconDownload = itemView.findViewById(R.id.icon_download);
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public interface OnClickListener {

        void onClick(int position);

        void onDownload(int position);
    }

}
