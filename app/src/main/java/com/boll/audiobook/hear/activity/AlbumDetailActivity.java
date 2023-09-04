package com.boll.audiobook.hear.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.boll.audiobook.hear.R;
import com.boll.audiobook.hear.adapter.AudioAdapter;
import com.boll.audiobook.hear.entity.AudioBean;
import com.boll.audiobook.hear.room.DownloadAudio;
import com.boll.audiobook.hear.network.response.AlbumDetailResponse;
import com.boll.audiobook.hear.network.response.AlbumResponse;
import com.boll.audiobook.hear.network.response.AudioResponse;
import com.boll.audiobook.hear.network.response.BaseResponse;
import com.boll.audiobook.hear.network.retrofit.ListenerLoader;
import com.boll.audiobook.hear.room.DownloadDao;
import com.boll.audiobook.hear.room.DownloadDb;
import com.boll.audiobook.hear.service.PlayService;
import com.boll.audiobook.hear.utils.Const;
import com.boll.audiobook.hear.utils.TokenUtil;
import com.boll.audiobook.hear.view.CollectDialog;
import com.boll.audiolib.util.DownloadUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

public class AlbumDetailActivity extends BaseActivity implements View.OnClickListener, PlayService.OnCurrentPlayIdListener {

    private static final String TAG = "AlbumPlayActivity";

    private Context mContext;

    private ImageView iconBack;
    private TextView tvTitle;
    private ImageView iconCollect;
    private ImageView imgAlbum;
    private TextView tvResCount;
    private TextView tvResListCount;
    private ImageView iconPlayAll;
    private TextView tvDownloadAll;
    private RecyclerView recyclerViewAlbum;
    private LinearLayout llError;

    private int id;//专辑id
    private ListenerLoader mListenerLoader;
    private boolean isCollect;//是否是收藏状态
    private CollectDialog mCollectDialog;

    private AlbumResponse albumResponse;
    private List<AudioResponse> audioList;
    private List<AudioBean> audioBeans;

    private AudioAdapter mAudioAdapter;

    private Handler handler = new Handler();

    private PlayService mPlayService;
    private LinearLayout llContent;

    private DownloadDb downloadDb;
    private DownloadDao downloadDao;

    @Override
    public void setStatus() {

    }

    @Override
    public int initLayout() {
        return R.layout.activity_album_detail;
    }

    @Override
    public void initView() {
        mContext = this;
        iconBack = findViewById(R.id.icon_back);
        tvTitle = findViewById(R.id.tv_title);
        iconCollect = findViewById(R.id.icon_collect);
        imgAlbum = findViewById(R.id.img_album);
        tvResCount = findViewById(R.id.tv_resCount);
        tvResListCount = findViewById(R.id.tv_resListCount);
        iconPlayAll = findViewById(R.id.icon_play_all);
        tvDownloadAll = findViewById(R.id.tv_downloadAll);
        recyclerViewAlbum = findViewById(R.id.recyclerView_album);
        llError = findViewById(R.id.ll_error);
        llContent = findViewById(R.id.ll_content);
        iconBack.setOnClickListener(this);
        iconCollect.setOnClickListener(this);
        iconPlayAll.setOnClickListener(this);
        tvDownloadAll.setOnClickListener(this);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(mContext);
        recyclerViewAlbum.setLayoutManager(manager);
        recyclerViewAlbum.setFocusable(false);
    }

    @Override
    public void initData() {
//        String oaid = TextUtils.isEmpty(DeviceID.getOAID()) ? "OAID" : DeviceID.getOAID();
//        String ua = "client/" + HeadUtil.getAppVersion() + "/-1/" + HeadUtil.getAndroidVersion()
//                + "/tinglibao/" + HeadUtil.getLocalMacAddressFromIp() + "/-1/-1"
//                + "/" + getPackageName() + "/" + HeadUtil.getScreenHeight(this) + "/"
//                + HeadUtil.getScreenWidth(this) + "/"
//                + UUIDHexGenerator.getInstance().generateToken() + "/"
//                + HeadUtil.getNetWorkType(this) + "/" + HeadUtil.getSerialNumber() + "/"
//                + TokenUtil.getUserLoginToken(this) + "/" + oaid;
        Const.UA = TokenUtil.getUserLoginToken(this);

        Intent intent = getIntent();
        id = intent.getIntExtra("id", 1);
        String albumName = intent.getStringExtra("albumName");
        String imgUrl = intent.getStringExtra("imgUrl");

        mListenerLoader = ListenerLoader.getInstance();
        mCollectDialog = new CollectDialog(mContext);

        downloadDb = Room.databaseBuilder(mContext,DownloadDb.class,"download-db").build();
        downloadDao = downloadDb.downloadDao();

        tvTitle.setText(albumName);
        Glide.with(mContext).load(imgUrl)
                .error(R.mipmap.icon_default)//异常时候显示的图片
                .placeholder(R.mipmap.icon_default)//加载成功前显示的图片
                .fallback(R.mipmap.icon_default)//url为空的时候,显示的图片
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(12)))
                .into(imgAlbum);

        mPlayService = PlayService.getInstance();

        mLoadingDialog.showDialog();
        //获取该专辑的数据
        mListenerLoader.getAlbumDetailData(id).subscribe(new Action1<AlbumDetailResponse>() {
            @Override
            public void call(AlbumDetailResponse albumDetailResponse) {
                if (albumDetailResponse.getCode() != 0) {
                    mLoadingDialog.dismiss();
                    Log.e(TAG, "code: " + albumDetailResponse.getCode());
                    Log.e(TAG, "msg: " + albumDetailResponse.getMsg());
                    llError.setVisibility(View.VISIBLE);
                    llContent.setVisibility(View.GONE);
                    showToast(albumDetailResponse.getMsg());
                    return;
                }
                albumResponse = albumDetailResponse.getData();

                if (albumResponse.getIsCollect()) {
                    isCollect = true;
                } else {
                    isCollect = false;
                }
                collectAlbumState(isCollect);

                tvResCount.setText(albumResponse.getResCount() + "集");
                tvResListCount.setText("全部播放(" + albumResponse.getResCount() + ")");
                audioList = albumResponse.getAudioList();
                audioBeans = new ArrayList<>();
                addAudioList(mPlayService.currentId);
                mAudioAdapter = new AudioAdapter(audioBeans, 0);
                recyclerViewAlbum.setAdapter(mAudioAdapter);

                mPlayService.addOnCurrentPlayIdListener(AlbumDetailActivity.this);

                mAudioAdapter.setOnClickListener(new AudioAdapter.OnClickListener() {
                    @Override
                    public void onClick(int position) {
                        Intent intent = new Intent(mContext, PlayActivity.class);
                        intent.putExtra("albumId", albumResponse.getId());
                        intent.putExtra("title", audioList.get(position).getTitle());
                        intent.putExtra("coverUrl", audioList.get(position).getCover());
                        intent.putExtra("position", position);
                        intent.putExtra("audioList", (Serializable) albumResponse.getAudioList());//接口的音频列表
                        startActivity(intent);
                    }

                    @Override
                    public void onDownload(int position) {
                        String audioUrl = audioList.get(position).getAudioUrl();
                        String[] split = audioUrl.split("/");
                        String fileName = split[split.length - 1];
                        if (DownloadUtil.existsFile(Const.MP3PATH + fileName)) return;
                        for (int i = 0; i < audioBeans.size(); i++) {
                            if (i == position) {
                                audioBeans.get(i).setDownloadState(2);
                            }
                        }
                        mAudioAdapter.notifyDataSetChanged();
                        //url包含中文需要转码
                        //对路径进行编码 然后替换路径中所有空格 编码之后空格变成“+”而空格的编码表示是“%20” 所以将所有的“+”替换成“%20”就可以了
                        try {
                            audioUrl = URLEncoder.encode(audioUrl, "utf-8").replaceAll("\\+", "%20");
                            //编码之后的路径中的“/”也变成编码的东西了 所有还有将其替换回来 这样才是完整的路径
                            audioUrl = audioUrl.replaceAll("%3A", ":").replaceAll("%2F", "/");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        String finalAudioUrl = audioUrl;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                boolean isDownloaded = DownloadUtil.downloadRes(finalAudioUrl, Const.MP3PATH, fileName);
                                for (int i = 0; i < audioBeans.size(); i++) {
                                    if (i == position) {
                                        if (isDownloaded) {
                                            audioBeans.get(i).setDownloadState(1);
                                            savaDataToSql(i);
                                        } else {
                                            audioBeans.get(i).setDownloadState(0);
                                        }
                                    }
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mAudioAdapter.notifyDataSetChanged();
                                    }
                                });
                                Log.e(TAG, "savePath: " + Const.MP3PATH + fileName + ",isDownloaded: " + isDownloaded);
                            }
                        }).start();
                    }
                });
                mLoadingDialog.dismiss();
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                mLoadingDialog.dismiss();
                showToast("加载失败，请检查网络后重试");
                llError.setVisibility(View.VISIBLE);
                llContent.setVisibility(View.GONE);
            }
        });
    }

    private void addAudioList(int currentId) {
        for (int i = 0; i < audioList.size(); i++) {
            AudioBean audioBean = new AudioBean();
            audioBean.setNum(i + 1);
            audioBean.setAudioName(audioList.get(i).getTitle());
            int duration = audioList.get(i).getDuration();
            if (duration < 60) {
                String durationStr = duration < 10 ? "00:0" + duration : "00:" + duration;
                audioBean.setDuration(durationStr);
            } else {
                int min = duration / 60;
                int sec = duration % 60;
                String minStr = min < 10 ? "0" + min : "" + min;
                String secStr = sec < 10 ? "0" + sec : "" + sec;
                audioBean.setDuration(minStr + ":" + secStr);
            }
            int id = audioList.get(i).getId();
            if (id == currentId) {
                audioBean.setPlaying(true);
            } else {
                audioBean.setPlaying(false);
            }

            String audioUrl = audioList.get(i).getAudioUrl();
            String[] strings = audioUrl.split("\\?");
            if (strings.length == 2) {
                audioUrl = strings[0];
            }
            String[] split = audioUrl.split("/");
            String fileName = split[split.length - 1];
            if (DownloadUtil.existsFile(Const.MP3PATH + fileName)) {
                audioBean.setDownloadState(1);
            } else {
                audioBean.setDownloadState(0);
            }
            audioBeans.add(audioBean);
        }
    }

    /**
     * 更新收藏图标状态
     */
    public void collectAlbumState(boolean isCollect) {
        if (isCollect) {
            iconCollect.setImageResource(R.mipmap.icon_album_collect);
        } else {
            iconCollect.setImageResource(R.mipmap.icon_album_nocollect);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.icon_back:
                finish();
                break;
            case R.id.icon_collect:
                if (!isCollect) {
                    mListenerLoader.collectAlbum(id)
                            .subscribe(new Action1<BaseResponse>() {
                                @Override
                                public void call(BaseResponse baseResponse) {
                                    Log.d(TAG, "collectAlbum: " + baseResponse.toString());
                                }
                            });
                    mCollectDialog.showDialog();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mCollectDialog.dismiss();
                                }
                            });
                        }
                    }, 350);
                    isCollect = true;
                } else {
                    mListenerLoader.cancelCollectAlbum(id)
                            .subscribe(new Action1<BaseResponse>() {
                                @Override
                                public void call(BaseResponse baseResponse) {
                                    Log.d(TAG, "cancelCollectAlbum: " + baseResponse.toString());
                                }
                            });
                    isCollect = false;
                }
                collectAlbumState(isCollect);
                break;
            case R.id.icon_play_all:
                if (albumResponse != null) {
                    Intent intent = new Intent(mContext, PlayActivity.class);
                    intent.putExtra("albumId", albumResponse.getId());
                    intent.putExtra("title", audioList.get(0).getTitle());
                    intent.putExtra("coverUrl", audioList.get(0).getCover());
                    intent.putExtra("position", 0);
                    intent.putExtra("audioList", (Serializable) albumResponse.getAudioList());//接口的音频列表
                    startActivity(intent);
                }
                break;
            case R.id.tv_downloadAll:
                for (int i = 0; i < audioList.size(); i++) {
                    String audioUrl = audioList.get(i).getAudioUrl();
                    String[] split = audioUrl.split("/");
                    String fileName = split[split.length - 1];
                    if (DownloadUtil.existsFile(Const.MP3PATH + fileName)) continue;
                    audioBeans.get(i).setDownloadState(2);
                }
                mAudioAdapter.notifyDataSetChanged();
                for (int i = 0; i < audioList.size(); i++) {
                    String audioUrl = audioList.get(i).getAudioUrl();
                    String[] split = audioUrl.split("/");
                    String fileName = split[split.length - 1];
                    int finalI = i;
                    //url包含中文需要转码
                    //对路径进行编码 然后替换路径中所有空格 编码之后空格变成“+”而空格的编码表示是“%20” 所以将所有的“+”替换成“%20”就可以了
                    try {
                        audioUrl = URLEncoder.encode(audioUrl, "utf-8").replaceAll("\\+", "%20");
                        //编码之后的路径中的“/”也变成编码的东西了 所有还有将其替换回来 这样才是完整的路径
                        audioUrl = audioUrl.replaceAll("%3A", ":").replaceAll("%2F", "/");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String finalAudioUrl = audioUrl;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean isDownloaded = DownloadUtil.downloadRes(finalAudioUrl, Const.MP3PATH, fileName);
                            for (int i = 0; i < audioBeans.size(); i++) {
                                if (i == finalI) {
                                    if (isDownloaded) {
                                        audioBeans.get(i).setDownloadState(1);
                                        savaDataToSql(i);
                                    } else {
                                        audioBeans.get(i).setDownloadState(0);
                                    }
                                }
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mAudioAdapter.notifyDataSetChanged();
                                }
                            });
                            Log.e(TAG, "savePath: " + Const.MP3PATH + fileName + ",isDownloaded: " + isDownloaded);
                        }
                    }).start();
                }
                break;
            default:
                break;
        }
    }

    private void savaDataToSql(int i) {
        AudioResponse audioResponse = audioList.get(i);
        DownloadAudio downloadAudio = new DownloadAudio();
        downloadAudio.setAudioId(audioResponse.getId());
        downloadAudio.setAlbumId(audioResponse.getAlbumId());
        downloadAudio.setTitle(audioResponse.getTitle());
        downloadAudio.setDuration(audioResponse.getDuration());
        downloadAudio.setAudioUrl(audioResponse.getAudioUrl());
        downloadAudio.setCaptionUrl(audioResponse.getCaptionUrl());
        downloadAudio.setCover(audioResponse.getCover());
        downloadAudio.setCollect(audioResponse.getIsCollect());

        downloadDao.insert(downloadAudio);

//        boolean save = downloadAudio.save();
//        if (save) {
//            Log.e(TAG, audioResponse.getTitle() + " 保存数据库成功");
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayService.removeOnCurrentPlayIdListener(this);
    }

    @Override
    public void onCurrentPlayId(int currentId) {
        for (int i = 0; i < audioList.size(); i++) {
            int id = audioList.get(i).getId();
            if (id == currentId) {
                audioBeans.get(i).setPlaying(true);
            } else {
                audioBeans.get(i).setPlaying(false);
            }
        }
        mAudioAdapter.notifyDataSetChanged();
    }
}