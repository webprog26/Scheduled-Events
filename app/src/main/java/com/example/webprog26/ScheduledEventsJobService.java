package com.example.webprog26;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

public class ScheduledEventsJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        final PersistableBundle bundle = params.getExtras();
        if (bundle.containsKey(MainActivity.KEY_EVENT_DAYS)) {
            final int days = bundle.getInt(MainActivity.KEY_EVENT_DAYS);
            Log.i("events_deb", getClass().getSimpleName() + ".onStartJob(): " + days);
        }
        Toast.makeText(getApplicationContext(), "onStartJob", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
