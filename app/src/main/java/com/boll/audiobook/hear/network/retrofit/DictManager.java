package com.boll.audiobook.hear.network.retrofit;

import com.boll.audiobook.hear.utils.Const;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionSpec;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit管理类
 * @author Created by zoro on 2021/2/2
 */
public class DictManager {

    private static final int DEFAULT_TIME_OUT = 30;//超时时间 30s
    private static final int DEFAULT_READ_TIME_OUT = 30;//读写超时时间 30s
    private Retrofit mRetrofit;

    public static Interceptor HeaderInterceptorPut() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Request.Builder builder = request.newBuilder();
                //下面这个header是为了解决 报错： java.net.ProtocolException: unexpected end of stream
                builder.addHeader("Connection","close")
                        .addHeader("Accept-Encoding", "identity");
                return chain.proceed(builder.build());
            }
        };
    }

    private DictManager(){
        // 创建 OKHttpClient
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)//连接超时时间
        .writeTimeout(DEFAULT_READ_TIME_OUT,TimeUnit.SECONDS)//写操作超时时间
        .readTimeout(DEFAULT_READ_TIME_OUT,TimeUnit.SECONDS)//读操作超时时间
        .addInterceptor(HeaderInterceptorPut())
        .connectionSpecs(Arrays.asList(//指定HTTP流量通过的套接字连接的配置
                ConnectionSpec.MODERN_TLS,
                ConnectionSpec.COMPATIBLE_TLS,
                ConnectionSpec.CLEARTEXT));

        // 创建Retrofit
        mRetrofit = new Retrofit.Builder()
                .client(builder.build())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Const.DICTIONARY_BASEURL)
                .build();
    }

    private static class SingletonHolder{
        private static final DictManager INSTANCE = new DictManager();
    }

    /**
     * 获取RetrofitServiceManager
     * @return
     */
    public static DictManager getInstance(){
        return SingletonHolder.INSTANCE;
    }

    /**
     * 获取对应的Service
     * @param service Service 的 class
     * @param <T>
     * @return
     */
    public <T> T create(Class<T> service){
        return mRetrofit.create(service);
    }

}
