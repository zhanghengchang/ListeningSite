package com.boll.audiobook.hear.activity;

import static android.view.View.OVER_SCROLL_NEVER;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.boll.audiobook.hear.R;
import com.boll.audiobook.hear.adapter.AlbumAdapter;
import com.boll.audiobook.hear.contentprovider.DownloadProvider;
import com.boll.audiobook.hear.entity.AlbumBean;
import com.boll.audiobook.hear.network.request.TokenRequest;
import com.boll.audiobook.hear.network.response.AlbumResponse;
import com.boll.audiobook.hear.network.response.IndexResponse;
import com.boll.audiobook.hear.network.response.LevelResponse;
import com.boll.audiobook.hear.network.response.TokenResponse;
import com.boll.audiobook.hear.network.retrofit.DictLoader;
import com.boll.audiobook.hear.network.retrofit.ListenerLoader;
import com.boll.audiobook.hear.utils.Const;
import com.boll.audiobook.hear.utils.HeadUtil;
import com.boll.audiobook.hear.utils.NetworkUtil;
import com.boll.audiobook.hear.utils.SaveDataUtil;
import com.boll.audiobook.hear.utils.TokenUtil;
import com.boll.audiobook.hear.utils.UUIDHexGenerator;
import com.boll.audiobook.hear.view.LevelTitle;
import com.github.gzuliyujiang.oaid.DeviceID;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private Context mContext;
    private ListenerLoader mListenerLoader;
    private DictLoader mDictLoader;
    private ImageView iconSearch;
    private LinearLayout llContent;
    private LinearLayout llError;

    private Gson gson;
    private TextView tvTitle;
    private LinearLayout llDisconnect;
    private ImageView iconRefresh;

    @Override
    public void setStatus() {

    }

    @Override
    public int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        mContext = this;
        tvTitle = findViewById(R.id.tv_title);
        iconSearch = findViewById(R.id.icon_search);
        llContent = findViewById(R.id.ll_content);
        llError = findViewById(R.id.ll_error);
        llDisconnect = findViewById(R.id.ll_disconnect);
        iconRefresh = findViewById(R.id.icon_refresh);
        iconRefresh.setOnClickListener(this);
        iconSearch.setOnClickListener(this);

//        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/ResourceHanRoundedCN-Regular.ttf");
//        tvTitle.setTypeface(typeface);
    }

    @Override
    public void initData() {
        boolean networkConnected = NetworkUtil.isNetworkConnected(this);
        if (networkConnected) {
            loadData();
        } else {
            llDisconnect.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 有网的情况下加载数据
     */
    private void loadData() {
//        String oaid = TextUtils.isEmpty(DeviceID.getOAID()) ? "OAID" : DeviceID.getOAID();
//        String ua = "client/" + HeadUtil.getAppVersion() + "/-1/" + HeadUtil.getAndroidVersion()
//                + "/tinglibao/" + HeadUtil.getLocalMacAddressFromIp() + "/-1/-1"
//                + "/" + getPackageName() + "/" + HeadUtil.getScreenHeight(this) + "/"
//                + HeadUtil.getScreenWidth(this) + "/"
//                + UUIDHexGenerator.getInstance().generateToken() + "/"
//                + HeadUtil.getNetWorkType(this) + "/" + HeadUtil.getSerialNumber() + "/"
//                + TokenUtil.getUserLoginToken(this) + "/" + oaid;
        Const.UA = TokenUtil.getUserLoginToken(this);
        Log.d(TAG, "UA: " + Const.UA);

        XXPermissions.with(this)
                .permission(Permission.READ_MEDIA_IMAGES)
                .permission(Permission.READ_MEDIA_VIDEO)
                .permission(Permission.READ_MEDIA_AUDIO)
                .permission(Permission.RECORD_AUDIO)
                .request(new OnPermissionCallback() {
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
                            Log.d(TAG, "获取权限");
                        }
                    }
                });

        mListenerLoader = ListenerLoader.getInstance();
        mDictLoader = DictLoader.getInstance();

        gson = new GsonBuilder()
                .serializeNulls() //输出null
                .setPrettyPrinting()//格式化输出
                .create();

        mLoadingDialog.showDialog();
        //获取主页数据
        mListenerLoader.getIndexLevelData().subscribe(new Action1<IndexResponse>() {
            @Override
            public void call(IndexResponse indexResponse) {
                if (indexResponse.getCode() != 0) {
                    mLoadingDialog.dismiss();
                    Log.e(TAG, "code: " + indexResponse.getCode());
                    Log.e(TAG, "msg: " + indexResponse.getMsg());
                    llError.setVisibility(View.VISIBLE);
                    showToast(indexResponse.getMsg());
                    return;
                }
                List<LevelResponse> levelList = indexResponse.getData().getLevelList();
                if (levelList != null && levelList.size() > 0) {
                    int size = levelList.size();
                    for (int i = 0; i < size; i++) {
                        LevelResponse levelResponse = levelList.get(i);
                        String levelName = levelResponse.getName();
                        LevelTitle levelTitle = new LevelTitle(mContext);
                        levelTitle.setTvLevel(levelName);
                        levelTitle.setClickListener(new LevelTitle.ClickListener() {
                            @Override
                            public void onClick() {
                                Intent intent = new Intent(mContext, SubcategoryActivity.class);
                                intent.putExtra("title", levelName);
                                intent.putExtra("id", levelResponse.getId());
                                startActivity(intent);
                            }
                        });
                        llContent.addView(levelTitle);

                        List<AlbumResponse> albumResponses = levelResponse.getAlbumList();
                        List<AlbumBean> albumBeans = new ArrayList<>();
                        for (int j = 0; j < albumResponses.size(); j++) {
                            if (j < 4) {
                                AlbumResponse albumResponse = albumResponses.get(j);
                                AlbumBean album = new AlbumBean();
                                album.setAlbumId(albumResponse.getId());
                                album.setName(albumResponse.getName());
                                String picUrl = albumResponse.getPicUrl();
                                try {
                                    picUrl = URLEncoder.encode(picUrl, "utf-8").replaceAll("\\+", "%20");
                                    picUrl = picUrl.replaceAll("%3A", ":").replaceAll("%2F", "/");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                album.setImgUrl(picUrl);
//                                Log.e(TAG, "picUrl: " + picUrl);
                                album.setResCount(String.valueOf(albumResponse.getResCount()));
                                albumBeans.add(album);
                            }
                        }

                        RecyclerView recyclerView = new RecyclerView(mContext);
                        recyclerView.setOverScrollMode(OVER_SCROLL_NEVER);//去掉阴影效果
                        LinearLayout.LayoutParams layoutParams = new LinearLayout
                                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        recyclerView.setLayoutParams(layoutParams);
                        GridLayoutManager manager = new GridLayoutManager(mContext, 2);
                        recyclerView.setLayoutManager(manager);
                        AlbumAdapter albumAdapter = new AlbumAdapter(mContext, albumBeans);
                        recyclerView.setAdapter(albumAdapter);

                        albumAdapter.setClickListener(new AlbumAdapter.ClickListener() {
                            @Override
                            public void onClick(int position) {
                                Intent intent = new Intent(mContext, AlbumDetailActivity.class);
                                intent.putExtra("id", albumBeans.get(position).getAlbumId());//专辑ID传给专辑列表界面
                                intent.putExtra("albumName", albumBeans.get(position).getName());
                                intent.putExtra("imgUrl", albumBeans.get(position).getImgUrl());
                                startActivity(intent);
                            }
                        });
                        llContent.addView(recyclerView);
                    }
                }
                mLoadingDialog.dismiss();
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                mLoadingDialog.dismiss();
                showToast("加载失败，请检查网络后重试");
                llError.setVisibility(View.VISIBLE);
            }
        });

        //获取词典接口的鉴权token
        TokenResponse tokenResponse = SaveDataUtil.getInstance(mContext).getObject("tokenResponse");
        if (tokenResponse != null) {
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis > tokenResponse.getExpireDate()) {
                getToken();
            }
        } else {
            getToken();
        }
    }

    private void getToken() {
        TokenRequest tokenRequest = new TokenRequest();
        String deviceId = "";
        if (!TextUtils.isEmpty(HeadUtil.getIMEIDeviceId(this))) {
            deviceId = HeadUtil.getIMEIDeviceId(this);
        } else {
            deviceId = HeadUtil.getSerialNumber();
        }
        tokenRequest.setDeviceId(deviceId);
        tokenRequest.setAccessKey(Const.ACCESS_KEY);
        mDictLoader.authUserInfo(tokenRequest)
                .subscribe(new Action1<TokenResponse>() {
                    @Override
                    public void call(TokenResponse tokenResponse) {
                        if (tokenResponse.getCode() == 200) {
                            SaveDataUtil.getInstance(mContext).putObject("tokenResponse", tokenResponse);
                            String toJson = gson.toJson(tokenResponse);
                            Log.d(TAG, "tokenResponse: " + toJson);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.icon_search:
                startActivity(new Intent(this, SearchActivity.class));
                break;
            case R.id.icon_refresh:
                boolean networkConnected = NetworkUtil.isNetworkConnected(mContext);
                if (networkConnected) {
                    loadData();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}