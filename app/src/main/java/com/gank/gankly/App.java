package com.gank.gankly;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;

import com.facebook.stetho.Stetho;
import com.gank.gankly.data.DaoMaster;
import com.gank.gankly.data.DaoSession;

import cn.sharesdk.framework.ShareSDK;

/**
 * Create by LingYan on 2016-04-01
 */
public class App extends Application {
    public static Context mContext;

    private static SQLiteDatabase db;
    private static final String DB_NAME = "gank.db";
    private static DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        //初始化Chrome Stetho
        Stetho.initializeWithDefaults(this);

        // 初始化ShareSDK
        ShareSDK.initSDK(this);

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, DB_NAME, null);
        db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public static Context getContext() {
        return mContext.getApplicationContext();
    }

    public static Resources getAppResources() {
        return mContext.getApplicationContext().getResources();
    }

    public static int getAppColor(int id) {
        return mContext.getApplicationContext().getResources().getColor(id);
    }

    public static SQLiteDatabase getDatabase() {
        return db;
    }

    public static DaoSession getDaoSession() {
        return daoSession;
    }
}