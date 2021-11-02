package com.example.webprog26;

import android.content.Context;

public class ContextUtils {

    private static Context applicationContext;

    public static Context getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(Context applicationContext) {
        ContextUtils.applicationContext = applicationContext;
    }
}
