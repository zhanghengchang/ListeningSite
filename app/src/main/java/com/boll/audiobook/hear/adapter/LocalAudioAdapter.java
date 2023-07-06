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

import com.boll.audiobook.hear.R;
import com.boll.audiobook.hear.entity.AudioBean;
import com.boll.audiobook.hear.entity.LocalAudioBean;

import java.util.List;

/**
 * created by zoro at 2023/5/12
 */
public class LocalAudioAdapter extends RecyclerView.Adapter<LocalAudioAdapter.ViewHolder> {

    private Context mContext;

    private List<LocalAudioBean> mLocalAudioBeans;

    private OnClickListener mOnClickListener;

    private AnimationDrawable playAnimation;

    public LocalAudioAdapter(List<LocalAudioBean> localAudioBeans) {
        mLocalAudioBeans = localAudioBeans;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_local_audio, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        LocalAudioBean localAudioBean = mLocalAudioBeans.get(position);
        holder.tvNum.setText(String.valueOf(localAudioBean.getNum()));
        holder.tvAudioName.setText(localAudioBean.getTitle());
        if (localAudioBean.isPlaying()) {
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
    }

    @Override
    public int getItemCount() {
        return mLocalAudioBeans.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llAudio;
        TextView tvNum;
        TextView tvAudioName;
        ImageView iconPlaying;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            llAudio = itemView.findViewById(R.id.ll_audio);
            tvNum = itemView.findViewById(R.id.tv_num);
            tvAudioName = itemView.findViewById(R.id.tv_audio_name);
            iconPlaying = itemView.findViewById(R.id.icon_playing);
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public interface OnClickListener {

        void onClick(int position);

    }

}
