package com.example.webprog26;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OneShotTaskReceiver extends BroadcastReceiver {

    static final String ACTION_ONE_SHOT_TASK = "name.iteora.chromium.ACTION_ONE_SHOT_TASK";

    @Override
    public void onReceive(Context context, Intent intent) {
        ScheduledEventsManager.getInstance().onReceive(intent);
    }
}
