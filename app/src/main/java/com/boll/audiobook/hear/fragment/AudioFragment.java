package com.boll.audiobook.hear.fragment;

import static android.view.View.OVER_SCROLL_NEVER;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.boll.audiobook.hear.R;
import com.boll.audiobook.hear.activity.PlayActivity;
import com.boll.audiobook.hear.adapter.AlbumAdapter;
import com.boll.audiobook.hear.entity.AlbumBean;
import com.boll.audiobook.hear.network.request.SearchRequest;
import com.boll.audiobook.hear.network.response.AudioResponse;
import com.boll.audiobook.hear.network.response.SearchResponse;
import com.boll.audiobook.hear.network.retrofit.ListenerLoader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * 搜索音频
 * created by zoro at 2023/6/15
 */
@SuppressLint("ValidFragment")
public class AudioFragment extends Fragment {

    private static final String TAG = "AudioFragment";

    private Context mContext;

    private LinearLayout llEmpty;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private AlbumAdapter mAlbumAdapter;
    private List<AlbumBean> mAlbumBeans;

    private ListenerLoader mListenerLoader;

    private String normValue;

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

    @SuppressLint("ValidFragment")
    public AudioFragment(Context context, String normValue) {
        mContext = context;
        this.normValue = normValue;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio, container, false);
        initView(view);
        initData();
        return view;
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        llEmpty = view.findViewById(R.id.ll_empty);

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
    }

    private void initData() {
        recyclerView.setOverScrollMode(OVER_SCROLL_NEVER);//去掉阴影效果
        GridLayoutManager manager = new GridLayoutManager(mContext, 2);
        recyclerView.setLayoutManager(manager);

        mAlbumBeans = new ArrayList<>();
        loadData();
    }

    private void loadData() {
        SearchRequest request = new SearchRequest();
        request.setKeyword(normValue);
        request.setLimit(limit);
        request.setPage(1);
        request.setType(2);

        mListenerLoader = ListenerLoader.getInstance();
        mListenerLoader.search(request).subscribe(new Action1<SearchResponse>() {
            @Override
            public void call(SearchResponse searchResponse) {
                if (searchResponse.getCode() != 0) {
                    Log.e(TAG, "code: " + searchResponse.getCode());
                    Log.e(TAG, "msg: " + searchResponse.getMsg());
                    isEmpty();
                    Toast.makeText(mContext, searchResponse.getMsg(), Toast.LENGTH_SHORT).show();
                    return;
                }
//                String toJson = gson.toJson(searchResponse);
//                Log.d(TAG, "toJson: " + toJson);
                if (searchResponse.getCode() == 0 && searchResponse != null) {
                    SearchResponse.DataBean.AudioListBean audioList = searchResponse
                            .getData().getAudioList();
                    if (audioList != null) {
                        List<AudioResponse> list = audioList.getList();
                        mAlbumBeans.clear();
                        if (list != null && list.size() > 0) {
                            for (int i = 0; i < list.size(); i++) {
                                String cover = list.get(i).getCover();
                                String title = list.get(i).getTitle();

                                AlbumBean albumBean = new AlbumBean();
                                albumBean.setName(title);
                                albumBean.setImgUrl(cover);

                                mAlbumBeans.add(albumBean);
                            }
                            mAlbumAdapter = new AlbumAdapter(mContext, mAlbumBeans);
                            recyclerView.setAdapter(mAlbumAdapter);

                            mAlbumAdapter.setClickListener(new AlbumAdapter.ClickListener() {
                                @Override
                                public void onClick(int position) {
                                    Intent intent = new Intent(mContext, PlayActivity.class);
                                    intent.putExtra("albumId", list.get(position).getAlbumId());
                                    intent.putExtra("title", list.get(position).getTitle());
                                    intent.putExtra("coverUrl", list.get(position).getCover());
                                    intent.putExtra("position", position);
                                    intent.putExtra("audioList", (Serializable) list);//接口的音频列表
                                    startActivity(intent);
                                }
                            });
                        } else {
                            isEmpty();
                        }
                    } else {
                        isEmpty();
                    }
                } else {
                    isEmpty();
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                isEmpty();
                throwable.printStackTrace();
            }
        });
    }

    private void isEmpty() {
        llEmpty.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

}
