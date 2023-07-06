package com.boll.audiobook.hear.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.boll.audiobook.hear.R;
import com.boll.audiobook.hear.adapter.AlbumAdapter;
import com.boll.audiobook.hear.entity.AlbumBean;
import com.boll.audiobook.hear.network.request.QueryAlbumRequest;
import com.boll.audiobook.hear.network.response.QueryAlbumResponse;
import com.boll.audiobook.hear.network.retrofit.ListenerLoader;
import com.boll.audiobook.hear.view.MarqueeTextView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * 查询专辑界面
 */
public class SubcategoryActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SubcategoryActivity";
    private Context mContext;

    private ImageView iconBack;
    private MarqueeTextView tvTitle;
    private LinearLayout llError;
    private RecyclerView albumListView;

    private AlbumAdapter mAlbumAdapter;

    private ListenerLoader mListenerLoader;
    private int id;//专辑id
    private SmartRefreshLayout refreshLayout;

    private int limit = 20;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == 0x11) {
                refreshLayout.finishRefresh(true);
                limit = 20;
                loadData();
            } else if (msg.what == 0x22) {
                refreshLayout.finishLoadMore(true);
                limit = limit + 20;
                loadData();
            }
            return false;
        }
    });

    @Override
    public void setStatus() {

    }

    @Override
    public int initLayout() {
        return R.layout.activity_subcategory;
    }

    @Override
    public void initView() {
        mContext = this;
        iconBack = findViewById(R.id.icon_back);
        tvTitle = findViewById(R.id.tv_title);
        llError = findViewById(R.id.ll_error);
        refreshLayout = findViewById(R.id.refreshLayout);
        albumListView = findViewById(R.id.albumListView);
        iconBack.setOnClickListener(this);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mHandler.sendEmptyMessageDelayed(0x11,2000);
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mHandler.sendEmptyMessageDelayed(0x22,2000);
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 2);
        albumListView.setLayoutManager(layoutManager);
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        id = intent.getIntExtra("id", 0);
        tvTitle.setText(title);

        mListenerLoader = ListenerLoader.getInstance();
        loadData();
    }

    private void loadData() {
        QueryAlbumRequest request = new QueryAlbumRequest();
        request.setLevelId(id);
        request.setKeyword("");
        request.setLimit(limit);
        request.setPage(1);
        mListenerLoader.queryAlbum(request)
                .subscribe(new Action1<QueryAlbumResponse>() {
                    @Override
                    public void call(QueryAlbumResponse response) {
                        if (response.getCode() != 0) {
                            mLoadingDialog.dismiss();
                            Log.e(TAG, "code: " + response.getCode());
                            Log.e(TAG, "msg: " + response.getMsg());
                            llError.setVisibility(View.VISIBLE);
                            showToast(response.getMsg());
                            return;
                        }
                        List<QueryAlbumResponse.DataBean.ListBean> list = response.getData().getList();
                        List<AlbumBean> albumBeans = new ArrayList<>();
                        for (int i = 0; i < list.size(); i++) {
                            QueryAlbumResponse.DataBean.ListBean listBean = list.get(i);
                            AlbumBean album = new AlbumBean();
                            album.setAlbumId(listBean.getId());
                            album.setName(listBean.getName());
                            album.setImgUrl(listBean.getPicUrl());
                            album.setResCount(String.valueOf(listBean.getResCount()));
                            albumBeans.add(album);
                        }

                        mAlbumAdapter = new AlbumAdapter(mContext, albumBeans);
                        albumListView.setAdapter(mAlbumAdapter);

                        mAlbumAdapter.setClickListener(new AlbumAdapter.ClickListener() {
                            @Override
                            public void onClick(int position) {
                                Intent intent = new Intent(mContext, AlbumDetailActivity.class);
                                intent.putExtra("id", albumBeans.get(position).getAlbumId());//专辑ID传给专辑列表界面
                                intent.putExtra("albumName", albumBeans.get(position).getName());
                                intent.putExtra("imgUrl", albumBeans.get(position).getImgUrl());
                                startActivity(intent);
                            }
                        });
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        showToast("加载失败，请检查网络后重试");
                        llError.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.icon_back:
                finish();
                break;
            default:
                break;
        }
    }

}