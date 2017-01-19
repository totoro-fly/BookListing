package com.totoro_fly.booklisting;

import android.app.Application;
import android.content.Context;

/**
 * Created by totoro-fly on 2017/1/19.
 */

public class MyApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }
}
