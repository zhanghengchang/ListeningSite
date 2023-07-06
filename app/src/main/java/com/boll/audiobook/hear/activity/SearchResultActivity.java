package com.boll.audiobook.hear.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boll.audiobook.hear.R;
import com.boll.audiobook.hear.fragment.AlbumFragment;
import com.boll.audiobook.hear.fragment.AudioFragment;

public class SearchResultActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iconBack;
    private LinearLayout llContent;
    private LinearLayout llEmpty;
    private TextView tvResult;
    private ImageView iconAlbum;
    private TextView tvAlbum;
    private LinearLayout llAlbum;
    private ImageView iconAudio;
    private TextView tvAudio;
    private LinearLayout llAudio;

    private AlbumFragment albumFragment;
    private AudioFragment audioFragment;

    @Override
    public void setStatus() {

    }

    @Override
    public int initLayout() {
        return R.layout.activity_search_result;
    }

    @Override
    public void initView() {
        iconBack = findViewById(R.id.icon_back);
        llContent = findViewById(R.id.ll_content);
        llEmpty = findViewById(R.id.ll_empty);
        tvResult = findViewById(R.id.tv_result);
        iconAlbum = findViewById(R.id.icon_album);
        tvAlbum = findViewById(R.id.tv_album);
        llAlbum = findViewById(R.id.ll_album);
        iconAudio = findViewById(R.id.icon_audio);
        tvAudio = findViewById(R.id.tv_audio);
        llAudio = findViewById(R.id.ll_audio);
        iconBack.setOnClickListener(this);
        llAlbum.setOnClickListener(this);
        llAudio.setOnClickListener(this);
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        String normValue = intent.getStringExtra("normValue");

        albumFragment = new AlbumFragment(this,normValue);
        audioFragment = new AudioFragment(this,normValue);

        Bundle bundle = new Bundle();
        bundle.putString("normValue", normValue);
        albumFragment.setArguments(bundle);
        audioFragment.setArguments(bundle);

        if (TextUtils.isEmpty(normValue)) {
            llEmpty.setVisibility(View.VISIBLE);
            llContent.setVisibility(View.GONE);
        } else {
            llEmpty.setVisibility(View.GONE);
            llContent.setVisibility(View.VISIBLE);
            tvResult.setText(normValue);
        }

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fl_content,albumFragment)
                .add(R.id.fl_content,audioFragment);
        fragmentTransaction.show(albumFragment);
        fragmentTransaction.hide(audioFragment);
        fragmentTransaction.commit();
    }

    private void updateAlbumOrAudio(View view) {
        llAlbum.setBackgroundResource(R.drawable.bg_search_normal);
        iconAlbum.setImageResource(R.mipmap.icon_album_normal);
        tvAlbum.setTextColor(getResources().getColor(R.color.C_CCCCCC));
        llAudio.setBackgroundResource(R.drawable.bg_search_normal);
        iconAudio.setImageResource(R.mipmap.icon_album_normal);
        tvAudio.setTextColor(getResources().getColor(R.color.C_CCCCCC));

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        if (view.getId() == R.id.ll_album) {
            llAlbum.setBackgroundResource(R.drawable.bg_search_selected);
            iconAlbum.setImageResource(R.mipmap.icon_album_selected);
            tvAlbum.setTextColor(getResources().getColor(R.color.white));
            fragmentTransaction.hide(audioFragment).show(albumFragment);
        } else {
            llAudio.setBackgroundResource(R.drawable.bg_search_selected);
            iconAudio.setImageResource(R.mipmap.icon_album_selected);
            tvAudio.setTextColor(getResources().getColor(R.color.white));
            fragmentTransaction.hide(albumFragment).show(audioFragment);
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.icon_back:
                finish();
                break;
            case R.id.ll_album:
            case R.id.ll_audio:
                updateAlbumOrAudio(view);
                break;
            default:
                break;
        }
    }
}