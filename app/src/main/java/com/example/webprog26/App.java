package com.example.webprog26;

import android.app.Application;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ContextUtils.setApplicationContext(this);
        AppLaunchCountHelper.getInstance().maybeSaveAppFirstStartTime();
    }
}
