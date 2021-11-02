package com.example.webprog26;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduledEventsManager {

    private static final String KEY_EVENT_DAYS = "event_days";

    private static final String KEY_THREE_DAYS_ALARM_TIME = "three_days_alarm_time";
    private static final String KEY_SEVEN_DAYS_ALARM_TIME = "seven_days_alarm_time";
    private static final String KEY_FOURTEEN_DAYS_ALARM_TIME = "fourteen_days_alarm_time";
    private static final String KEY_THIRTY_DAYS_ALARM_TIME = "thirty_days_alarm_time";

    private static final String[] KEYS = new String[]{
            KEY_THREE_DAYS_ALARM_TIME,
            KEY_SEVEN_DAYS_ALARM_TIME,
            KEY_FOURTEEN_DAYS_ALARM_TIME,
            KEY_THIRTY_DAYS_ALARM_TIME
    };

    private static final int THREE_DAYS_INTERVAL = 2;
    private static final int SEVEN_DAYS_INTERVAL = 3;
    private static final int FOURTEEN_DAYS_INTERVAL = 4;
    private static final int THIRTY_DAYS_INTERVAL = 5;


    private static ScheduledEventsManager instance;

    private final Storage storage;

    private final OneShotTaskScheduler scheduler;

    private ScheduledEventsManager() {
        this.storage = new Storage(ContextUtils.getApplicationContext());
        this.scheduler = new OneShotTaskScheduler(ContextUtils.getApplicationContext());
    }

    public static ScheduledEventsManager getInstance() {
        if (instance == null) {
            instance = new ScheduledEventsManager();
        }
        return instance;
    }

    public void onReceive(final Intent intent) {
        if (intent != null) {
            final int intervalInDays = intent.getIntExtra(KEY_EVENT_DAYS, 0);
            log(getClass().getSimpleName() + ".onReceive(): intervalInDays: " + intervalInDays);
        }
    }

    public void maybeSetupAlarms() {
        log(getClass().getSimpleName() + ".maybeSetupAlarms()");

        final long appFirstStartTime = AppLaunchCountHelper.getInstance().getAppFirstStartTime();

        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(appFirstStartTime);

        log("appFirstStartTime: " + getDateFormatted(calendar.getTime()));

        maybeSaveAlarmsTime();

        final List<OneShotTaskScheduler.OneShotTask> tasks = getTasks();

        if (tasks != null) {
            for (final OneShotTaskScheduler.OneShotTask task : tasks) {
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTimeInMillis(task.getTriggerAtMillis());
                log("scheduling alarm at: " + getDateFormatted(calendar1.getTime()));
                scheduler.setOneShotTask(task);
            }
        } else {
            log("tasks is null");
        }
    }

    private void maybeSaveAlarmsTime() {
        final String savedAlarms = storage.getSavedAlarms();
        final boolean hasAlarms = !TextUtils.isEmpty(savedAlarms);
        log(getClass().getSimpleName() + ".maybeSaveAlarmsTime(): hasAlarms" + hasAlarms);
        if (!hasAlarms) {
            try {
                final JSONObject object = new JSONObject();

                for (final String key : KEYS) {
                    object.put(key, getAlarmTime(getTaskDaysInterval(key)));
                }

                storage.saveAlarms(object.toString());
            } catch (JSONException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    private List<OneShotTaskScheduler.OneShotTask> getTasks() {
        final List<OneShotTaskScheduler.OneShotTask> tasks = new ArrayList<>();

        final String savedAlarms = storage.getSavedAlarms();
        if (!TextUtils.isEmpty(savedAlarms)) {
            try {
                final JSONObject object = new JSONObject(savedAlarms);

                for (final String key : KEYS) {
                    final long alarmTime = object.getLong(key);
                    final boolean isAlarmActive = alarmTime > System.currentTimeMillis();

                    if (isAlarmActive) {
                        final OneShotTaskScheduler.OneShotTask task = new OneShotTaskScheduler.OneShotTask(
                                alarmTime, createPendingIntent(getTaskDaysInterval(key)));
                        tasks.add(task);
                    }
                }

                return tasks;

            } catch (JSONException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static PendingIntent createPendingIntent(final int intervalInDays) {

        final Context context = ContextUtils.getApplicationContext();

        final Intent alarmIntent = new Intent(OneShotTaskReceiver.ACTION_ONE_SHOT_TASK);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            alarmIntent.setClass(context, OneShotTaskReceiver.class);
        }

        alarmIntent.putExtra(KEY_EVENT_DAYS, intervalInDays);

        return PendingIntent.getBroadcast(
                context, intervalInDays, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    private static int getTaskDaysInterval(final String key) throws IllegalArgumentException {
        switch (key) {
            case KEY_THREE_DAYS_ALARM_TIME:
                return THREE_DAYS_INTERVAL;
            case KEY_SEVEN_DAYS_ALARM_TIME:
                return SEVEN_DAYS_INTERVAL;
            case KEY_FOURTEEN_DAYS_ALARM_TIME:
                return FOURTEEN_DAYS_INTERVAL;
            case KEY_THIRTY_DAYS_ALARM_TIME:
                return THIRTY_DAYS_INTERVAL;
        }
        throw new IllegalArgumentException("Inappropriate key received");
    }

    private static long getAlarmTime(final int intervalInDays) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(AppLaunchCountHelper.getInstance().getAppFirstStartTime());
        calendar.add(Calendar.MINUTE, intervalInDays);

        log("getAlarmTime() for " + intervalInDays + " days: " + getDateFormatted(calendar.getTime()));

        return calendar.getTimeInMillis();
    }

    private static class Storage extends PreferencesStorage {

        private static final String KEY_ALARMS_TIME = "three_days_alarm_time";

        public Storage(Context context) {
            super(context);
        }

        void saveAlarms(final String alarmsTime) {
            prefs.edit().putString(KEY_ALARMS_TIME, alarmsTime).apply();
        }

        String getSavedAlarms() {
            return prefs.getString(KEY_ALARMS_TIME, null);
        }
    }

    public static void log(String message) {
        Log.i("events_deb", message);
    }

    public static String getDateFormatted(final Date date) {
        String pattern = "yyyy-MM-dd-hh-mm-ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        return simpleDateFormat.format(date);
    }
}
