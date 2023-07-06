package com.boll.audiobook.hear.network.retrofit;

import com.boll.audiobook.hear.network.request.AddRecordRequest;
import com.boll.audiobook.hear.network.request.DurationRequest;
import com.boll.audiobook.hear.network.request.QueryAlbumRequest;
import com.boll.audiobook.hear.network.request.RecentPlayRequest;
import com.boll.audiobook.hear.network.request.SearchRequest;
import com.boll.audiobook.hear.network.response.AlbumDetailResponse;
import com.boll.audiobook.hear.network.response.BaseResponse;
import com.boll.audiobook.hear.network.response.IndexResponse;
import com.boll.audiobook.hear.network.response.QueryAlbumResponse;
import com.boll.audiobook.hear.network.response.ResUrlResponse;
import com.boll.audiobook.hear.network.response.SearchResponse;
import com.boll.audiobook.hear.network.response.UploadTokenResponse;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;
import rx.functions.Func1;

/**
 * 心喜悦接口网络请求
 *
 * @author Created by zoro on 2021/02/02.
 */
public class ListenerLoader extends BaseLoader {

    private static final String TAG = "ListenerLoader";

    private ListenerService mListenerService;

    private static class SingletonHolder {
        private static final ListenerLoader INSTANCE = new ListenerLoader();
    }

    public static ListenerLoader getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private ListenerLoader() {
        mListenerService = ListenerManager.getInstance().create(ListenerService.class);
    }

    /**
     * 获取主页分级数据
     *
     * @return
     */
    public Observable<IndexResponse> getIndexLevelData() {
        return observe(mListenerService.getIndexLevelData())
                .map(new Func1<IndexResponse, IndexResponse>() {
                    @Override
                    public IndexResponse call(IndexResponse response) {
                        return response;
                    }
                });
    }

    /**
     * 搜索
     *
     * @return
     */
    public Observable<SearchResponse> search(SearchRequest request) {
        return observe(mListenerService.search(request))
                .map(new Func1<SearchResponse, SearchResponse>() {
                    @Override
                    public SearchResponse call(SearchResponse response) {
                        return response;
                    }
                });
    }

    /**
     * 查询专辑
     *
     * @return
     */
    public Observable<QueryAlbumResponse> queryAlbum(QueryAlbumRequest request) {
        return observe(mListenerService.queryAlbum(request))
                .map(new Func1<QueryAlbumResponse, QueryAlbumResponse>() {
                    @Override
                    public QueryAlbumResponse call(QueryAlbumResponse response) {
                        return response;
                    }
                });
    }


    /**
     * 获取该专辑下的音频列表数据
     *
     * @param id 专辑id
     * @return
     */
    public Observable<AlbumDetailResponse> getAlbumDetailData(int id) {
        return observe(mListenerService.getAlbumDetailData(id))
                .map(new Func1<AlbumDetailResponse, AlbumDetailResponse>() {
                    @Override
                    public AlbumDetailResponse call(AlbumDetailResponse response) {
                        return response;
                    }
                });
    }

    /**
     * 收藏音频
     *
     * @param id
     * @return
     */
    public Observable<BaseResponse> collectAudio(int id) {
        return observe(mListenerService.collectAudio(id))
                .map(new Func1<BaseResponse, BaseResponse>() {
                    @Override
                    public BaseResponse call(BaseResponse response) {
                        return response;
                    }
                });
    }

    /**
     * 取消收藏音频
     *
     * @param id
     * @return
     */
    public Observable<BaseResponse> cancelCollectAudio(int id) {
        return observe(mListenerService.cancelCollectAudio(id))
                .map(new Func1<BaseResponse, BaseResponse>() {
                    @Override
                    public BaseResponse call(BaseResponse response) {
                        return response;
                    }
                });
    }

    /**
     * 收藏专辑
     *
     * @param id
     * @return
     */
    public Observable<BaseResponse> collectAlbum(int id) {
        return observe(mListenerService.collectAlbum(id))
                .map(new Func1<BaseResponse, BaseResponse>() {
                    @Override
                    public BaseResponse call(BaseResponse response) {
                        return response;
                    }
                });
    }

    /**
     * 取消收藏专辑
     *
     * @param id
     * @return
     */
    public Observable<BaseResponse> cancelCollectAlbum(int id) {
        return observe(mListenerService.cancelCollectAlbum(id))
                .map(new Func1<BaseResponse, BaseResponse>() {
                    @Override
                    public BaseResponse call(BaseResponse response) {
                        return response;
                    }
                });
    }

    /**
     * 资源链接转换为带token链接
     *
     * @param url
     * @return
     */
    public Observable<ResUrlResponse> getDownloadUrl(String url) {
        return observe(mListenerService.getDownloadUrl(url))
                .map(new Func1<ResUrlResponse, ResUrlResponse>() {
                    @Override
                    public ResUrlResponse call(ResUrlResponse response) {
                        return response;
                    }
                });
    }

    /**
     * 添加听音时长
     *
     * @param
     * @return
     */
    public Observable<BaseResponse> addAudioDuration(DurationRequest request) {
        return observe(mListenerService.addAudioDuration(request))
                .map(new Func1<BaseResponse, BaseResponse>() {
                    @Override
                    public BaseResponse call(BaseResponse response) {
                        return response;
                    }
                });
    }

    /**
     * 添加最近播放记录
     *
     * @param
     * @return
     */
    public Observable<BaseResponse> addRecentPlay(RecentPlayRequest request) {
        return observe(mListenerService.addRecentPlay(request))
                .map(new Func1<BaseResponse, BaseResponse>() {
                    @Override
                    public BaseResponse call(BaseResponse response) {
                        return response;
                    }
                });
    }

    /**
     * 获取七牛云上传凭证
     *
     * @param
     * @return
     */
    public Observable<UploadTokenResponse> uploadToken() {
        return observe(mListenerService.uploadToken())
                .map(new Func1<UploadTokenResponse, UploadTokenResponse>() {
                    @Override
                    public UploadTokenResponse call(UploadTokenResponse response) {
                        return response;
                    }
                });
    }

    /**
     * 获取七牛云上传凭证
     *
     * @param
     * @return
     */
    public Observable<BaseResponse> addSoundRecord(AddRecordRequest request) {
        return observe(mListenerService.addSoundRecord(request))
                .map(new Func1<BaseResponse, BaseResponse>() {
                    @Override
                    public BaseResponse call(BaseResponse response) {
                        return response;
                    }
                });
    }

    public interface ListenerService {

        //获取主页分级数据
        @POST("album/index")
        Observable<IndexResponse> getIndexLevelData();

        //搜索
        @POST("album/search")
        Observable<SearchResponse> search(@Body SearchRequest request);

        //查询专辑
        @POST("album/queryAlbum")
        Observable<QueryAlbumResponse> queryAlbum(@Body QueryAlbumRequest request);

        //获取该专辑下的音频列表数据
        @POST("album/detail")
        Observable<AlbumDetailResponse> getAlbumDetailData(@Query("id") int id);

        //收藏音频
        @POST("album/collectAudio")
        Observable<BaseResponse> collectAudio(@Query("id") int id);

        //取消收藏音频
        @POST("album/cancelCollectAudio")
        Observable<BaseResponse> cancelCollectAudio(@Query("id") int id);

        //收藏专辑
        @POST("album/collectAlbum")
        Observable<BaseResponse> collectAlbum(@Query("id") int id);

        //取消收藏专辑
        @POST("album/cancelCollectAlbum")
        Observable<BaseResponse> cancelCollectAlbum(@Query("id") int id);

        //资源链接转换为带token链接
        @GET("tool/getDownloadUrl")
        Observable<ResUrlResponse> getDownloadUrl(@Query("url") String url);

        //添加听音时长
        @POST("audioDurationLog/add")
        Observable<BaseResponse> addAudioDuration(@Body DurationRequest request);

        //添加最近播放记录（上传专辑id）
        @POST("recentPlay/add")
        Observable<BaseResponse> addRecentPlay(@Body RecentPlayRequest request);

        //获取七牛云上传凭证
        @POST("userSoundRecord/uploadToken")
        Observable<UploadTokenResponse> uploadToken();

        //添加录音记录
        @POST("userSoundRecord/add")
        Observable<BaseResponse> addSoundRecord(@Body AddRecordRequest request);

    }

}