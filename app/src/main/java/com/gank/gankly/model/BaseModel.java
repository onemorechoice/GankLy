package com.gank.gankly.model;

import rx.Subscriber;

/**
 * Create by LingYan on 2016-07-13
 * Email:137387869@qq.com
 */
public interface BaseModel {

    void fetchDate(int page, int limit, Subscriber subscriber);
}
