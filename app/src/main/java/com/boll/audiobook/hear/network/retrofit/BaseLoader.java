package com.boll.audiobook.hear.network.retrofit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 *
 * 将一些重复的操作提出来，放到父类 以免Loader里每个接口都有重复代码
 * @author  Created by zoro on 2021/02/02.
 * 
 */
public class BaseLoader {

    /**
    *
    * @param observable
    * @param <T>
    * @return
    */
    protected  <T> Observable<T> observe(Observable<T> observable){
        return observable
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
    }

}