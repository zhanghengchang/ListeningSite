package com.boll.audiobook.hear.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.boll.audiobook.hear.entity.AlbumBean;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.boll.audiobook.hear.R;

import java.util.List;

/**
 * created by zoro at 2023/5/11
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private List<AlbumBean> mAlbumBeans;
    private Context mContext;
    private ClickListener mClickListener;

    public AlbumAdapter(Context context, List<AlbumBean> albumBeans) {
        mContext = context;
        mAlbumBeans = albumBeans;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        AlbumBean albumBean = mAlbumBeans.get(position);
        Glide.with(mContext).load(albumBean.getImgUrl())
                .error(R.mipmap.icon_default)//异常时候显示的图片
                .placeholder(R.mipmap.icon_default)//加载成功前显示的图片
                .fallback(R.mipmap.icon_default)//url为空的时候,显示的图片
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(12)))
                .into(holder.imgAlbum);
        if (TextUtils.isEmpty(albumBean.getResCount())) {
            holder.tvResCount.setVisibility(View.GONE);
        } else {
            holder.tvResCount.setText(albumBean.getResCount() + "集");
        }
        holder.tvAlbum.setText(albumBean.getName());

//        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "font/ResourceHanRoundedCN-Medium.ttf");
//        holder.tvResCount.setTypeface(typeface);
//        holder.tvAlbum.setTypeface(typeface);

        holder.llAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAlbumBeans.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout llAlbum;
        private ImageView imgAlbum;
        private TextView tvResCount;
        private TextView tvAlbum;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            llAlbum = itemView.findViewById(R.id.ll_album);
            imgAlbum = itemView.findViewById(R.id.img_album);
            tvResCount = itemView.findViewById(R.id.tv_resCount);
            tvAlbum = itemView.findViewById(R.id.tv_album);
        }

    }

    public void setClickListener(ClickListener clickListener) {
        mClickListener = clickListener;
    }

    public interface ClickListener {
        void onClick(int position);
    }

}
