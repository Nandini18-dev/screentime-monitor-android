package com.nandini.screentime;

import android.app.Application;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.nandini.screentime.worker.DailyRolloverWorker;

import java.util.concurrent.TimeUnit;

public class ScreenTimeApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        schedulePeriodicRollover();
    }

    private void schedulePeriodicRollover() {
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                DailyRolloverWorker.class, 1, TimeUnit.DAYS)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "daily_rollover",
                ExistingPeriodicWorkPolicy.KEEP,
                request);
    }
}
