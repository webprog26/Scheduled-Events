package com.example.webprog26;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class IteoraBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ScheduledEventsManager.getInstance().maybeSetupAlarms();
    }
}