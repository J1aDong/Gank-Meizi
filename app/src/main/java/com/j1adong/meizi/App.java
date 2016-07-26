package com.j1adong.meizi;

import android.app.Application;
import android.content.Context;

/**
 * Created by J1aDong on 16/7/21.
 */
public class App extends Application {

    private static App mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
    }

    public static Context getAppContext() {
        return mApp;
    }
}
