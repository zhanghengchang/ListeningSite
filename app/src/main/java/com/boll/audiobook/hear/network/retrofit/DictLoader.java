package com.boll.audiobook.hear.network.retrofit;

import com.boll.audiobook.hear.network.request.DictRequest;
import com.boll.audiobook.hear.network.request.TokenRequest;
import com.boll.audiobook.hear.network.response.DictEngResponse;
import com.boll.audiobook.hear.network.response.TokenResponse;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;
import rx.functions.Func1;

/**
 * 博尔词典接口网络请求
 *
 * @author Created by zoro on 2021/02/02.
 */
public class DictLoader extends BaseLoader {

    private static final String TAG = "DictionaryLoader";

    private DictionaryService mDictionaryService;

    private static class SingletonHolder {
        private static final DictLoader INSTANCE = new DictLoader();
    }

    public static DictLoader getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private DictLoader() {
        mDictionaryService = DictManager.getInstance().create(DictionaryService.class);
    }

    /**
     * 获取鉴权token
     *
     * @return
     */
    public Observable<TokenResponse> authUserInfo(TokenRequest tokenRequest) {
        return observe(mDictionaryService.authUserInfo(tokenRequest))
                .map(new Func1<TokenResponse, TokenResponse>() {
                    @Override
                    public TokenResponse call(TokenResponse tokenResponse) {
                        return tokenResponse;
                    }
                });
    }


    /**
     * 获取鉴权token
     *
     * @return
     */
    public Observable<DictEngResponse> searchWord(DictRequest dictRequest) {
        return observe(mDictionaryService.searchWord(dictRequest))
                .map(new Func1<DictEngResponse, DictEngResponse>() {
                    @Override
                    public DictEngResponse call(DictEngResponse dictEngResponse) {
                        return dictEngResponse;
                    }
                });
    }

    public interface DictionaryService {

        @POST("authUserInfo")
        Observable<TokenResponse> authUserInfo(@Body TokenRequest tokenRequest);

        @POST("search/selectDicData")
        Observable<DictEngResponse> searchWord(@Body DictRequest dictRequest);

    }

}