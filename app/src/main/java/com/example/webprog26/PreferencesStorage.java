package com.example.webprog26;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public abstract class PreferencesStorage {

    protected final SharedPreferences prefs;

    public PreferencesStorage(final Context context) {
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }
}
