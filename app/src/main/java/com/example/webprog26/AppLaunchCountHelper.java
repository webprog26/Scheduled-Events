package com.example.webprog26;

import android.content.Context;

public class AppLaunchCountHelper {

    private static AppLaunchCountHelper instance;

    private final Storage storage;

    private AppLaunchCountHelper() {
        this.storage = new Storage(ContextUtils.getApplicationContext());
    }

    public void maybeSaveAppFirstStartTime() {
        storage.maybeSaveAppFirstStartTime();
    }

    public long getAppFirstStartTime() {
        return storage.getAppFirstStartTime();
    }

    public static AppLaunchCountHelper getInstance() {
        if (instance == null) {
            instance = new AppLaunchCountHelper();
        }
        return instance;
    }

    private static class Storage extends PreferencesStorage {

        private static final String KEY_APP_FIRST_START_TIME = "app_first_start_time";

        public Storage(Context context) {
            super(context);
        }

        void maybeSaveAppFirstStartTime() {
            final long appFirstStartTime = getAppFirstStartTime();
            if (appFirstStartTime == 0) {
                prefs.edit().putLong(KEY_APP_FIRST_START_TIME, System.currentTimeMillis()).apply();
            }
        }

        long getAppFirstStartTime() {
            return prefs.getLong(KEY_APP_FIRST_START_TIME, 0);
        }
    }
}
