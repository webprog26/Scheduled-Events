package com.example.webprog26;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    static final String KEY_EVENT_DAYS = "event_days";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(v -> {
            scheduleEvent(3);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ScheduledEventsManager.getInstance().maybeSetupAlarms();
    }

    void scheduleEvent(final int intervalInDays) {
        final ComponentName componentName = new ComponentName(this, ScheduledEventsJobService.class);
        final JobInfo.Builder builder = new JobInfo.Builder(intervalInDays, componentName);
        final PersistableBundle bundle = new PersistableBundle();
        bundle.putInt(KEY_EVENT_DAYS, intervalInDays);

        builder.setExtras(bundle)
                .setPersisted(true)
                .setRequiresCharging(false)
                .setMinimumLatency(1000L * 60 * (intervalInDays - 1))
                .setOverrideDeadline((1000L * 60 * intervalInDays));
        final JobScheduler scheduler = getSystemService(JobScheduler.class);
        scheduler.schedule(builder.build());
    }
}