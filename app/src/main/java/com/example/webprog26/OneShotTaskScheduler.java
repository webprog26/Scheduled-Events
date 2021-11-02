package com.example.webprog26;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;

public class OneShotTaskScheduler {

    private final AlarmManager alarmManager;

    public OneShotTaskScheduler(final Context context) {
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void setOneShotTask(final OneShotTask task) {
        ScheduledEventsManager.log(getClass().getSimpleName() + "setOneShotTask() isActive: " + task.isActive);
        alarmManager.cancel(task.pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC, task.triggerAtMillis, task.pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC, task.triggerAtMillis, task.pendingIntent);
        }
    }

    public static class OneShotTask {

        private final long triggerAtMillis;
        private final PendingIntent pendingIntent;

        private final boolean isActive;

        public OneShotTask(long triggerAtMillis, PendingIntent pendingIntent) {
            this.triggerAtMillis = triggerAtMillis;
            this.pendingIntent = pendingIntent;
            this.isActive = triggerAtMillis > System.currentTimeMillis();
        }

        public long getTriggerAtMillis() {
            return triggerAtMillis;
        }
    }
}
